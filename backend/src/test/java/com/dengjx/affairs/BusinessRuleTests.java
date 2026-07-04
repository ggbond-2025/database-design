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
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.service.EnrollmentService;
import com.dengjx.affairs.service.EnrollmentSettingService;
import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
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
            return Map.of("djx_coursetype13", "ELECTIVE", "djx_targetgrade13", 1, "djx_targetsemester13", 1, "djx_hours13", 32);
        }
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
}
