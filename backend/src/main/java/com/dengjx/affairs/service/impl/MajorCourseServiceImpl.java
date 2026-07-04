package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorCourseRequest;
import com.dengjx.affairs.entity.MajorCourse;
import com.dengjx.affairs.mapper.MajorCourseMapper;
import com.dengjx.affairs.service.MajorCourseService;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MajorCourseServiceImpl implements MajorCourseService {

    private static final Set<String> COURSE_TYPES = Set.of("REQUIRED", "ELECTIVE");

    private final MajorCourseMapper majorCourseMapper;

    public MajorCourseServiceImpl(MajorCourseMapper majorCourseMapper) {
        this.majorCourseMapper = majorCourseMapper;
    }

    public PageResult<MajorCourse> list(String keyword, long page, long size) {
        LambdaQueryWrapper<MajorCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MajorCourse::getMajorId)
                .orderByAsc(MajorCourse::getTargetGrade)
                .orderByAsc(MajorCourse::getTargetSemester)
                .orderByAsc(MajorCourse::getCourseId);
        Page<MajorCourse> result = majorCourseMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public MajorCourse getById(Long id) {
        MajorCourse majorCourse = majorCourseMapper.selectById(id);
        if (majorCourse == null) {
            throw new BusinessException("专业课程计划不存在");
        }
        return majorCourse;
    }

    public MajorCourse create(MajorCourseRequest request) {
        validate(request);
        MajorCourse majorCourse = new MajorCourse();
        apply(request, majorCourse);
        majorCourseMapper.insert(majorCourse);
        return majorCourse;
    }

    public MajorCourse update(Long id, MajorCourseRequest request) {
        validate(request);
        MajorCourse majorCourse = getById(id);
        apply(request, majorCourse);
        majorCourseMapper.updateById(majorCourse);
        return majorCourse;
    }

    public void delete(Long id) {
        if (majorCourseMapper.deleteById(id) == 0) {
            throw new BusinessException("专业课程计划不存在");
        }
    }

    private void validate(MajorCourseRequest request) {
        if (!COURSE_TYPES.contains(request.courseType())) {
            throw new BusinessException("课程类型必须为REQUIRED或ELECTIVE");
        }
        if (request.targetGrade() == null || request.targetGrade() < 1 || request.targetGrade() > 4) {
            throw new BusinessException("开设年级必须在1到4之间");
        }
        if (request.targetSemester() == null || (request.targetSemester() != 1 && request.targetSemester() != 2)) {
            throw new BusinessException("开设学期必须为1或2");
        }
    }

    private void apply(MajorCourseRequest request, MajorCourse majorCourse) {
        majorCourse.setMajorId(request.majorId());
        majorCourse.setCourseId(request.courseId());
        majorCourse.setCourseType(request.courseType());
        majorCourse.setTargetGrade(request.targetGrade());
        majorCourse.setTargetSemester(request.targetSemester());
    }
}
