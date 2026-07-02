package com.dengjx.affairs.course;

import java.math.BigDecimal;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.course.dto.CourseRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CourseService {

    private static final Set<String> ASSESSMENT_TYPES = Set.of("EXAM", "CHECK");

    private final CourseMapper courseMapper;

    public CourseService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public PageResult<Course> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getCourseCode, keyword).or().like(Course::getCourseName, keyword);
        }
        wrapper.orderByAsc(Course::getCourseId);
        Page<Course> result = courseMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Course getById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    public Course create(CourseRequest request) {
        validate(request);
        Course course = new Course();
        apply(request, course);
        courseMapper.insert(course);
        return course;
    }

    public Course update(Long id, CourseRequest request) {
        validate(request);
        Course course = getById(id);
        apply(request, course);
        courseMapper.updateById(course);
        return course;
    }

    public void delete(Long id) {
        if (courseMapper.deleteById(id) == 0) {
            throw new BusinessException("课程不存在");
        }
    }

    private void validate(CourseRequest request) {
        if (request.hours() == null || request.hours() <= 0) {
            throw new BusinessException("课程学时必须大于0");
        }
        if (request.credit() == null || request.credit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("课程学分必须大于0");
        }
        if (!ASSESSMENT_TYPES.contains(request.assessmentType())) {
            throw new BusinessException("考核方式必须为EXAM或CHECK");
        }
    }

    private void apply(CourseRequest request, Course course) {
        course.setCourseCode(request.courseCode());
        course.setCourseName(request.courseName());
        course.setHours(request.hours());
        course.setAssessmentType(request.assessmentType());
        course.setCredit(request.credit());
    }
}
