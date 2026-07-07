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

    default PageResult<Map<String, Object>> adminAssignmentList(long page, long size) {
        return adminAssignmentList(null, page, size);
    }

    PageResult<Map<String, Object>> adminAssignmentList(String keyword, long page, long size);

    List<Map<String, Object>> adminAssignmentGrades(Long assignmentId);

    PageResult<Grade> adminList(long page, long size);

    Grade adminCreate(GradeRequest request);

    Grade adminUpdate(Long id, GradeRequest request);

    void adminDelete(Long id);
}
