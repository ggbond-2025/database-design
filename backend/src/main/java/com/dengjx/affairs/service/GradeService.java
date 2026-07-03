package com.dengjx.affairs.service;

import java.util.List;
import java.util.Map;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.GradeRequest;
import com.dengjx.affairs.entity.Grade;

public interface GradeService {

    List<Map<String, Object>> studentGrades(Long userId);

    List<Map<String, Object>> teacherAssignmentGrades(Long userId, Long assignmentId);

    Grade teacherCreate(Long userId, GradeRequest request);

    Grade teacherUpdate(Long userId, Long id, GradeRequest request);

    PageResult<Grade> adminList(long page, long size);

    Grade adminCreate(GradeRequest request);

    Grade adminUpdate(Long id, GradeRequest request);

    void adminDelete(Long id);
}
