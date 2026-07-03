package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeacherRequest;
import com.dengjx.affairs.entity.Teacher;

public interface TeacherService {

    PageResult<Teacher> list(String keyword, long page, long size);

    Teacher getById(Long id);

    Teacher create(TeacherRequest request);

    Teacher update(Long id, TeacherRequest request);

    void delete(Long id);
}
