package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.entity.Student;

public interface StudentService {

    PageResult<Student> list(String keyword, long page, long size);

    Student getById(Long id);

    Student create(StudentRequest request);

    Student update(Long id, StudentRequest request);

    void delete(Long id);
}
