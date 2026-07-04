package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorCourseRequest;
import com.dengjx.affairs.entity.MajorCourse;

public interface MajorCourseService {

    PageResult<MajorCourse> list(String keyword, long page, long size);

    MajorCourse getById(Long id);

    MajorCourse create(MajorCourseRequest request);

    MajorCourse update(Long id, MajorCourseRequest request);

    void delete(Long id);
}
