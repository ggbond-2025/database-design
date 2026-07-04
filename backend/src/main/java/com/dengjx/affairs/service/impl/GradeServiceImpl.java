package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.entity.Grade;
import com.dengjx.affairs.service.GradeService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.dto.GradeRequest;
import com.dengjx.affairs.security.UserContextService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final AssignmentMapper assignmentMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;

    public GradeServiceImpl(
            GradeMapper gradeMapper,
            EnrollmentMapper enrollmentMapper,
            AssignmentMapper assignmentMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this.gradeMapper = gradeMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.assignmentMapper = assignmentMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> studentGrades(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        return jdbcTemplate.queryForList(
                "SELECT * FROM V_Dengjx_StudentGrades13 WHERE djx_StudentId13 = ? ORDER BY djx_AcademicYear13, djx_Semester13",
                studentId);
    }

    public List<Map<String, Object>> teacherAssignmentGrades(Long userId, Long assignmentId) {
        requireTeacherOwnsAssignment(userId, assignmentId);
        String sql = """
                SELECT e.djx_EnrollmentId13,
                       g.djx_GradeId13,
                       s.djx_Sno13,
                       s.djx_Sname13,
                       g.djx_Score13,
                       g.djx_GradedAt13
                FROM Dengjx_Enrollments13 e
                JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
                LEFT JOIN Dengjx_Grades13 g ON g.djx_EnrollmentId13 = e.djx_EnrollmentId13
                WHERE e.djx_AssignmentId13 = ?
                  AND e.djx_Status13 IN ('SELECTED', 'COMPLETED')
                ORDER BY s.djx_Sno13
                """;
        return jdbcTemplate.queryForList(sql, assignmentId);
    }

    @Transactional
    public Grade teacherCreate(Long userId, GradeRequest request) {
        validateScore(request.score());
        Enrollment enrollment = requireGradableEnrollment(request.enrollmentId());
        requireTeacherOwnsAssignment(userId, enrollment.getAssignmentId());
        return createGrade(enrollment, request.score());
    }

    @Transactional
    public Grade teacherUpdate(Long userId, Long id, GradeRequest request) {
        validateScore(request.score());
        Grade grade = getById(id);
        Enrollment enrollment = requireGradableEnrollment(grade.getEnrollmentId());
        requireTeacherOwnsAssignment(userId, enrollment.getAssignmentId());
        grade.setScore(request.score());
        grade.setGradedAt(LocalDateTime.now());
        gradeMapper.updateById(grade);
        return grade;
    }

    public PageResult<Map<String, Object>> adminAssignmentList(long page, long size) {
        long offset = (page - 1) * size;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT a.djx_assignmentid13 AS "assignmentId",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       mc.djx_coursetype13 AS "courseType",
                       cl.djx_classname13 AS "className",
                       t.djx_tname13 AS "teacherName",
                       a.djx_academicyear13 AS "academicYear",
                       a.djx_semester13 AS "semester",
                       COUNT(CASE WHEN e.djx_status13 IN ('SELECTED', 'COMPLETED') THEN e.djx_enrollmentid13 END) AS "studentCount",
                       COUNT(g.djx_gradeid13) AS "gradedCount",
                       ROUND(AVG(g.djx_score13), 2) AS "averageScore"
                FROM dengjx_teachingassignments13 a
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = a.djx_classid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                LEFT JOIN dengjx_enrollments13 e ON e.djx_assignmentid13 = a.djx_assignmentid13
                    AND e.djx_status13 IN ('SELECTED', 'COMPLETED')
                LEFT JOIN dengjx_grades13 g ON g.djx_enrollmentid13 = e.djx_enrollmentid13
                GROUP BY a.djx_assignmentid13,
                         c.djx_coursecode13,
                         c.djx_coursename13,
                         mc.djx_coursetype13,
                         cl.djx_classname13,
                         t.djx_tname13,
                         a.djx_academicyear13,
                         a.djx_semester13
                ORDER BY a.djx_academicyear13 DESC, a.djx_semester13 DESC, cl.djx_classname13, c.djx_coursecode13
                LIMIT ? OFFSET ?
                """, size, offset);
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dengjx_teachingassignments13", Long.class);
        return PageResult.of(rows, total == null ? 0 : total, page, size);
    }

    public List<Map<String, Object>> adminAssignmentGrades(Long assignmentId) {
        requireAssignment(assignmentId);
        return jdbcTemplate.queryForList("""
                SELECT e.djx_enrollmentid13 AS "enrollmentId",
                       e.djx_status13 AS "status",
                       s.djx_sno13 AS "sno",
                       s.djx_sname13 AS "sname",
                       cl.djx_classname13 AS "className",
                       g.djx_gradeid13 AS "gradeId",
                       g.djx_score13 AS "score",
                       g.djx_gradedat13 AS "gradedAt"
                FROM dengjx_enrollments13 e
                JOIN dengjx_students13 s ON s.djx_studentid13 = e.djx_studentid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                LEFT JOIN dengjx_grades13 g ON g.djx_enrollmentid13 = e.djx_enrollmentid13
                WHERE e.djx_assignmentid13 = ?
                  AND e.djx_status13 IN ('SELECTED', 'COMPLETED')
                ORDER BY s.djx_sno13
                """, assignmentId);
    }

    public PageResult<Grade> adminList(long page, long size) {
        Page<Grade> result = gradeMapper.selectPage(
                new Page<>(page, size),
                Wrappers.<Grade>lambdaQuery().orderByDesc(Grade::getGradedAt));
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public Grade adminCreate(GradeRequest request) {
        validateScore(request.score());
        Enrollment enrollment = requireGradableEnrollment(request.enrollmentId());
        return createGrade(enrollment, request.score());
    }

    @Transactional
    public Grade adminUpdate(Long id, GradeRequest request) {
        validateScore(request.score());
        Grade grade = getById(id);
        requireGradableEnrollment(grade.getEnrollmentId());
        grade.setScore(request.score());
        grade.setGradedAt(LocalDateTime.now());
        gradeMapper.updateById(grade);
        return grade;
    }

    @Transactional
    public void adminDelete(Long id) {
        if (gradeMapper.deleteById(id) == 0) {
            throw new BusinessException("成绩不存在");
        }
    }

    private Grade createGrade(Enrollment enrollment, BigDecimal score) {
        Grade existing = gradeMapper.selectOne(Wrappers.<Grade>lambdaQuery()
                .eq(Grade::getEnrollmentId, enrollment.getEnrollmentId()));
        if (existing != null) {
            throw new BusinessException("该选课记录已存在成绩");
        }

        Grade grade = new Grade();
        grade.setEnrollmentId(enrollment.getEnrollmentId());
        grade.setScore(score);
        grade.setGradedAt(LocalDateTime.now());
        gradeMapper.insert(grade);

        enrollment.setStatus("COMPLETED");
        enrollmentMapper.updateById(enrollment);
        return grade;
    }

    private Grade getById(Long id) {
        Grade grade = gradeMapper.selectById(id);
        if (grade == null) {
            throw new BusinessException("成绩不存在");
        }
        return grade;
    }

    private Assignment requireAssignment(Long assignmentId) {
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        return assignment;
    }

    private Enrollment requireGradableEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null) {
            throw new BusinessException("选课记录不存在");
        }
        if (!List.of("SELECTED", "COMPLETED").contains(enrollment.getStatus())) {
            throw new BusinessException("成绩必须关联有效选课记录");
        }
        return enrollment;
    }

    private void requireTeacherOwnsAssignment(Long userId, Long assignmentId) {
        Long teacherId = userContextService.getTeacherId(userId);
        Assignment assignment = requireAssignment(assignmentId);
        if (!teacherId.equals(assignment.getTeacherId())) {
            throw new AccessDeniedException("只能管理本人任课课程的成绩");
        }
    }

    private void validateScore(BigDecimal score) {
        if (score == null || score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("成绩必须在0到100之间");
        }
    }
}
