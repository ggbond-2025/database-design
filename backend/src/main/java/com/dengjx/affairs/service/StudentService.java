package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.entity.Student;
import java.util.List;
import java.util.Map;

public interface StudentService {

    PageResult<Student> list(String keyword, long page, long size);

    Student getById(Long id);

    Student create(StudentRequest request);

    Student update(Long id, StudentRequest request);

    void delete(Long id);

    Map<String, Object> academicStatus(Long userId);

    List<Map<String, Object>> classmates(Long userId);
}
