package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.dto.FinalExamRequest;
import com.dengjx.affairs.dto.MajorTransferReviewRequest;
import com.dengjx.affairs.dto.MajorTransferSubmitRequest;
import com.dengjx.affairs.entity.FinalExam;
import com.dengjx.affairs.entity.MajorTransferApplication;
import com.dengjx.affairs.entity.Student;
import com.dengjx.affairs.mapper.FinalExamMapper;
import com.dengjx.affairs.mapper.MajorTransferApplicationMapper;
import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.service.FinalExamService;
import com.dengjx.affairs.service.MajorTransferSettingService;
import com.dengjx.affairs.service.MajorTransferApplicationService;
import com.dengjx.affairs.service.impl.FinalExamServiceImpl;
import com.dengjx.affairs.service.impl.MajorTransferApplicationServiceImpl;
import com.dengjx.affairs.service.impl.StatisticsServiceImpl;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class AcademicFeatureContractTests {

    @Test
    void locationEntitiesMapBuildingAndClassroomColumns() throws NoSuchFieldException {
        assertColumn(com.dengjx.affairs.entity.TeachingBuilding.class, "buildingName", "djx_buildingname13");
        assertColumn(com.dengjx.affairs.entity.Classroom.class, "buildingId", "djx_buildingid13");
        assertColumn(com.dengjx.affairs.entity.Classroom.class, "classroomName", "djx_classroomname13");
    }

    @Test
    void majorTransferApprovalUpdatesStudentClassButCurrentTermTakesEffectNextSemester() {
        MajorTransferApplicationMapper applicationMapper = mock(MajorTransferApplicationMapper.class);
        StudentMapper studentMapper = mock(StudentMapper.class);
        MajorTransferApplication application = new MajorTransferApplication();
        application.setApplicationId(9L);
        application.setStudentId(3L);
        application.setFromClassId(11L);
        application.setTargetMajorId(2L);
        application.setStatus("PENDING");

        when(applicationMapper.selectById(9L)).thenReturn(application);
        when(applicationMapper.updateById(application)).thenReturn(1);

        MajorTransferApplicationService service = new MajorTransferApplicationServiceImpl(
                applicationMapper,
                studentMapper,
                new FixedStudentUserContextService(),
                new FixedTransferJdbcTemplate(),
                new FixedTransferTermService(),
                openMajorTransferSettingService());

        MajorTransferApplication approved = service.review(
                100L,
                9L,
                new MajorTransferReviewRequest("APPROVED", 22L, "同意转入"));

        assertThat(approved.getStatus()).isEqualTo("APPROVED");
        assertThat(approved.getFromClassId()).isEqualTo(11L);
        assertThat(approved.getTargetClassId()).isEqualTo(22L);
        assertThat(approved.getEffectiveAcademicYear()).isEqualTo("2026-2027");
        assertThat(approved.getEffectiveSemester()).isEqualTo(1);
        verify(applicationMapper).updateById(application);
        verify(studentMapper).updateById(any(Student.class));
    }

    @Test
    void majorTransferSubmitBlockedWhenApplicationSettingIsClosed() {
        MajorTransferApplicationService service = new MajorTransferApplicationServiceImpl(
                mock(MajorTransferApplicationMapper.class),
                mock(StudentMapper.class),
                new FixedStudentUserContextService(),
                new FixedTransferJdbcTemplate(),
                new FixedTransferTermService(),
                closedMajorTransferSettingService());

        assertThatThrownBy(() -> service.submit(10L, new MajorTransferSubmitRequest(2L, "希望转入")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前转专业申请未开放");
    }

    @Test
    void majorTransferRejectDoesNotRequireTargetClass() {
        MajorTransferApplicationMapper applicationMapper = mock(MajorTransferApplicationMapper.class);
        MajorTransferApplication application = new MajorTransferApplication();
        application.setApplicationId(10L);
        application.setStudentId(3L);
        application.setTargetMajorId(2L);
        application.setStatus("PENDING");

        when(applicationMapper.selectById(10L)).thenReturn(application);

        MajorTransferApplicationService service = new MajorTransferApplicationServiceImpl(
                applicationMapper,
                mock(StudentMapper.class),
                new FixedStudentUserContextService(),
                new FixedTransferJdbcTemplate(),
                new FixedTransferTermService(),
                openMajorTransferSettingService());

        MajorTransferApplication rejected = service.review(
                100L,
                10L,
                new MajorTransferReviewRequest("REJECTED", null, "名额不足"));

        assertThat(rejected.getStatus()).isEqualTo("REJECTED");
        assertThat(rejected.getTargetClassId()).isNull();
    }

    @Test
    void majorTransferSubmitRejectsCurrentMajorAsTarget() {
        MajorTransferApplicationService service = new MajorTransferApplicationServiceImpl(
                mock(MajorTransferApplicationMapper.class),
                mock(StudentMapper.class),
                new FixedStudentUserContextService(),
                new FixedTransferJdbcTemplate(),
                new FixedTransferTermService(),
                openMajorTransferSettingService());

        assertThatThrownBy(() -> service.submit(10L, new MajorTransferSubmitRequest(1L, "希望转入")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不能申请转入当前专业");
    }

    @Test
    void finalExamRequiresOneTimePerCourseAcademicYearAndSemester() {
        FinalExamMapper mapper = mock(FinalExamMapper.class);
        when(mapper.selectCount(any())).thenReturn(1L);
        FinalExamService service = new FinalExamServiceImpl(mapper, new FixedStudentUserContextService(), new FixedExamJdbcTemplate());

        FinalExamRequest request = new FinalExamRequest(5L, "2025-2026", 2, LocalDateTime.of(2026, 6, 20, 9, 0));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("期末考试已存在");
    }

    @Test
    void finalExamCannotBeChangedAfterCourseCompleted() {
        FinalExamMapper mapper = mock(FinalExamMapper.class);
        when(mapper.selectCount(any())).thenReturn(0L);
        FinalExamService service = new FinalExamServiceImpl(
                mapper,
                new FixedStudentUserContextService(),
                new FixedCompletedExamJdbcTemplate());

        FinalExamRequest request = new FinalExamRequest(5L, "2025-2026", 2, LocalDateTime.of(2026, 6, 20, 9, 0));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已完成");
    }

    @Test
    void studentFinalExamListUsesAssignmentClassroomAsDefaultLocation() {
        FinalExamService service = new FinalExamServiceImpl(
                mock(FinalExamMapper.class),
                new FixedStudentUserContextService(),
                new FixedExamJdbcTemplate());

        List<Map<String, Object>> rows = service.studentMine(10L);

        assertThat(rows).singleElement()
                .satisfies(row -> assertThat(row).containsEntry("classroomLabel", "广知楼 A101"));
    }

    @Test
    void studentFinalExamListOnlyIncludesSelectedEnrollments() {
        FixedExamJdbcTemplate jdbcTemplate = new FixedExamJdbcTemplate();
        FinalExamService service = new FinalExamServiceImpl(
                mock(FinalExamMapper.class),
                new FixedStudentUserContextService(),
                jdbcTemplate);

        service.studentMine(10L);

        assertThat(jdbcTemplate.lastSql)
                .contains("e.djx_status13 = 'SELECTED'")
                .doesNotContain("COMPLETED");
    }

    @Test
    void teacherScheduleSplitsAssignmentsByHalfTerm() {
        FixedTeacherScheduleJdbcTemplate jdbcTemplate = new FixedTeacherScheduleJdbcTemplate();
        StatisticsServiceImpl service = new StatisticsServiceImpl(
                jdbcTemplate,
                new FixedStudentUserContextService(),
                null,
                null);

        Map<String, Object> schedule = service.teacherSchedule(100L);

        assertThat(jdbcTemplate.teacherIdArg).isEqualTo(7L);
        Map<Integer, List<Map<String, Object>>> firstHalf = scheduleHalf(schedule, "firstHalf");
        Map<Integer, List<Map<String, Object>>> secondHalf = scheduleHalf(schedule, "secondHalf");
        assertThat(firstHalf.get(1)).singleElement()
                .satisfies(row -> assertThat(row)
                        .containsEntry("weekday", 1)
                        .containsEntry("startTime", "08:00")
                        .containsEntry("endTime", "09:40"));
        assertThat(firstHalf.get(3)).singleElement()
                .satisfies(row -> assertThat(row)
                        .containsEntry("weekday", 3)
                        .containsEntry("startTime", "13:30")
                        .containsEntry("endTime", "15:10"));
        assertThat(secondHalf.get(1)).singleElement()
                .satisfies(row -> assertThat(row).containsEntry("weekday", 1));
        assertThat(secondHalf.get(3)).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, List<Map<String, Object>>> scheduleHalf(Map<String, Object> schedule, String key) {
        return (Map<Integer, List<Map<String, Object>>>) schedule.get(key);
    }

    private static void assertColumn(Class<?> entityClass, String fieldName, String columnName) throws NoSuchFieldException {
        Field field = entityClass.getDeclaredField(fieldName);
        TableField tableField = field.getAnnotation(TableField.class);

        assertThat(tableField).isNotNull();
        assertThat(tableField.value()).isEqualTo(columnName);
    }

    private static class FixedStudentUserContextService extends UserContextService {

        FixedStudentUserContextService() {
            super(null);
        }

        @Override
        public Long getStudentId(Long userId) {
            return 3L;
        }

        @Override
        public Long getTeacherId(Long userId) {
            return 7L;
        }
    }

    private static class FixedTransferJdbcTemplate extends JdbcTemplate {

        @Override
        public Map<String, Object> queryForMap(String sql, Object... args) {
            if (sql.contains("FROM dengjx_students13")) {
                return Map.of("studentid", 3L, "classid", 11L, "majorid", 1L, "admissiondate", java.time.LocalDate.of(2025, 9, 1));
            }
            return Map.of("majorid", 2L);
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            return requiredType.cast(0L);
        }
    }

    private static class FixedTransferTermService extends com.dengjx.affairs.service.impl.AcademicTermService {

        @Override
        public AcademicTerm current(java.time.LocalDate admissionDate) {
            return new AcademicTerm(1, 2, "2025-2026", "大一下学期");
        }
    }

    private static MajorTransferSettingService openMajorTransferSettingService() {
        MajorTransferSettingService service = mock(MajorTransferSettingService.class);
        when(service.isEnabled()).thenReturn(true);
        return service;
    }

    private static MajorTransferSettingService closedMajorTransferSettingService() {
        MajorTransferSettingService service = mock(MajorTransferSettingService.class);
        when(service.isEnabled()).thenReturn(false);
        return service;
    }

    private static class FixedExamJdbcTemplate extends JdbcTemplate {

        private String lastSql;

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            this.lastSql = sql;
            return List.of(Map.of(
                    "courseName", "数据库原理",
                    "examTime", LocalDateTime.of(2026, 6, 20, 9, 0),
                    "classroomLabel", "广知楼 A101"));
        }
    }

    private static class FixedCompletedExamJdbcTemplate extends FixedExamJdbcTemplate {

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            return requiredType.cast(1L);
        }
    }

    private static class FixedTeacherScheduleJdbcTemplate extends JdbcTemplate {

        private Long teacherIdArg;

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            this.teacherIdArg = ((Number) args[0]).longValue();
            return List.of(Map.ofEntries(
                    Map.entry("djx_assignmentid13", 5L),
                    Map.entry("djx_coursecode13", "DB001"),
                    Map.entry("djx_coursename13", "数据库原理"),
                    Map.entry("djx_hours13", 48),
                    Map.entry("djx_coursetype13", "REQUIRED"),
                    Map.entry("djx_classname13", "计科2301"),
                    Map.entry("djx_tname13", "王老师"),
                    Map.entry("djx_classroomlabel13", "广知楼 A101"),
                    Map.entry("djx_academicyear13", "2025-2026"),
                    Map.entry("djx_semester13", 2),
                    Map.entry("djx_weekdayone13", 1),
                    Map.entry("djx_starttimeone13", LocalTime.of(8, 0)),
                    Map.entry("djx_endtimeone13", LocalTime.of(9, 40)),
                    Map.entry("djx_weekdaytwo13", 3),
                    Map.entry("djx_starttimetwo13", LocalTime.of(13, 30)),
                    Map.entry("djx_endtimetwo13", LocalTime.of(15, 10))));
        }
    }
}
