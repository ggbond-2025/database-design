package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.service.EnrollmentService;
import com.dengjx.affairs.service.EnrollmentSettingService;
import com.dengjx.affairs.service.AssignmentService;
import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.service.GradeService;
import com.dengjx.affairs.service.impl.GradeServiceImpl;
import com.dengjx.affairs.service.impl.LookupServiceImpl;
import com.dengjx.affairs.dto.GradeRequest;
import com.dengjx.affairs.dto.LookupOption;
import com.dengjx.affairs.dto.TeachingEvaluationRequest;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.entity.TeachingEvaluation;
import com.dengjx.affairs.mapper.TeachingEvaluationMapper;
import com.dengjx.affairs.service.TeachingEvaluationService;
import com.dengjx.affairs.service.impl.TeachingEvaluationServiceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;

class BusinessRuleTests {

    @Test
    void studentCannotEnrollWhenAssignmentIsClosed() {
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        GradeMapper gradeMapper = mock(GradeMapper.class);
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(3L);
        assignment.setEnrollmentOpen(false);

        when(assignmentMapper.selectById(3L)).thenReturn(assignment);

        EnrollmentService service = new EnrollmentServiceImpl(
                enrollmentMapper,
                assignmentMapper,
                gradeMapper,
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.enroll(10L, 3L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("未开放选课");
    }

    @Test
    void studentCannotDropWhenGlobalEnrollmentIsClosed() {
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        GradeMapper gradeMapper = mock(GradeMapper.class);
        EnrollmentSettingService enrollmentSettingService = mock(EnrollmentSettingService.class);
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(6L);
        enrollment.setStudentId(1L);
        enrollment.setAssignmentId(3L);
        enrollment.setStatus("SELECTED");

        when(enrollmentSettingService.isEnabled()).thenReturn(false);
        when(enrollmentMapper.selectOne(any())).thenReturn(enrollment);

        EnrollmentService service = new EnrollmentServiceImpl(
                enrollmentMapper,
                assignmentMapper,
                gradeMapper,
                new FakeUserContextService(),
                new FakeEnrollmentJdbcTemplate(),
                new com.dengjx.affairs.service.impl.AcademicTermService(),
                enrollmentSettingService);

        assertThatThrownBy(() -> service.studentDrop(10L, 3L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前不在有效选课时间范围内");
    }

    @Test
    void availableCoursesBlockedWhenGlobalEnrollmentIsClosed() {
        EnrollmentSettingService enrollmentSettingService = mock(EnrollmentSettingService.class);
        when(enrollmentSettingService.isEnabled()).thenReturn(false);

        EnrollmentService service = new EnrollmentServiceImpl(
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                mock(GradeMapper.class),
                new FakeUserContextService(),
                new FakeStudentTermJdbcTemplate(),
                new com.dengjx.affairs.service.impl.AcademicTermService(),
                enrollmentSettingService);

        assertThatThrownBy(() -> service.available(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前不在有效选课时间范围内");
    }

    @Test
    void availableCoursesUseOriginalClassBeforeApprovedMajorTransferEffectiveTerm() {
        EnrollmentSettingService enrollmentSettingService = mock(EnrollmentSettingService.class);
        when(enrollmentSettingService.isEnabled()).thenReturn(true);
        FakePendingTransferJdbcTemplate jdbcTemplate = new FakePendingTransferJdbcTemplate();

        EnrollmentService service = new EnrollmentServiceImpl(
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                mock(GradeMapper.class),
                new FakeUserContextService(),
                jdbcTemplate,
                new com.dengjx.affairs.service.impl.AcademicTermService(),
                enrollmentSettingService);

        service.available(10L);

        assertThat(jdbcTemplate.availableCourseArgs[0]).isEqualTo(11L);
    }

    @Test
    void mineDoesNotReturnDroppedEnrollmentRows() {
        FakeStudentTermJdbcTemplate jdbcTemplate = new FakeStudentTermJdbcTemplate();
        EnrollmentService service = new EnrollmentServiceImpl(
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                mock(GradeMapper.class),
                new FakeUserContextService(),
                jdbcTemplate,
                new com.dengjx.affairs.service.impl.AcademicTermService(),
                mock(EnrollmentSettingService.class));

        service.mine(10L);

        assertThat(jdbcTemplate.lastSql)
                .contains("e.djx_Status13 IN ('SELECTED', 'COMPLETED')");
    }

    @Test
    void enrollmentLocksAssignmentBeforeCapacityCheck() {
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        GradeMapper gradeMapper = mock(GradeMapper.class);
        LockingEnrollmentJdbcTemplate jdbcTemplate = new LockingEnrollmentJdbcTemplate();
        EnrollmentSettingService enrollmentSettingService = mock(EnrollmentSettingService.class);
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(3L);
        assignment.setClassId(1L);
        assignment.setAcademicYear("2025-2026");
        assignment.setSemester(2);
        assignment.setCapacity(30);
        assignment.setEnrollmentOpen(true);

        when(enrollmentSettingService.isEnabled()).thenReturn(true);
        when(assignmentMapper.selectById(3L)).thenReturn(assignment);
        when(enrollmentMapper.selectCount(any())).thenReturn(0L);

        EnrollmentService service = new EnrollmentServiceImpl(
                enrollmentMapper,
                assignmentMapper,
                gradeMapper,
                new FakeUserContextService(),
                jdbcTemplate,
                new FixedAcademicTermService(),
                enrollmentSettingService);

        service.enroll(10L, 3L);

        assertThat(jdbcTemplate.lockedAssignmentId).isEqualTo(3L);
    }

    @Test
    void assignmentRejectsClassScheduleConflict() {
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        Assignment existing = assignment(9L, 20L, 7L, 30L, LocalTime.of(8, 0), LocalTime.of(9, 40));
        when(assignmentMapper.selectList(any())).thenReturn(List.of(), List.of(existing));
        AssignmentService service = new AssignmentServiceImpl(assignmentMapper, new CourseHoursJdbcTemplate(32));

        assertThatThrownBy(() -> service.create(assignmentRequest(1L, 7L, 31L, LocalTime.of(8, 30), LocalTime.of(10, 0))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("班级");
    }

    @Test
    void assignmentRejectsClassroomScheduleConflict() {
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        Assignment existing = assignment(9L, 20L, 7L, 30L, LocalTime.of(8, 0), LocalTime.of(9, 40));
        when(assignmentMapper.selectList(any())).thenReturn(List.of(), List.of(existing));
        AssignmentService service = new AssignmentServiceImpl(assignmentMapper, new CourseHoursJdbcTemplate(32));

        assertThatThrownBy(() -> service.create(assignmentRequest(1L, 8L, 30L, LocalTime.of(8, 30), LocalTime.of(10, 0))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("教室");
    }

    @Test
    void gradeScoreMustBeBetween0And100() {
        GradeService service = new GradeServiceImpl(
                mock(GradeMapper.class),
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.adminCreate(new GradeRequest(1L, BigDecimal.valueOf(101))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("成绩");
    }

    @Test
    void teachingEvaluationRatingMustBeBetweenOneAndFive() {
        TeachingEvaluationService service = new TeachingEvaluationServiceImpl(
                mock(TeachingEvaluationMapper.class),
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.studentCreate(10L, new TeachingEvaluationRequest(1L, 6, "")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("评价等级");
    }

    @Test
    void teachingEvaluationRequiresCompletedEnrollment() {
        TeachingEvaluationMapper evaluationMapper = mock(TeachingEvaluationMapper.class);
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(5L);
        enrollment.setStudentId(1L);
        enrollment.setStatus("SELECTED");

        when(enrollmentMapper.selectById(5L)).thenReturn(enrollment);

        TeachingEvaluationService service = new TeachingEvaluationServiceImpl(
                evaluationMapper,
                enrollmentMapper,
                mock(AssignmentMapper.class),
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.studentCreate(10L, new TeachingEvaluationRequest(5L, 5, "课程讲解清晰")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("完成");
    }

    @Test
    void teachingEvaluationCannotBeSubmittedTwice() {
        TeachingEvaluationMapper evaluationMapper = mock(TeachingEvaluationMapper.class);
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(8L);
        enrollment.setStudentId(1L);
        enrollment.setStatus("COMPLETED");

        when(enrollmentMapper.selectById(8L)).thenReturn(enrollment);
        when(evaluationMapper.selectOne(any())).thenReturn(new TeachingEvaluation());

        TeachingEvaluationService service = new TeachingEvaluationServiceImpl(
                evaluationMapper,
                enrollmentMapper,
                mock(AssignmentMapper.class),
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.studentCreate(10L, new TeachingEvaluationRequest(8L, 4, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已评价");
    }

    @Test
    void majorCourseLookupIncludesCourseHoursMetadata() {
        JdbcTemplate jdbcTemplate = new FakeLookupJdbcTemplate(List.of(Map.of(
                "value", 12L,
                "label", "软件工程 / CS101 / 数据库原理 / 必修 / 大1上学期",
                "hours", 64)));

        List<LookupOption> options = new LookupServiceImpl(jdbcTemplate).majorCourseOptions();

        assertThat(options).hasSize(1);
        assertThat(options.get(0).meta()).containsEntry("hours", 64);
    }

    private static class FakeUserContextService extends UserContextService {

        FakeUserContextService() {
            super(null);
        }

        @Override
        public Long getStudentId(Long userId) {
            return 1L;
        }

        @Override
        public Long getTeacherId(Long userId) {
            return 1L;
        }
    }

    private static class FakeLookupJdbcTemplate extends JdbcTemplate {

        private final List<Map<String, Object>> rows;

        FakeLookupJdbcTemplate(List<Map<String, Object>> rows) {
            this.rows = rows;
        }

        @Override
        public List<Map<String, Object>> queryForList(String sql) {
            return rows;
        }
    }

    private static class FakeEnrollmentJdbcTemplate extends JdbcTemplate {

        @Override
        public Map<String, Object> queryForMap(String sql, Object... args) {
            return Map.of("djx_coursetype13", "ELECTIVE", "djx_targetgrade13", 1, "djx_targetsemester13", 2, "djx_hours13", 32);
        }
    }

    private static class LockingEnrollmentJdbcTemplate extends JdbcTemplate {

        private Long lockedAssignmentId;

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            if (sql.contains("FOR UPDATE")) {
                lockedAssignmentId = ((Number) args[0]).longValue();
                return requiredType.cast(lockedAssignmentId);
            }
            return requiredType.cast(32);
        }

        @Override
        public Map<String, Object> queryForMap(String sql, Object... args) {
            if (sql.contains("FROM dengjx_students13")) {
                return Map.of(
                        "djx_studentid13", 1L,
                        "djx_classid13", 1L,
                        "djx_admissiondate13", LocalDate.of(2025, 9, 1));
            }
            return Map.of("djx_coursetype13", "ELECTIVE", "djx_targetgrade13", 1, "djx_targetsemester13", 2, "djx_hours13", 32);
        }

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            return List.of();
        }
    }

    private static class CourseHoursJdbcTemplate extends JdbcTemplate {

        private final Integer hours;

        CourseHoursJdbcTemplate(Integer hours) {
            this.hours = hours;
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            return requiredType.cast(hours);
        }
    }

    private static class FixedAcademicTermService extends com.dengjx.affairs.service.impl.AcademicTermService {

        @Override
        public AcademicTerm current(LocalDate admissionDate) {
            return new AcademicTerm(1, 2, "2025-2026", "大一下学期");
        }
    }

    private Assignment assignment(Long id, Long teacherId, Long classId, Long classroomId, LocalTime start, LocalTime end) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(id);
        assignment.setMajorCourseId(1L);
        assignment.setTeacherId(teacherId);
        assignment.setClassId(classId);
        assignment.setClassroomId(classroomId);
        assignment.setAcademicYear("2025-2026");
        assignment.setSemester(1);
        assignment.setWeekdayOne(1);
        assignment.setStartTimeOne(start);
        assignment.setEndTimeOne(end);
        return assignment;
    }

    private AssignmentRequest assignmentRequest(Long teacherId, Long classId, Long classroomId, LocalTime start, LocalTime end) {
        return new AssignmentRequest(
                1L,
                classId,
                teacherId,
                classroomId,
                "2025-2026",
                1,
                30,
                true,
                1,
                start,
                end,
                null,
                null,
                null);
    }

    private static class FakeStudentTermJdbcTemplate extends JdbcTemplate {

        private String lastSql;

        @Override
        public Map<String, Object> queryForMap(String sql, Object... args) {
            return Map.of(
                    "djx_studentid13", 1L,
                    "djx_classid13", 1L,
                    "djx_admissiondate13", LocalDate.of(2025, 9, 1));
        }

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            lastSql = sql;
            return List.of();
        }
    }

    private static class FakePendingTransferJdbcTemplate extends JdbcTemplate {

        private Object[] availableCourseArgs = new Object[0];

        @Override
        public Map<String, Object> queryForMap(String sql, Object... args) {
            return Map.of(
                    "djx_studentid13", 1L,
                    "djx_classid13", 22L,
                    "djx_admissiondate13", LocalDate.of(2025, 9, 1));
        }

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            if (sql.contains("FROM dengjx_majortransferapplications13")) {
                return List.of(Map.of(
                        "fromclassid", 11L,
                        "effectiveacademicyear", "2026-2027",
                        "effectivesemester", 1));
            }
            if (sql.contains("mc.djx_CourseType13 = 'ELECTIVE'")) {
                availableCourseArgs = args;
            }
            return List.of();
        }
    }
}
