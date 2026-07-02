package com.dengjx.affairs.enrollment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.assignment.Assignment;
import com.dengjx.affairs.assignment.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.enrollment.dto.EnrollmentRequest;
import com.dengjx.affairs.grade.Grade;
import com.dengjx.affairs.grade.GradeMapper;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.security.UserContextService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    private static final List<String> ACTIVE_STATUSES = List.of("SELECTED", "COMPLETED");

    private final EnrollmentMapper enrollmentMapper;
    private final AssignmentMapper assignmentMapper;
    private final GradeMapper gradeMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;

    public EnrollmentService(
            EnrollmentMapper enrollmentMapper,
            AssignmentMapper assignmentMapper,
            GradeMapper gradeMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this.enrollmentMapper = enrollmentMapper;
        this.assignmentMapper = assignmentMapper;
        this.gradeMapper = gradeMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> available(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        String sql = """
                SELECT a.djx_AssignmentId13,
                       c.djx_CourseCode13,
                       c.djx_CourseName13,
                       c.djx_Credit13,
                       c.djx_Hours13,
                       c.djx_AssessmentType13,
                       cl.djx_ClassName13,
                       t.djx_Tname13,
                       a.djx_AcademicYear13,
                       a.djx_Semester13,
                       a.djx_CourseType13,
                       a.djx_Capacity13,
                       ta.djx_SelectedCount13
                FROM Dengjx_TeachingAssignments13 a
                JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = a.djx_CourseId13
                JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
                JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
                JOIN V_Dengjx_TeacherAssignments13 ta ON ta.djx_AssignmentId13 = a.djx_AssignmentId13
                WHERE a.djx_EnrollmentOpen13 = TRUE
                  AND NOT EXISTS (
                      SELECT 1
                      FROM Dengjx_Enrollments13 e
                      WHERE e.djx_StudentId13 = ?
                        AND e.djx_AssignmentId13 = a.djx_AssignmentId13
                        AND e.djx_Status13 IN ('SELECTED', 'COMPLETED')
                  )
                ORDER BY a.djx_AcademicYear13, a.djx_Semester13, c.djx_CourseCode13
                """;
        return jdbcTemplate.queryForList(sql, studentId);
    }

    public Enrollment enroll(Long userId, Long assignmentId) {
        Long studentId = userContextService.getStudentId(userId);
        Assignment assignment = requireAssignment(assignmentId);
        if (!Boolean.TRUE.equals(assignment.getEnrollmentOpen())) {
            throw new BusinessException("该课程未开放选课");
        }
        ensureCapacity(assignment);
        return createOrRestore(studentId, assignmentId, "SELECTED");
    }

    public Enrollment studentDrop(Long userId, Long assignmentId) {
        Long studentId = userContextService.getStudentId(userId);
        Enrollment enrollment = activeEnrollment(studentId, assignmentId);
        if (enrollment == null) {
            throw new BusinessException("未找到有效选课记录");
        }
        if (hasGrade(enrollment.getEnrollmentId())) {
            throw new BusinessException("已录入成绩的课程不能退课");
        }
        return drop(enrollment);
    }

    public List<Map<String, Object>> mine(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        String sql = """
                SELECT e.djx_EnrollmentId13,
                       e.djx_Status13,
                       e.djx_SelectedAt13,
                       e.djx_DroppedAt13,
                       c.djx_CourseCode13,
                       c.djx_CourseName13,
                       c.djx_Credit13,
                       t.djx_Tname13,
                       a.djx_AcademicYear13,
                       a.djx_Semester13,
                       a.djx_CourseType13
                FROM Dengjx_Enrollments13 e
                JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
                JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = a.djx_CourseId13
                JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
                WHERE e.djx_StudentId13 = ?
                ORDER BY e.djx_SelectedAt13 DESC
                """;
        return jdbcTemplate.queryForList(sql, studentId);
    }

    public List<Map<String, Object>> assignmentStudents(AuthenticatedUser user, Long assignmentId) {
        requireTeacherAssignment(user, assignmentId);
        String sql = """
                SELECT e.djx_EnrollmentId13,
                       e.djx_Status13,
                       s.djx_StudentId13,
                       s.djx_Sno13,
                       s.djx_Sname13,
                       cl.djx_ClassName13,
                       e.djx_SelectedAt13,
                       e.djx_DroppedAt13
                FROM Dengjx_Enrollments13 e
                JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
                JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
                WHERE e.djx_AssignmentId13 = ?
                ORDER BY s.djx_Sno13
                """;
        return jdbcTemplate.queryForList(sql, assignmentId);
    }

    public PageResult<Enrollment> adminList(long page, long size) {
        Page<Enrollment> result = enrollmentMapper.selectPage(
                new Page<>(page, size),
                Wrappers.<Enrollment>lambdaQuery().orderByDesc(Enrollment::getSelectedAt));
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Enrollment adminCreate(EnrollmentRequest request) {
        Assignment assignment = requireAssignment(request.assignmentId());
        ensureCapacity(assignment);
        return createOrRestore(request.studentId(), request.assignmentId(), normalizeStatus(request.status()));
    }

    public Enrollment adminDrop(Long id) {
        Enrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("选课记录不存在");
        }
        return drop(enrollment);
    }

    private Assignment requireAssignment(Long assignmentId) {
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        return assignment;
    }

    private void ensureCapacity(Assignment assignment) {
        Long selectedCount = enrollmentMapper.selectCount(Wrappers.<Enrollment>lambdaQuery()
                .eq(Enrollment::getAssignmentId, assignment.getAssignmentId())
                .in(Enrollment::getStatus, ACTIVE_STATUSES));
        if (selectedCount >= assignment.getCapacity()) {
            throw new BusinessException("课程容量已满");
        }
    }

    private Enrollment createOrRestore(Long studentId, Long assignmentId, String status) {
        Enrollment existing = enrollmentMapper.selectOne(Wrappers.<Enrollment>lambdaQuery()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getAssignmentId, assignmentId));
        if (existing != null) {
            if (ACTIVE_STATUSES.contains(existing.getStatus())) {
                throw new BusinessException("学生已存在有效选课记录");
            }
            existing.setStatus(status);
            existing.setSelectedAt(LocalDateTime.now());
            existing.setDroppedAt("DROPPED".equals(status) ? LocalDateTime.now() : null);
            enrollmentMapper.updateById(existing);
            return existing;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setAssignmentId(assignmentId);
        enrollment.setStatus(status);
        enrollment.setSelectedAt(LocalDateTime.now());
        enrollment.setDroppedAt("DROPPED".equals(status) ? LocalDateTime.now() : null);
        enrollmentMapper.insert(enrollment);
        return enrollment;
    }

    private Enrollment activeEnrollment(Long studentId, Long assignmentId) {
        return enrollmentMapper.selectOne(Wrappers.<Enrollment>lambdaQuery()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getAssignmentId, assignmentId)
                .in(Enrollment::getStatus, ACTIVE_STATUSES));
    }

    private boolean hasGrade(Long enrollmentId) {
        Grade grade = gradeMapper.selectOne(Wrappers.<Grade>lambdaQuery()
                .eq(Grade::getEnrollmentId, enrollmentId));
        return grade != null;
    }

    private Enrollment drop(Enrollment enrollment) {
        enrollment.setStatus("DROPPED");
        enrollment.setDroppedAt(LocalDateTime.now());
        enrollmentMapper.updateById(enrollment);
        return enrollment;
    }

    private void requireTeacherAssignment(AuthenticatedUser user, Long assignmentId) {
        if ("ADMIN".equals(user.role())) {
            return;
        }
        Assignment assignment = requireAssignment(assignmentId);
        Long teacherId = userContextService.getTeacherId(user.userId());
        if (!teacherId.equals(assignment.getTeacherId())) {
            throw new AccessDeniedException("只能查看本人任课课程的选课名单");
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "SELECTED";
        }
        if (!List.of("SELECTED", "DROPPED", "COMPLETED").contains(status)) {
            throw new BusinessException("选课状态不合法");
        }
        return status;
    }
}
