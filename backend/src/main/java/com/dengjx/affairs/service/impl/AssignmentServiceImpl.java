package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.service.AssignmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(AssignmentMapper assignmentMapper) {
        this.assignmentMapper = assignmentMapper;
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
    }

    private void apply(AssignmentRequest request, Assignment assignment) {
        assignment.setMajorCourseId(request.majorCourseId());
        assignment.setClassId(request.classId());
        assignment.setTeacherId(request.teacherId());
        assignment.setAcademicYear(request.academicYear());
        assignment.setSemester(request.semester());
        assignment.setCapacity(request.capacity());
        assignment.setEnrollmentOpen(Boolean.TRUE.equals(request.enrollmentOpen()));
    }
}
