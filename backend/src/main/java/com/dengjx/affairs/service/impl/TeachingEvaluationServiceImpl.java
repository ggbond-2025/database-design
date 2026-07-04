package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingEvaluationRequest;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.entity.TeachingEvaluation;
import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.mapper.TeachingEvaluationMapper;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.service.TeachingEvaluationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeachingEvaluationServiceImpl implements TeachingEvaluationService {

    private final TeachingEvaluationMapper evaluationMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final AssignmentMapper assignmentMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;

    public TeachingEvaluationServiceImpl(
            TeachingEvaluationMapper evaluationMapper,
            EnrollmentMapper enrollmentMapper,
            AssignmentMapper assignmentMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this.evaluationMapper = evaluationMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.assignmentMapper = assignmentMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> studentCompletedCourses(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        return jdbcTemplate.queryForList("""
                SELECT e.djx_enrollmentid13 AS "enrollmentId",
                       e.djx_assignmentid13 AS "assignmentId",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       t.djx_tname13 AS "teacherName",
                       cl.djx_classname13 AS "className",
                       a.djx_academicyear13 AS "academicYear",
                       a.djx_semester13 AS "semester",
                       ev.djx_evaluationid13 AS "evaluationId",
                       ev.djx_rating13 AS "rating",
                       ev.djx_comment13 AS "comment",
                       ev.djx_evaluatedat13 AS "evaluatedAt"
                FROM dengjx_enrollments13 e
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = a.djx_classid13
                LEFT JOIN dengjx_teachingevaluations13 ev ON ev.djx_enrollmentid13 = e.djx_enrollmentid13
                WHERE e.djx_studentid13 = ?
                  AND e.djx_status13 = 'COMPLETED'
                ORDER BY a.djx_academicyear13 DESC, a.djx_semester13 DESC, c.djx_coursecode13
                """, studentId);
    }

    @Override
    @Transactional
    public TeachingEvaluation studentCreate(Long userId, TeachingEvaluationRequest request) {
        validateRating(request.rating());
        Long studentId = userContextService.getStudentId(userId);
        Enrollment enrollment = enrollmentMapper.selectById(request.enrollmentId());
        if (enrollment == null || !studentId.equals(enrollment.getStudentId())) {
            throw new AccessDeniedException("只能评价本人完成的课程");
        }
        if (!"COMPLETED".equals(enrollment.getStatus())) {
            throw new BusinessException("只能评价已完成课程");
        }
        TeachingEvaluation existing = evaluationMapper.selectOne(Wrappers.<TeachingEvaluation>lambdaQuery()
                .eq(TeachingEvaluation::getEnrollmentId, request.enrollmentId()));
        if (existing != null) {
            throw new BusinessException("该课程已评价，不能重复提交");
        }

        TeachingEvaluation evaluation = new TeachingEvaluation();
        evaluation.setEnrollmentId(request.enrollmentId());
        evaluation.setRating(request.rating());
        evaluation.setComment(normalizeComment(request.comment()));
        evaluation.setEvaluatedAt(LocalDateTime.now());
        evaluationMapper.insert(evaluation);
        return evaluation;
    }

    @Override
    public PageResult<Map<String, Object>> teacherAssignments(Long userId, long page, long size) {
        Long teacherId = userContextService.getTeacherId(userId);
        return assignmentPage("""
                WHERE a.djx_teacherid13 = ?
                """, page, size, teacherId);
    }

    @Override
    public List<Map<String, Object>> teacherAssignmentEvaluations(Long userId, Long assignmentId) {
        requireTeacherOwnsAssignment(userId, assignmentId);
        return assignmentEvaluations(assignmentId);
    }

    @Override
    public List<Map<String, Object>> adminTeachers() {
        return jdbcTemplate.queryForList("""
                SELECT t.djx_teacherid13 AS "teacherId",
                       t.djx_tno13 AS "tno",
                       t.djx_tname13 AS "teacherName",
                       COUNT(ev.djx_evaluationid13) AS "evaluationCount"
                FROM dengjx_teachers13 t
                LEFT JOIN dengjx_teachingassignments13 a ON a.djx_teacherid13 = t.djx_teacherid13
                LEFT JOIN dengjx_enrollments13 e ON e.djx_assignmentid13 = a.djx_assignmentid13
                LEFT JOIN dengjx_teachingevaluations13 ev ON ev.djx_enrollmentid13 = e.djx_enrollmentid13
                GROUP BY t.djx_teacherid13, t.djx_tno13, t.djx_tname13
                ORDER BY t.djx_tno13
                """);
    }

    @Override
    public PageResult<Map<String, Object>> adminTeacherAssignments(Long teacherId, long page, long size) {
        return assignmentPage("""
                WHERE a.djx_teacherid13 = ?
                """, page, size, teacherId);
    }

    @Override
    public List<Map<String, Object>> adminAssignmentEvaluations(Long assignmentId) {
        requireAssignment(assignmentId);
        return assignmentEvaluations(assignmentId);
    }

    private PageResult<Map<String, Object>> assignmentPage(String whereClause, long page, long size, Object... params) {
        long offset = (page - 1) * size;
        Object[] queryParams = append(params, size, offset);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT a.djx_assignmentid13 AS "assignmentId",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       cl.djx_classname13 AS "className",
                       t.djx_tname13 AS "teacherName",
                       a.djx_academicyear13 AS "academicYear",
                       a.djx_semester13 AS "semester",
                       COUNT(ev.djx_evaluationid13) AS "evaluationCount",
                       ROUND(AVG(ev.djx_rating13), 2) AS "averageRating"
                FROM dengjx_teachingassignments13 a
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = a.djx_classid13
                JOIN dengjx_teachers13 t ON t.djx_teacherid13 = a.djx_teacherid13
                LEFT JOIN dengjx_enrollments13 e ON e.djx_assignmentid13 = a.djx_assignmentid13
                LEFT JOIN dengjx_teachingevaluations13 ev ON ev.djx_enrollmentid13 = e.djx_enrollmentid13
                %s
                GROUP BY a.djx_assignmentid13,
                         c.djx_coursecode13,
                         c.djx_coursename13,
                         cl.djx_classname13,
                         t.djx_tname13,
                         a.djx_academicyear13,
                         a.djx_semester13
                ORDER BY a.djx_academicyear13 DESC, a.djx_semester13 DESC, c.djx_coursecode13
                LIMIT ? OFFSET ?
                """.formatted(whereClause), queryParams);
        Long total = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM dengjx_teachingassignments13 a
                %s
                """.formatted(whereClause), Long.class, params);
        return PageResult.of(rows, total == null ? 0 : total, page, size);
    }

    private List<Map<String, Object>> assignmentEvaluations(Long assignmentId) {
        return jdbcTemplate.queryForList("""
                SELECT ev.djx_evaluationid13 AS "evaluationId",
                       ev.djx_rating13 AS "rating",
                       ev.djx_comment13 AS "comment",
                       ev.djx_evaluatedat13 AS "evaluatedAt",
                       e.djx_enrollmentid13 AS "enrollmentId",
                       s.djx_sno13 AS "sno",
                       s.djx_sname13 AS "sname",
                       cl.djx_classname13 AS "studentClassName",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       a.djx_academicyear13 AS "academicYear",
                       a.djx_semester13 AS "semester"
                FROM dengjx_teachingevaluations13 ev
                JOIN dengjx_enrollments13 e ON e.djx_enrollmentid13 = ev.djx_enrollmentid13
                JOIN dengjx_students13 s ON s.djx_studentid13 = e.djx_studentid13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE e.djx_assignmentid13 = ?
                ORDER BY ev.djx_evaluatedat13 DESC, s.djx_sno13
                """, assignmentId);
    }

    private void requireTeacherOwnsAssignment(Long userId, Long assignmentId) {
        Long teacherId = userContextService.getTeacherId(userId);
        Assignment assignment = requireAssignment(assignmentId);
        if (!teacherId.equals(assignment.getTeacherId())) {
            throw new AccessDeniedException("只能查看本人任课课程的评价");
        }
    }

    private Assignment requireAssignment(Long assignmentId) {
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        return assignment;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评价等级必须在1到5之间");
        }
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.trim();
    }

    private Object[] append(Object[] values, Object... suffix) {
        Object[] result = new Object[values.length + suffix.length];
        System.arraycopy(values, 0, result, 0, values.length);
        System.arraycopy(suffix, 0, result, values.length, suffix.length);
        return result;
    }
}
