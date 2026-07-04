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
            wrapper.like(FinalExam::getAcademicYear, keyword);
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
