package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.service.StatisticsService;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.security.UserContextService;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final JdbcTemplate jdbcTemplate;
    private final UserContextService userContextService;
    private final AssignmentMapper assignmentMapper;
    private final EnrollmentMapper enrollmentMapper;

    public StatisticsServiceImpl(
            JdbcTemplate jdbcTemplate,
            UserContextService userContextService,
            AssignmentMapper assignmentMapper,
            EnrollmentMapper enrollmentMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userContextService = userContextService;
        this.assignmentMapper = assignmentMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    public List<Map<String, Object>> regionCounts() {
        return jdbcTemplate.queryForList("SELECT * FROM V_Dengjx_RegionStudentCount13 ORDER BY djx_RegionId13");
    }

    public List<Map<String, Object>> adminCourseAverages() {
        return jdbcTemplate.queryForList("SELECT * FROM V_Dengjx_CourseAverage13 ORDER BY djx_AcademicYear13, djx_Semester13");
    }

    public List<Map<String, Object>> studentCreditSummaries() {
        return jdbcTemplate.queryForList("SELECT * FROM V_Dengjx_StudentCreditSummary13 ORDER BY djx_StudentId13");
    }

    public List<Map<String, Object>> teacherAssignments() {
        return jdbcTemplate.queryForList("SELECT * FROM V_Dengjx_TeacherAssignments13 ORDER BY djx_TeacherId13, djx_AcademicYear13, djx_Semester13");
    }

    public List<Map<String, Object>> classCourses() {
        return jdbcTemplate.queryForList("SELECT * FROM V_Dengjx_ClassCourses13 ORDER BY djx_ClassId13, djx_AcademicYear13, djx_Semester13");
    }

    public List<Map<String, Object>> teacherCourseAverages(Long userId) {
        Long teacherId = userContextService.getTeacherId(userId);
        String sql = """
                SELECT v.*
                FROM V_Dengjx_CourseAverage13 v
                JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = v.djx_AssignmentId13
                WHERE a.djx_TeacherId13 = ?
                ORDER BY v.djx_AcademicYear13, v.djx_Semester13
                """;
        return jdbcTemplate.queryForList(sql, teacherId);
    }

    public List<Map<String, Object>> studentYearScores(Long studentId, String academicYear) {
        return callRefCursor("{ call Dengjx_GetStudentYearScores13(?, ?, ?) }", 3, statement -> {
            statement.setLong(1, studentId);
            statement.setString(2, academicYear);
            statement.registerOutParameter(3, Types.REF_CURSOR);
        });
    }

    public List<Map<String, Object>> courseRank(Long assignmentId) {
        return callRefCursor("{ call Dengjx_GetCourseScoreRank13(?, ?) }", 2, statement -> {
            statement.setLong(1, assignmentId);
            statement.registerOutParameter(2, Types.REF_CURSOR);
        });
    }

    public Map<String, Object> studentCredits(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM V_Dengjx_StudentCreditSummary13 WHERE djx_StudentId13 = ?",
                studentId);
        if (rows.isEmpty()) {
            throw new BusinessException("学生学分统计不存在");
        }
        return rows.get(0);
    }

    public List<Map<String, Object>> studentYearScoresForCurrentUser(Long userId, String academicYear) {
        return studentYearScores(userContextService.getStudentId(userId), academicYear);
    }

    public List<Map<String, Object>> teacherCourseRank(Long userId, Long assignmentId) {
        requireTeacherOwnsAssignment(userId, assignmentId);
        return courseRank(assignmentId);
    }

    public List<Map<String, Object>> studentCourseRank(Long userId, Long assignmentId) {
        Long studentId = userContextService.getStudentId(userId);
        Enrollment enrollment = enrollmentMapper.selectOne(Wrappers.<Enrollment>lambdaQuery()
                .eq(Enrollment::getStudentId, studentId)
                .eq(Enrollment::getAssignmentId, assignmentId));
        if (enrollment == null) {
            throw new AccessDeniedException("只能查看本人已选课程排名");
        }
        return courseRank(assignmentId);
    }

    private void requireTeacherOwnsAssignment(Long userId, Long assignmentId) {
        Long teacherId = userContextService.getTeacherId(userId);
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        if (!teacherId.equals(assignment.getTeacherId())) {
            throw new AccessDeniedException("只能查看本人任课课程统计");
        }
    }

    private List<Map<String, Object>> callRefCursor(String callSql, int outIndex, ProcedureBinder binder) {
        return jdbcTemplate.execute((ConnectionCallback<List<Map<String, Object>>>) connection -> {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (CallableStatement statement = connection.prepareCall(callSql)) {
                binder.bind(statement);
                statement.execute();
                try (ResultSet resultSet = (ResultSet) statement.getObject(outIndex)) {
                    return resultSetToList(resultSet);
                }
            } finally {
                restoreAutoCommit(connection, autoCommit);
            }
        });
    }

    private void restoreAutoCommit(Connection connection, boolean autoCommit) throws SQLException {
        if (connection.getAutoCommit() != autoCommit) {
            connection.setAutoCommit(autoCommit);
        }
    }

    private List<Map<String, Object>> resultSetToList(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int index = 1; index <= columnCount; index++) {
                row.put(metaData.getColumnLabel(index), resultSet.getObject(index));
            }
            rows.add(row);
        }
        return rows;
    }

    @FunctionalInterface
    private interface ProcedureBinder {
        void bind(CallableStatement statement) throws SQLException;
    }
}
