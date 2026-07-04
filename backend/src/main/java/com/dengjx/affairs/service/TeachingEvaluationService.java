package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingEvaluationRequest;
import com.dengjx.affairs.entity.TeachingEvaluation;
import java.util.List;
import java.util.Map;

public interface TeachingEvaluationService {

    List<Map<String, Object>> studentCompletedCourses(Long userId);

    TeachingEvaluation studentCreate(Long userId, TeachingEvaluationRequest request);

    PageResult<Map<String, Object>> teacherAssignments(Long userId, long page, long size);

    List<Map<String, Object>> teacherAssignmentEvaluations(Long userId, Long assignmentId);

    List<Map<String, Object>> adminTeachers();

    PageResult<Map<String, Object>> adminTeacherAssignments(Long teacherId, long page, long size);

    List<Map<String, Object>> adminAssignmentEvaluations(Long assignmentId);
}
