package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.dto.LookupOption;
import com.dengjx.affairs.service.LookupService;
import com.dengjx.affairs.service.impl.LookupServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class LookupServiceTests {

    @Test
    void courseOptionsExposeValueAndReadableLabel() {
        FakeJdbcTemplate jdbcTemplate = new FakeJdbcTemplate(List.of(
                Map.of("value", 1L, "label", "CS101 / 数据库原理")));

        LookupService service = new LookupServiceImpl(jdbcTemplate);

        List<LookupOption> options = service.courseOptions();

        assertThat(options).containsExactly(new LookupOption(1L, "CS101 / 数据库原理"));
        assertThat(jdbcTemplate.lastSql()).contains("dengjx_courses13");
    }

    @Test
    void majorCourseOptionsExposeMajorCourseAndTerm() {
        FakeJdbcTemplate jdbcTemplate = new FakeJdbcTemplate(List.of(
                Map.of("value", 7L, "label", "软件工程 / CS101 / 数据库原理 / 必修 / 大一上学期")));

        LookupService service = new LookupServiceImpl(jdbcTemplate);

        List<LookupOption> options = service.majorCourseOptions();

        assertThat(options).containsExactly(new LookupOption(7L, "软件工程 / CS101 / 数据库原理 / 必修 / 大一上学期"));
        assertThat(jdbcTemplate.lastSql()).contains("dengjx_majorcourses13");
    }

    @Test
    void activeEnrollmentOptionsIncludeStudentCourseAndStatus() {
        FakeJdbcTemplate jdbcTemplate = new FakeJdbcTemplate(List.of(
                Map.of("value", 9L, "label", "20230001 / 张三 / 数据库原理 / 已选")));

        LookupService service = new LookupServiceImpl(jdbcTemplate);

        List<LookupOption> options = service.activeEnrollmentOptions();

        assertThat(options).containsExactly(new LookupOption(9L, "20230001 / 张三 / 数据库原理 / 已选"));
        assertThat(jdbcTemplate.lastSql()).contains("dengjx_enrollments13");
    }

    private static class FakeJdbcTemplate extends JdbcTemplate {

        private final List<Map<String, Object>> rows;
        private final List<String> sqlHistory = new ArrayList<>();

        FakeJdbcTemplate(List<Map<String, Object>> rows) {
            this.rows = rows;
        }

        @Override
        public List<Map<String, Object>> queryForList(String sql) {
            sqlHistory.add(sql);
            return rows;
        }

        String lastSql() {
            return sqlHistory.get(sqlHistory.size() - 1);
        }
    }
}
