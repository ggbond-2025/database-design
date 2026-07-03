package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.CourseRequest;
import com.dengjx.affairs.entity.Course;

public interface CourseService {

    PageResult<Course> list(String keyword, long page, long size);

    Course getById(Long id);

    Course create(CourseRequest request);

    Course update(Long id, CourseRequest request);

    void delete(Long id);
}
