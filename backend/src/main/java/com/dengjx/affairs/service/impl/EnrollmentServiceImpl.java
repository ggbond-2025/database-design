package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.service.EnrollmentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.EnrollmentRequest;
import com.dengjx.affairs.entity.Grade;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.service.EnrollmentSettingService;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final List<String> ACTIVE_STATUSES = List.of("SELECTED", "COMPLETED");
    private static final String ENROLLMENT_CLOSED_MESSAGE = "当前不在有效选课时间范围内！";

    private final EnrollmentMapper enrollmentMapper;
    private final AssignmentMapper assignmentMapper;
    private final GradeMapper gradeMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;
    private final AcademicTermService academicTermService;
    private final EnrollmentSettingService enrollmentSettingService;

    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentMapper enrollmentMapper,
            AssignmentMapper assignmentMapper,
            GradeMapper gradeMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate,
            AcademicTermService academicTermService,
            EnrollmentSettingService enrollmentSettingService) {
        this.enrollmentMapper = enrollmentMapper;
        this.assignmentMapper = assignmentMapper;
        this.gradeMapper = gradeMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
        this.academicTermService = academicTermService;
        this.enrollmentSettingService = enrollmentSettingService;
    }

    public EnrollmentServiceImpl(
            EnrollmentMapper enrollmentMapper,
            AssignmentMapper assignmentMapper,
            GradeMapper gradeMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this(enrollmentMapper, assignmentMapper, gradeMapper, userContextService, jdbcTemplate, new AcademicTermService(), null);
    }

    public List<Map<String, Object>> available(Long userId) {
        ensureGlobalEnrollmentOpen();
        Long studentId = userContextService.getStudentId(userId);
        StudentTermContext context = studentTermContext(studentId);
        autoEnrollRequiredCourses(context);
        String sql = """
                SELECT a.djx_AssignmentId13,
                       c.djx_CourseCode13,
                       c.djx_CourseName13,
                       c.djx_Credit13,
                       c.djx_Hours13,
                       c.djx_AssessmentType13,
                       cl.djx_ClassName13,
                       t.djx_Tname13,
                       tb.djx_BuildingName13,
                       cr.djx_ClassroomName13,
                       tb.djx_BuildingName13 || ' ' || cr.djx_ClassroomName13 AS djx_ClassroomLabel13,
                       a.djx_AcademicYear13,
                       a.djx_Semester13,
                       a.djx_WeekdayOne13,
                       a.djx_StartTimeOne13,
                       a.djx_EndTimeOne13,
                       a.djx_WeekdayTwo13,
                       a.djx_StartTimeTwo13,
                       a.djx_EndTimeTwo13,
                       mc.djx_CourseType13,
                       mc.djx_TargetGrade13,
                       mc.djx_TargetSemester13,
                       a.djx_Capacity13,
                       ta.djx_SelectedCount13
                FROM Dengjx_TeachingAssignments13 a
                JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
                JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
                JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
                JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
                JOIN Dengjx_Classrooms13 cr ON cr.djx_ClassroomId13 = a.djx_ClassroomId13
                JOIN Dengjx_TeachingBuildings13 tb ON tb.djx_BuildingId13 = cr.djx_BuildingId13
                JOIN V_Dengjx_TeacherAssignments13 ta ON ta.djx_AssignmentId13 = a.djx_AssignmentId13
                WHERE a.djx_EnrollmentOpen13 = TRUE
                  AND mc.djx_CourseType13 = 'ELECTIVE'
                  AND a.djx_ClassId13 = ?
                  AND a.djx_AcademicYear13 = ?
                  AND a.djx_Semester13 = ?
                  AND mc.djx_TargetGrade13 = ?
                  AND mc.djx_TargetSemester13 = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM Dengjx_Enrollments13 e
                      WHERE e.djx_StudentId13 = ?
                        AND e.djx_AssignmentId13 = a.djx_AssignmentId13
                        AND e.djx_Status13 IN ('SELECTED', 'COMPLETED')
                  )
                ORDER BY a.djx_AcademicYear13, a.djx_Semester13, c.djx_CourseCode13
                """;
        return jdbcTemplate.queryForList(sql, context.classId(), context.academicYear(), context.semester(), context.grade(), context.semester(), studentId);
    }

    @Transactional
    public Enrollment enroll(Long userId, Long assignmentId) {
        ensureGlobalEnrollmentOpen();
        Long studentId = userContextService.getStudentId(userId);
        Assignment assignment = requireAssignment(assignmentId);
        if (!Boolean.TRUE.equals(assignment.getEnrollmentOpen())) {
            throw new BusinessException("该课程未开放选课");
        }
        Map<String, Object> course = assignmentCourse(assignmentId);
        if (!"ELECTIVE".equals(String.valueOf(course.get("djx_coursetype13")))) {
            throw new BusinessException("必修课由系统自动修习，不能手动选课");
        }
        StudentTermContext context = studentTermContext(studentId);
        if (!assignment.getClassId().equals(context.classId())
                || !context.academicYear().equals(assignment.getAcademicYear())
                || assignment.getSemester() == null
                || assignment.getSemester() != context.semester()
                || numberValue(course.get("djx_targetgrade13")).intValue() != context.grade()
                || numberValue(course.get("djx_targetsemester13")).intValue() != context.semester()) {
            throw new BusinessException("只能选择当前年级学期开放的选修课");
        }
        ensureCapacity(assignment);
        ensureNoScheduleConflict(context, assignment, numberValue(course.get("djx_hours13")).intValue());
        return createOrRestore(studentId, assignmentId, "SELECTED");
    }

    @Transactional
    public Enrollment studentDrop(Long userId, Long assignmentId) {
        ensureGlobalEnrollmentOpen();
        Long studentId = userContextService.getStudentId(userId);
        Enrollment enrollment = activeEnrollment(studentId, assignmentId);
        if (enrollment == null) {
            throw new BusinessException("未找到有效选课记录");
        }
        Map<String, Object> course = assignmentCourse(assignmentId);
        if ("REQUIRED".equals(String.valueOf(course.get("djx_coursetype13")))) {
            throw new BusinessException("必修课不能退课");
        }
        if (hasGrade(enrollment.getEnrollmentId())) {
            throw new BusinessException("已录入成绩的课程不能退课");
        }
        return drop(enrollment);
    }

    public List<Map<String, Object>> mine(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        StudentTermContext context = studentTermContext(studentId);
        autoEnrollRequiredCourses(context);
        String sql = """
                SELECT e.djx_EnrollmentId13,
                       e.djx_AssignmentId13,
                       e.djx_Status13,
                       e.djx_SelectedAt13,
                       e.djx_DroppedAt13,
                       c.djx_CourseCode13,
                       c.djx_CourseName13,
                       c.djx_Credit13,
                       c.djx_Hours13,
                       t.djx_Tname13,
                       tb.djx_BuildingName13,
                       cr.djx_ClassroomName13,
                       tb.djx_BuildingName13 || ' ' || cr.djx_ClassroomName13 AS djx_ClassroomLabel13,
                       a.djx_AcademicYear13,
                       a.djx_Semester13,
                       a.djx_WeekdayOne13,
                       a.djx_StartTimeOne13,
                       a.djx_EndTimeOne13,
                       a.djx_WeekdayTwo13,
                       a.djx_StartTimeTwo13,
                       a.djx_EndTimeTwo13,
                       mc.djx_CourseType13,
                       mc.djx_TargetGrade13,
                       mc.djx_TargetSemester13
                FROM Dengjx_Enrollments13 e
                JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
                JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
                JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
                JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
                JOIN Dengjx_Classrooms13 cr ON cr.djx_ClassroomId13 = a.djx_ClassroomId13
                JOIN Dengjx_TeachingBuildings13 tb ON tb.djx_BuildingId13 = cr.djx_BuildingId13
                WHERE e.djx_StudentId13 = ?
                  AND e.djx_Status13 IN ('SELECTED', 'COMPLETED')
                  AND a.djx_AcademicYear13 = ?
                  AND a.djx_Semester13 = ?
                  AND mc.djx_TargetGrade13 = ?
                  AND mc.djx_TargetSemester13 = ?
                ORDER BY e.djx_SelectedAt13 DESC
                """;
        return jdbcTemplate.queryForList(sql, studentId, context.academicYear(), context.semester(), context.grade(), context.semester());
    }

    public Map<String, Object> schedule(Long userId) {
        List<Map<String, Object>> rows = mine(userId).stream()
                .filter(row -> ACTIVE_STATUSES.contains(String.valueOf(row.get("djx_status13"))))
                .toList();
        return ScheduleGridBuilder.build(rows);
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

    public PageResult<Map<String, Object>> adminList(String keyword, long page, long size) {
        long offset = (page - 1) * size;
        String whereClause = "";
        List<Object> filterArgs = new ArrayList<>();
        if (StringUtils.hasText(keyword)) {
            String likeKeyword = "%" + keyword.trim() + "%";
            whereClause = """
                    WHERE s.djx_sno13 LIKE ?
                       OR s.djx_sname13 LIKE ?
                       OR cl.djx_classname13 LIKE ?
                       OR c.djx_coursecode13 LIKE ?
                       OR c.djx_coursename13 LIKE ?
                       OR mc.djx_coursetype13 LIKE ?
                       OR t.djx_tname13 LIKE ?
                       OR a.djx_academicyear13 LIKE ?
                       OR e.djx_status13 LIKE ?
                    """;
            for (int i = 0; i < 9; i++) {
                filterArgs.add(likeKeyword);
            }
        }
        List<Object> queryArgs = new ArrayList<>(filterArgs);
        queryArgs.add(size);
        queryArgs.add(offset);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT e.djx_enrollmentid13 AS "enrollmentId",
                       e.djx_status13 AS "status",
                       e.djx_selectedat13 AS "selectedAt",
                       e.djx_droppedat13 AS "droppedAt",
                       s.djx_sno13 AS "sno",
                       s.djx_sname13 AS "sname",
                       cl.djx_classname13 AS "className",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       mc.djx_coursetype13 AS "courseType",
                       t.djx_tname13 AS "teacherName",
                       a.djx_academicyear13 AS "academicYear",
                       a.djx_semester13 AS "semester"
                FROM dengjx_enrollments13 e
                JOIN dengjx_students13 s ON s.djx_studentid13 = e.djx_studentid13
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                %s
                ORDER BY e.djx_selectedat13 DESC
                LIMIT ? OFFSET ?
                """.formatted(whereClause), queryArgs.toArray());
        Long total = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM dengjx_enrollments13 e
                JOIN dengjx_students13 s ON s.djx_studentid13 = e.djx_studentid13
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                %s
                """.formatted(whereClause), Long.class, filterArgs.toArray());
        return PageResult.of(rows, total == null ? 0 : total, page, size);
    }

    @Transactional
    public Enrollment adminCreate(EnrollmentRequest request) {
        String status = normalizeStatus(request.status());
        Assignment assignment = requireAssignment(request.assignmentId());
        if (ACTIVE_STATUSES.contains(status)) {
            Map<String, Object> course = assignmentCourse(request.assignmentId());
            StudentTermContext context = studentTermContext(request.studentId());
            ensureAdminEnrollmentMatchesStudentContext(context, assignment, course);
            ensureCapacity(assignment);
            ensureNoScheduleConflict(context, assignment, numberValue(course.get("djx_hours13")).intValue());
        }
        return createOrRestore(request.studentId(), request.assignmentId(), status);
    }

    @Transactional
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
        lockAssignmentForCapacityCheck(assignment.getAssignmentId());
        Long selectedCount = enrollmentMapper.selectCount(Wrappers.<Enrollment>lambdaQuery()
                .eq(Enrollment::getAssignmentId, assignment.getAssignmentId())
                .in(Enrollment::getStatus, ACTIVE_STATUSES));
        if (selectedCount >= assignment.getCapacity()) {
            throw new BusinessException("课程容量已满");
        }
    }

    private void ensureGlobalEnrollmentOpen() {
        if (enrollmentSettingService != null && !enrollmentSettingService.isEnabled()) {
            throw new BusinessException(ENROLLMENT_CLOSED_MESSAGE);
        }
    }

    private void lockAssignmentForCapacityCheck(Long assignmentId) {
        if (jdbcTemplate == null || assignmentId == null) {
            return;
        }
        jdbcTemplate.queryForObject("""
                SELECT djx_assignmentid13
                FROM dengjx_teachingassignments13
                WHERE djx_assignmentid13 = ?
                FOR UPDATE
                """, Long.class, assignmentId);
    }

    private void autoEnrollRequiredCourses(StudentTermContext context) {
        List<Map<String, Object>> requiredAssignments = jdbcTemplate.queryForList("""
                SELECT a.djx_assignmentid13
                FROM dengjx_teachingassignments13 a
                JOIN dengjx_majorcourses13 cplan ON cplan.djx_majorcourseid13 = a.djx_majorcourseid13
                WHERE a.djx_classid13 = ?
                  AND cplan.djx_coursetype13 = 'REQUIRED'
                  AND a.djx_academicyear13 = ?
                  AND a.djx_semester13 = ?
                  AND cplan.djx_targetgrade13 = ?
                  AND cplan.djx_targetsemester13 = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM dengjx_enrollments13 e
                      WHERE e.djx_studentid13 = ?
                        AND e.djx_assignmentid13 = a.djx_assignmentid13
                        AND e.djx_status13 IN ('SELECTED', 'COMPLETED')
                  )
                """, context.classId(), context.academicYear(), context.semester(), context.grade(), context.semester(), context.studentId());
        for (Map<String, Object> row : requiredAssignments) {
            Long assignmentId = ((Number) row.get("djx_assignmentid13")).longValue();
            createOrRestore(context.studentId(), assignmentId, "SELECTED");
        }
    }

    private StudentTermContext studentTermContext(Long studentId) {
        Map<String, Object> row = jdbcTemplate.queryForMap("""
                SELECT djx_studentid13,
                       djx_classid13,
                       djx_admissiondate13
                FROM dengjx_students13
                WHERE djx_studentid13 = ?
                """, studentId);
        LocalDate admissionDate = toLocalDate(row.get("djx_admissiondate13"));
        AcademicTermService.AcademicTerm term = academicTermService.current(admissionDate);
        Long classId = effectiveClassId(
                studentId,
                ((Number) row.get("djx_classid13")).longValue(),
                term);
        return new StudentTermContext(
                ((Number) row.get("djx_studentid13")).longValue(),
                classId,
                term.grade(),
                term.semester(),
                term.academicYear());
    }

    private Long effectiveClassId(Long studentId, Long currentClassId, AcademicTermService.AcademicTerm currentTerm) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT djx_fromclassid13 AS fromclassid,
                       djx_targetclassid13 AS targetclassid,
                       djx_effectiveacademicyear13 AS effectiveacademicyear,
                       djx_effectivesemester13 AS effectivesemester
                FROM dengjx_majortransferapplications13
                WHERE djx_studentid13 = ?
                  AND djx_status13 = 'APPROVED'
                  AND djx_fromclassid13 IS NOT NULL
                  AND djx_effectiveacademicyear13 IS NOT NULL
                  AND djx_effectivesemester13 IS NOT NULL
                ORDER BY djx_reviewedat13 DESC
                LIMIT 1
                """, studentId);
        if (rows.isEmpty()) {
            return currentClassId;
        }
        Map<String, Object> transfer = rows.get(0);
        String effectiveAcademicYear = String.valueOf(transfer.get("effectiveacademicyear"));
        int effectiveSemester = numberValue(transfer.get("effectivesemester")).intValue();
        if (isBefore(currentTerm.academicYear(), currentTerm.semester(), effectiveAcademicYear, effectiveSemester)) {
            return numberValue(transfer.get("fromclassid")).longValue();
        }
        Object targetClassId = transfer.get("targetclassid");
        return targetClassId == null ? currentClassId : numberValue(targetClassId).longValue();
    }

    private boolean isBefore(String academicYear, int semester, String effectiveAcademicYear, int effectiveSemester) {
        int currentStartYear = Integer.parseInt(academicYear.substring(0, 4));
        int effectiveStartYear = Integer.parseInt(effectiveAcademicYear.substring(0, 4));
        if (currentStartYear != effectiveStartYear) {
            return currentStartYear < effectiveStartYear;
        }
        return semester < effectiveSemester;
    }

    private Map<String, Object> assignmentCourse(Long assignmentId) {
        return jdbcTemplate.queryForMap("""
                SELECT mc.djx_coursetype13,
                       mc.djx_targetgrade13,
                       mc.djx_targetsemester13,
                       c.djx_hours13
                FROM dengjx_teachingassignments13 a
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE a.djx_assignmentid13 = ?
                """, assignmentId);
    }

    private void ensureAdminEnrollmentMatchesStudentContext(
            StudentTermContext context,
            Assignment assignment,
            Map<String, Object> course) {
        if (!assignment.getClassId().equals(context.classId())
                || !context.academicYear().equals(assignment.getAcademicYear())
                || assignment.getSemester() == null
                || assignment.getSemester() != context.semester()
                || numberValue(course.get("djx_targetgrade13")).intValue() != context.grade()
                || numberValue(course.get("djx_targetsemester13")).intValue() != context.semester()) {
            throw new BusinessException("只能为学生当前年级学期对应课程创建选课记录");
        }
    }

    private Number numberValue(Object value) {
        if (value instanceof Number number) {
            return number;
        }
        throw new BusinessException("课程年级学期配置不完整");
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        throw new BusinessException("学生入学时间配置不完整");
    }

    private void ensureNoScheduleConflict(StudentTermContext context, Assignment target, int targetHours) {
        List<AssignmentScheduleRules.EffectiveSlot> targetSlots = AssignmentScheduleRules.effectiveSlots(target, targetHours);
        if (targetSlots.isEmpty()) {
            return;
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT a.djx_assignmentid13,
                       a.djx_weekdayone13,
                       a.djx_starttimeone13,
                       a.djx_endtimeone13,
                       a.djx_weekdaytwo13,
                       a.djx_starttimetwo13,
                       a.djx_endtimetwo13,
                       c.djx_hours13
                FROM dengjx_enrollments13 e
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE e.djx_studentid13 = ?
                  AND e.djx_status13 IN ('SELECTED', 'COMPLETED')
                  AND a.djx_assignmentid13 <> ?
                  AND a.djx_academicyear13 = ?
                  AND a.djx_semester13 = ?
                """, context.studentId(), target.getAssignmentId(), context.academicYear(), context.semester());
        for (Map<String, Object> row : rows) {
            Assignment existing = ScheduleGridBuilder.assignmentFromRow(row);
            int existingHours = numberValue(row.get("djx_hours13")).intValue();
            if (AssignmentScheduleRules.hasConflict(targetSlots, AssignmentScheduleRules.effectiveSlots(existing, existingHours))) {
                throw new BusinessException("该时间段已有课程，不能重复选课");
            }
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

    private record StudentTermContext(Long studentId, Long classId, int grade, int semester, String academicYear) {
    }
}
