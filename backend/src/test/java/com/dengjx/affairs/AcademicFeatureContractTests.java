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
import com.dengjx.affairs.service.MajorTransferApplicationService;
import com.dengjx.affairs.service.impl.FinalExamServiceImpl;
import com.dengjx.affairs.service.impl.MajorTransferApplicationServiceImpl;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
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
    void majorTransferApprovalUpdatesStudentClassButLeavesEnrollmentsUntouched() {
        MajorTransferApplicationMapper applicationMapper = mock(MajorTransferApplicationMapper.class);
        StudentMapper studentMapper = mock(StudentMapper.class);
        MajorTransferApplication application = new MajorTransferApplication();
        application.setApplicationId(9L);
        application.setStudentId(3L);
        application.setTargetMajorId(2L);
        application.setStatus("PENDING");

        when(applicationMapper.selectById(9L)).thenReturn(application);

        MajorTransferApplicationService service = new MajorTransferApplicationServiceImpl(
                applicationMapper,
                studentMapper,
                new FixedStudentUserContextService(),
                new FixedTransferJdbcTemplate());

        MajorTransferApplication approved = service.review(
                100L,
                9L,
                new MajorTransferReviewRequest("APPROVED", 22L, "同意转入"));

        assertThat(approved.getStatus()).isEqualTo("APPROVED");
        assertThat(approved.getTargetClassId()).isEqualTo(22L);
        verify(applicationMapper).updateById(application);
        verify(studentMapper).updateById(any(Student.class));
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
                new FixedTransferJdbcTemplate());

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
                new FixedTransferJdbcTemplate());

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
                return Map.of("studentid", 3L, "classid", 11L, "majorid", 1L);
            }
            return Map.of("majorid", 2L);
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            return requiredType.cast(0L);
        }
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
}
