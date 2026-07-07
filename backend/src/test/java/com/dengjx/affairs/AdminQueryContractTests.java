package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.entity.MajorCourse;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.mapper.MajorCourseMapper;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
import com.dengjx.affairs.service.impl.GradeServiceImpl;
import com.dengjx.affairs.service.impl.MajorCourseServiceImpl;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

class AdminQueryContractTests {

    @BeforeAll
    static void initMybatisTableInfo() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, Assignment.class);
        TableInfoHelper.initTableInfo(assistant, MajorCourse.class);
    }

    @Test
    void assignmentListKeywordSearchesRelatedCourseClassAndTeacherNames() {
        AssignmentMapper mapper = mock(AssignmentMapper.class);
        when(mapper.selectPage(any(), any())).thenReturn(new Page<Assignment>(1, 10));

        new AssignmentServiceImpl(mapper).list("数据库", 1, 10);

        String segment = capturedAssignmentWrapper(mapper).getCustomSqlSegment();
        assertThat(segment)
                .contains("dengjx_courses13")
                .contains("dengjx_classes13")
                .contains("dengjx_teachers13");
    }

    @Test
    void majorCourseListKeywordSearchesRelatedMajorAndCourseNames() {
        MajorCourseMapper mapper = mock(MajorCourseMapper.class);
        when(mapper.selectPage(any(), any())).thenReturn(new Page<MajorCourse>(1, 10));

        new MajorCourseServiceImpl(mapper).list("数据库", 1, 10);

        String segment = capturedMajorCourseWrapper(mapper).getCustomSqlSegment();
        assertThat(segment)
                .contains("dengjx_majors13")
                .contains("dengjx_courses13");
    }

    @Test
    void enrollmentAdminListAcceptsKeywordAndFiltersVisibleColumns() throws Exception {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        EnrollmentServiceImpl service = new EnrollmentServiceImpl(
                mock(EnrollmentMapper.class),
                null,
                null,
                null,
                jdbcTemplate);

        Method method = EnrollmentServiceImpl.class.getMethod("adminList", String.class, long.class, long.class);
        method.invoke(service, "数据库", 1L, 10L);

        assertThat(jdbcTemplate.lastListSql)
                .contains("s.djx_sno13 LIKE")
                .contains("s.djx_sname13 LIKE")
                .contains("c.djx_coursename13 LIKE")
                .contains("t.djx_tname13 LIKE");
        assertThat(jdbcTemplate.lastObjectSql).contains("WHERE");
    }

    @Test
    void gradeAdminAssignmentListAcceptsKeywordAndFiltersVisibleColumns() throws Exception {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();
        GradeServiceImpl service = new GradeServiceImpl(
                mock(GradeMapper.class),
                null,
                null,
                null,
                jdbcTemplate);

        Method method = GradeServiceImpl.class.getMethod("adminAssignmentList", String.class, long.class, long.class);
        method.invoke(service, "数据库", 1L, 10L);

        assertThat(jdbcTemplate.lastListSql)
                .contains("c.djx_coursecode13 LIKE")
                .contains("c.djx_coursename13 LIKE")
                .contains("cl.djx_classname13 LIKE")
                .contains("t.djx_tname13 LIKE");
        assertThat(jdbcTemplate.lastObjectSql).contains("WHERE");
    }

    private static LambdaQueryWrapper<Assignment> capturedAssignmentWrapper(AssignmentMapper mapper) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper<Assignment>> captor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(mapper).selectPage(any(), captor.capture());
        return captor.getValue();
    }

    private static LambdaQueryWrapper<MajorCourse> capturedMajorCourseWrapper(MajorCourseMapper mapper) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper<MajorCourse>> captor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(mapper).selectPage(any(), captor.capture());
        return captor.getValue();
    }

    private static class RecordingJdbcTemplate extends JdbcTemplate {

        private String lastListSql;
        private String lastObjectSql;

        @Override
        public List<Map<String, Object>> queryForList(String sql, Object... args) {
            this.lastListSql = sql;
            return List.of();
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            this.lastObjectSql = sql;
            return requiredType.cast(0L);
        }
    }
}
