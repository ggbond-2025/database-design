package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.FinalExamRequest;
import com.dengjx.affairs.entity.FinalExam;
import com.dengjx.affairs.mapper.FinalExamMapper;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.service.FinalExamService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FinalExamServiceImpl implements FinalExamService {

    private final FinalExamMapper finalExamMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;

    public FinalExamServiceImpl(
            FinalExamMapper finalExamMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this.finalExamMapper = finalExamMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PageResult<FinalExam> list(String keyword, long page, long size) {
        LambdaQueryWrapper<FinalExam> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim();
            String likeKeyword = "%" + trimmedKeyword + "%";
            wrapper.and(query -> query
                    .like(FinalExam::getAcademicYear, trimmedKeyword)
                    .or().apply("""
                            djx_courseid13 IN (
                                SELECT djx_courseid13
                                FROM dengjx_courses13
                                WHERE djx_coursecode13 LIKE {0}
                                   OR djx_coursename13 LIKE {0}
                            )
                            """, likeKeyword));
        }
        wrapper.orderByAsc(FinalExam::getAcademicYear)
                .orderByAsc(FinalExam::getSemester)
                .orderByAsc(FinalExam::getCourseId);
        Page<FinalExam> result = finalExamMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public FinalExam getById(Long id) {
        FinalExam exam = finalExamMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("期末考试不存在");
        }
        return exam;
    }

    public FinalExam create(FinalExamRequest request) {
        validate(request, null);
        ensureCourseNotCompleted(request.courseId(), request.academicYear(), request.semester());
        FinalExam exam = new FinalExam();
        apply(request, exam);
        finalExamMapper.insert(exam);
        return exam;
    }

    public FinalExam update(Long id, FinalExamRequest request) {
        FinalExam exam = getById(id);
        ensureCourseNotCompleted(exam.getCourseId(), exam.getAcademicYear(), exam.getSemester());
        validate(request, id);
        ensureCourseNotCompleted(request.courseId(), request.academicYear(), request.semester());
        apply(request, exam);
        finalExamMapper.updateById(exam);
        return exam;
    }

    public void delete(Long id) {
        FinalExam exam = getById(id);
        ensureCourseNotCompleted(exam.getCourseId(), exam.getAcademicYear(), exam.getSemester());
        if (finalExamMapper.deleteById(id) == 0) {
            throw new BusinessException("期末考试不存在");
        }
    }

    public List<Map<String, Object>> studentMine(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        return jdbcTemplate.queryForList(baseExamSql("""
                WHERE e.djx_studentid13 = ?
                  AND e.djx_status13 = 'SELECTED'
                """), studentId);
    }

    public List<Map<String, Object>> teacherMine(Long userId) {
        Long teacherId = userContextService.getTeacherId(userId);
        return jdbcTemplate.queryForList(baseExamSql("""
                WHERE a.djx_teacherid13 = ?
                """), teacherId);
    }

    private void validate(FinalExamRequest request, Long currentId) {
        if (request.semester() == null || (request.semester() != 1 && request.semester() != 2)) {
            throw new BusinessException("学期必须为1或2");
        }
        LambdaQueryWrapper<FinalExam> wrapper = new LambdaQueryWrapper<FinalExam>()
                .eq(FinalExam::getCourseId, request.courseId())
                .eq(FinalExam::getAcademicYear, request.academicYear())
                .eq(FinalExam::getSemester, request.semester());
        if (currentId != null) {
            wrapper.ne(FinalExam::getExamId, currentId);
        }
        if (finalExamMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该课程本学年学期期末考试已存在");
        }
        validateStudentExamTimeConflict(request, currentId);
        validateTeacherExamTimeConflict(request, currentId);
        validateClassroomExamTimeConflict(request, currentId);
    }

    private void validateStudentExamTimeConflict(FinalExamRequest request, Long currentId) {
        String sql = """
                SELECT COUNT(*)
                FROM dengjx_finalexams13 existing_exam
                JOIN dengjx_majorcourses13 existing_mc
                    ON existing_mc.djx_courseid13 = existing_exam.djx_courseid13
                JOIN dengjx_teachingassignments13 existing_assignment
                    ON existing_assignment.djx_majorcourseid13 = existing_mc.djx_majorcourseid13
                    AND existing_assignment.djx_academicyear13 = existing_exam.djx_academicyear13
                    AND existing_assignment.djx_semester13 = existing_exam.djx_semester13
                JOIN dengjx_enrollments13 existing_enrollment
                    ON existing_enrollment.djx_assignmentid13 = existing_assignment.djx_assignmentid13
                    AND existing_enrollment.djx_status13 IN ('SELECTED', 'COMPLETED')
                JOIN dengjx_majorcourses13 candidate_mc
                    ON candidate_mc.djx_courseid13 = ?
                JOIN dengjx_teachingassignments13 candidate_assignment
                    ON candidate_assignment.djx_majorcourseid13 = candidate_mc.djx_majorcourseid13
                    AND candidate_assignment.djx_academicyear13 = ?
                    AND candidate_assignment.djx_semester13 = ?
                JOIN dengjx_enrollments13 candidate_student
                    ON candidate_student.djx_assignmentid13 = candidate_assignment.djx_assignmentid13
                    AND candidate_student.djx_studentid13 = existing_enrollment.djx_studentid13
                    AND candidate_student.djx_status13 IN ('SELECTED', 'COMPLETED')
                WHERE existing_exam.djx_academicyear13 = ?
                  AND existing_exam.djx_semester13 = ?
                  AND existing_exam.djx_examtime13 = ?
                  AND existing_exam.djx_courseid13 <> ?
                """;
        if (hasExamConflict(sql, request, currentId)) {
            throw new BusinessException("学生考试时间冲突：同一学生同一时间已有其他考试");
        }
    }

    private void validateTeacherExamTimeConflict(FinalExamRequest request, Long currentId) {
        String sql = """
                SELECT COUNT(*)
                FROM dengjx_finalexams13 existing_exam
                JOIN dengjx_majorcourses13 existing_mc
                    ON existing_mc.djx_courseid13 = existing_exam.djx_courseid13
                JOIN dengjx_teachingassignments13 existing_assignment
                    ON existing_assignment.djx_majorcourseid13 = existing_mc.djx_majorcourseid13
                    AND existing_assignment.djx_academicyear13 = existing_exam.djx_academicyear13
                    AND existing_assignment.djx_semester13 = existing_exam.djx_semester13
                JOIN dengjx_majorcourses13 candidate_mc
                    ON candidate_mc.djx_courseid13 = ?
                JOIN dengjx_teachingassignments13 candidate_teacher
                    ON candidate_teacher.djx_majorcourseid13 = candidate_mc.djx_majorcourseid13
                    AND candidate_teacher.djx_academicyear13 = ?
                    AND candidate_teacher.djx_semester13 = ?
                    AND candidate_teacher.djx_teacherid13 = existing_assignment.djx_teacherid13
                WHERE existing_exam.djx_academicyear13 = ?
                  AND existing_exam.djx_semester13 = ?
                  AND existing_exam.djx_examtime13 = ?
                  AND existing_exam.djx_courseid13 <> ?
                """;
        if (hasExamConflict(sql, request, currentId)) {
            throw new BusinessException("教师考试时间冲突：同一教师同一时间已有其他考试");
        }
    }

    private void validateClassroomExamTimeConflict(FinalExamRequest request, Long currentId) {
        String sql = """
                SELECT COUNT(*)
                FROM dengjx_finalexams13 existing_exam
                JOIN dengjx_majorcourses13 existing_mc
                    ON existing_mc.djx_courseid13 = existing_exam.djx_courseid13
                JOIN dengjx_teachingassignments13 existing_assignment
                    ON existing_assignment.djx_majorcourseid13 = existing_mc.djx_majorcourseid13
                    AND existing_assignment.djx_academicyear13 = existing_exam.djx_academicyear13
                    AND existing_assignment.djx_semester13 = existing_exam.djx_semester13
                JOIN dengjx_majorcourses13 candidate_mc
                    ON candidate_mc.djx_courseid13 = ?
                JOIN dengjx_teachingassignments13 candidate_classroom
                    ON candidate_classroom.djx_majorcourseid13 = candidate_mc.djx_majorcourseid13
                    AND candidate_classroom.djx_academicyear13 = ?
                    AND candidate_classroom.djx_semester13 = ?
                    AND candidate_classroom.djx_classroomid13 = existing_assignment.djx_classroomid13
                WHERE existing_exam.djx_academicyear13 = ?
                  AND existing_exam.djx_semester13 = ?
                  AND existing_exam.djx_examtime13 = ?
                  AND existing_exam.djx_courseid13 <> ?
                """;
        if (hasExamConflict(sql, request, currentId)) {
            throw new BusinessException("教室考试时间冲突：同一教室同一时间已有其他考试");
        }
    }

    private boolean hasExamConflict(String baseSql, FinalExamRequest request, Long currentId) {
        List<Object> args = new ArrayList<>();
        args.add(request.courseId());
        args.add(request.academicYear());
        args.add(request.semester());
        args.add(request.academicYear());
        args.add(request.semester());
        args.add(request.examTime());
        args.add(request.courseId());
        String sql = baseSql;
        if (currentId != null) {
            sql += "  AND existing_exam.djx_examid13 <> ?\n";
            args.add(currentId);
        }
        Long count = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());
        return count != null && count > 0;
    }

    private void ensureCourseNotCompleted(Long courseId, String academicYear, Integer semester) {
        Long completedCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM dengjx_enrollments13 e
                JOIN dengjx_teachingassignments13 a ON a.djx_assignmentid13 = e.djx_assignmentid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_majorcourseid13 = a.djx_majorcourseid13
                WHERE mc.djx_courseid13 = ?
                  AND a.djx_academicyear13 = ?
                  AND a.djx_semester13 = ?
                  AND e.djx_status13 = 'COMPLETED'
                """, Long.class, courseId, academicYear, semester);
        if (completedCount != null && completedCount > 0) {
            throw new BusinessException("课程已完成，不能再操作期末考试");
        }
    }

    private void apply(FinalExamRequest request, FinalExam exam) {
        exam.setCourseId(request.courseId());
        exam.setAcademicYear(request.academicYear());
        exam.setSemester(request.semester());
        exam.setExamTime(request.examTime());
    }

    private String baseExamSql(String whereClause) {
        return """
                SELECT DISTINCT
                       ex.djx_examid13 AS "examId",
                       c.djx_courseid13 AS "courseId",
                       c.djx_coursecode13 AS "courseCode",
                       c.djx_coursename13 AS "courseName",
                       ex.djx_academicyear13 AS "academicYear",
                       ex.djx_semester13 AS "semester",
                       ex.djx_examtime13 AS "examTime",
                       a.djx_assignmentid13 AS "assignmentId",
                       cl.djx_classname13 AS "className",
                       tb.djx_buildingname13 || ' ' || cr.djx_classroomname13 AS "classroomLabel"
                FROM dengjx_finalexams13 ex
                JOIN dengjx_courses13 c ON c.djx_courseid13 = ex.djx_courseid13
                JOIN dengjx_majorcourses13 mc ON mc.djx_courseid13 = c.djx_courseid13
                JOIN dengjx_teachingassignments13 a ON a.djx_majorcourseid13 = mc.djx_majorcourseid13
                    AND a.djx_academicyear13 = ex.djx_academicyear13
                    AND a.djx_semester13 = ex.djx_semester13
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = a.djx_classid13
                JOIN dengjx_classrooms13 cr ON cr.djx_classroomid13 = a.djx_classroomid13
                JOIN dengjx_teachingbuildings13 tb ON tb.djx_buildingid13 = cr.djx_buildingid13
                LEFT JOIN dengjx_enrollments13 e ON e.djx_assignmentid13 = a.djx_assignmentid13
                """ + whereClause + """
                ORDER BY ex.djx_examtime13, c.djx_coursecode13, cl.djx_classname13
                """;
    }
}
