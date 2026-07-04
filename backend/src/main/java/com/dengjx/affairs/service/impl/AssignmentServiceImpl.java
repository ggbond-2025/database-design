package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.service.AssignmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AssignmentServiceImpl(AssignmentMapper assignmentMapper, JdbcTemplate jdbcTemplate) {
        this.assignmentMapper = assignmentMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public AssignmentServiceImpl(AssignmentMapper assignmentMapper) {
        this(assignmentMapper, null);
    }

    public PageResult<Assignment> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Assignment::getAcademicYear, keyword);
        }
        wrapper.orderByAsc(Assignment::getAssignmentId);
        Page<Assignment> result = assignmentMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Assignment getById(Long id) {
        Assignment assignment = assignmentMapper.selectById(id);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        return assignment;
    }

    public Assignment create(AssignmentRequest request) {
        validate(request);
        Assignment assignment = new Assignment();
        apply(request, assignment);
        assignmentMapper.insert(assignment);
        return assignment;
    }

    public Assignment update(Long id, AssignmentRequest request) {
        validate(request);
        Assignment assignment = getById(id);
        apply(request, assignment);
        assignmentMapper.updateById(assignment);
        return assignment;
    }

    public void delete(Long id) {
        if (assignmentMapper.deleteById(id) == 0) {
            throw new BusinessException("开课安排不存在");
        }
    }

    private void validate(AssignmentRequest request) {
        if (request.semester() == null || (request.semester() != 1 && request.semester() != 2)) {
            throw new BusinessException("学期必须为1或2");
        }
        if (request.capacity() == null || request.capacity() <= 0) {
            throw new BusinessException("课程容量必须大于0");
        }
        validateSlot("第一个上课时间", request.weekdayOne(), request.startTimeOne(), request.endTimeOne());
        validateSlot("第二个上课时间", request.weekdayTwo(), request.startTimeTwo(), request.endTimeTwo());
        Integer hours = courseHours(request.majorCourseId());
        if (hours != null) {
            if (hours >= 48 && !hasSlot(request.weekdayTwo(), request.startTimeTwo(), request.endTimeTwo())) {
                throw new BusinessException(hours + "学时课程需要配置第二个上课时间");
            }
            if (hours == 32 && !hasSlot(request.weekdayOne(), request.startTimeOne(), request.endTimeOne())) {
                throw new BusinessException("32学时课程需要配置第一个上课时间");
            }
        }
    }

    private void apply(AssignmentRequest request, Assignment assignment) {
        assignment.setMajorCourseId(request.majorCourseId());
        assignment.setClassId(request.classId());
        assignment.setTeacherId(request.teacherId());
        assignment.setClassroomId(request.classroomId());
        assignment.setAcademicYear(request.academicYear());
        assignment.setSemester(request.semester());
        assignment.setCapacity(request.capacity());
        assignment.setEnrollmentOpen(Boolean.TRUE.equals(request.enrollmentOpen()));
        assignment.setWeekdayOne(request.weekdayOne());
        assignment.setStartTimeOne(request.startTimeOne());
        assignment.setEndTimeOne(request.endTimeOne());
        assignment.setWeekdayTwo(request.weekdayTwo());
        assignment.setStartTimeTwo(request.startTimeTwo());
        assignment.setEndTimeTwo(request.endTimeTwo());
    }

    private void validateSlot(String label, Integer weekday, LocalTime startTime, LocalTime endTime) {
        boolean hasAny = weekday != null || startTime != null || endTime != null;
        if (!hasAny) {
            return;
        }
        if (!hasSlot(weekday, startTime, endTime)) {
            throw new BusinessException(label + "必须完整配置星期、开始时间和结束时间");
        }
        if (weekday < 1 || weekday > 5) {
            throw new BusinessException(label + "的星期必须为周一到周五");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(label + "的开始时间必须早于结束时间");
        }
    }

    private boolean hasSlot(Integer weekday, LocalTime startTime, LocalTime endTime) {
        return weekday != null && startTime != null && endTime != null;
    }

    private Integer courseHours(Long majorCourseId) {
        if (jdbcTemplate == null || majorCourseId == null) {
            return null;
        }
        return jdbcTemplate.queryForObject("""
                SELECT c.djx_hours13
                FROM dengjx_majorcourses13 mc
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE mc.djx_majorcourseid13 = ?
                """, Integer.class, majorCourseId);
    }
}
