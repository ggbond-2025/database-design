package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.dto.LookupOption;
import java.util.LinkedHashMap;
import com.dengjx.affairs.service.LookupService;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LookupServiceImpl implements LookupService {

    private final JdbcTemplate jdbcTemplate;

    public LookupServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LookupOption> studentOptions() {
        return queryOptions("""
                SELECT djx_studentid13 AS value,
                       djx_sno13 || ' / ' || djx_sname13 AS label
                FROM dengjx_students13
                ORDER BY djx_sno13
                """);
    }

    @Override
    public List<LookupOption> regionOptions() {
        return queryOptions("""
                SELECT djx_regionid13 AS value,
                       djx_regionname13 AS label
                FROM dengjx_regions13
                ORDER BY djx_regionname13
                """);
    }

    @Override
    public List<LookupOption> majorOptions() {
        return queryOptions("""
                SELECT djx_majorid13 AS value,
                       djx_majorname13 AS label
                FROM dengjx_majors13
                ORDER BY djx_majorname13
                """);
    }

    @Override
    public List<LookupOption> teacherOptions() {
        return queryOptions("""
                SELECT djx_teacherid13 AS value,
                       djx_tno13 || ' / ' || djx_tname13 AS label
                FROM dengjx_teachers13
                ORDER BY djx_tno13
                """);
    }

    @Override
    public List<LookupOption> courseOptions() {
        return queryOptions("""
                SELECT djx_courseid13 AS value,
                       djx_coursecode13 || ' / ' || djx_coursename13 AS label
                FROM dengjx_courses13
                ORDER BY djx_coursecode13
                """);
    }

    @Override
    public List<LookupOption> majorCourseOptions() {
        return queryOptions("""
                SELECT mc.djx_majorcourseid13 AS value,
                       m.djx_majorname13 || ' / ' || c.djx_coursecode13 || ' / ' || c.djx_coursename13 || ' / ' ||
                       CASE mc.djx_coursetype13
                           WHEN 'REQUIRED' THEN '必修'
                           WHEN 'ELECTIVE' THEN '选修'
                           ELSE mc.djx_coursetype13
                       END || ' / 大' || mc.djx_targetgrade13 ||
                       CASE mc.djx_targetsemester13
                           WHEN 1 THEN '上学期'
                           WHEN 2 THEN '下学期'
                           ELSE mc.djx_targetsemester13 || '学期'
                       END AS label,
                       c.djx_hours13 AS hours
                FROM dengjx_majorcourses13 mc
                JOIN dengjx_majors13 m ON m.djx_majorid13 = mc.djx_majorid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                ORDER BY m.djx_majorname13, mc.djx_targetgrade13, mc.djx_targetsemester13, c.djx_coursecode13
                """);
    }

    @Override
    public List<LookupOption> classOptions() {
        return queryOptions("""
                SELECT c.djx_classid13 AS value,
                       c.djx_classname13 || ' / ' || m.djx_majorname13 AS label,
                       m.djx_majorid13 AS "majorId"
                FROM dengjx_classes13 c
                JOIN dengjx_majors13 m ON m.djx_majorid13 = c.djx_majorid13
                ORDER BY c.djx_gradeyear13, c.djx_classname13
                """);
    }

    @Override
    public List<LookupOption> teachingBuildingOptions() {
        return queryOptions("""
                SELECT djx_buildingid13 AS value,
                       djx_buildingname13 AS label
                FROM dengjx_teachingbuildings13
                ORDER BY djx_buildingname13
                """);
    }

    @Override
    public List<LookupOption> classroomOptions() {
        return queryOptions("""
                SELECT cr.djx_classroomid13 AS value,
                       tb.djx_buildingname13 || ' / ' || cr.djx_classroomname13 AS label,
                       tb.djx_buildingid13 AS "buildingId",
                       tb.djx_buildingname13 AS "buildingName",
                       cr.djx_capacity13 AS capacity
                FROM dengjx_classrooms13 cr
                JOIN dengjx_teachingbuildings13 tb ON tb.djx_buildingid13 = cr.djx_buildingid13
                ORDER BY tb.djx_buildingname13, cr.djx_classroomname13
                """);
    }

    @Override
    public List<LookupOption> assignmentOptions() {
        return queryOptions("""
                SELECT a.djx_assignmentid13 AS value,
                       c.djx_coursename13 || ' / ' || cl.djx_classname13 || ' / ' ||
                       a.djx_academicyear13 || ' / 第' || a.djx_semester13 || '学期 / ' ||
                       t.djx_tname13 AS label
                FROM dengjx_teachingassignments13 a
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = a.djx_classid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                ORDER BY a.djx_academicyear13, a.djx_semester13, c.djx_coursecode13
                """);
    }

    @Override
    public List<LookupOption> activeEnrollmentOptions() {
        return queryOptions("""
                SELECT e.djx_enrollmentid13 AS value,
                       s.djx_sno13 || ' / ' || s.djx_sname13 || ' / ' || c.djx_coursename13 || ' / ' ||
                       CASE e.djx_status13
                           WHEN 'SELECTED' THEN '已选'
                           WHEN 'COMPLETED' THEN '已完成'
                           ELSE e.djx_status13
                       END AS label
                FROM dengjx_enrollments13 e
                JOIN dengjx_students13 s ON s.djx_studentid13 = e.djx_studentid13
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE e.djx_status13 IN ('SELECTED', 'COMPLETED')
                ORDER BY s.djx_sno13, c.djx_coursecode13
                """);
    }

    private List<LookupOption> queryOptions(String sql) {
        return jdbcTemplate.queryForList(sql)
                .stream()
                .map(this::mapOption)
                .toList();
    }

    private LookupOption mapOption(Map<String, Object> row) {
        Object value = row.get("value");
        if (!(value instanceof Number number)) {
            throw new IllegalStateException("Lookup value must be numeric");
        }
        Map<String, Object> meta = new LinkedHashMap<>(row);
        meta.remove("value");
        meta.remove("label");
        return new LookupOption(number.longValue(), String.valueOf(row.get("label")), meta);
    }
}
