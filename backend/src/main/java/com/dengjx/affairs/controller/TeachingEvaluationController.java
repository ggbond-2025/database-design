package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingEvaluationRequest;
import com.dengjx.affairs.entity.TeachingEvaluation;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.service.TeachingEvaluationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeachingEvaluationController {

    private final TeachingEvaluationService evaluationService;

    public TeachingEvaluationController(TeachingEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/api/student/evaluations/courses")
    public ApiResponse<List<Map<String, Object>>> studentCompletedCourses(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(evaluationService.studentCompletedCourses(user.userId()));
    }

    @PostMapping("/api/student/evaluations")
    public ApiResponse<TeachingEvaluation> studentCreate(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody TeachingEvaluationRequest request) {
        return ApiResponse.ok(evaluationService.studentCreate(user.userId(), request));
    }

    @GetMapping("/api/teacher/evaluations/assignments")
    public ApiResponse<PageResult<Map<String, Object>>> teacherAssignments(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(evaluationService.teacherAssignments(user.userId(), page, size));
    }

    @GetMapping("/api/teacher/evaluations/assignments/{assignmentId}")
    public ApiResponse<List<Map<String, Object>>> teacherAssignmentEvaluations(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return ApiResponse.ok(evaluationService.teacherAssignmentEvaluations(user.userId(), assignmentId));
    }

    @GetMapping("/api/admin/evaluations/teachers")
    public ApiResponse<List<Map<String, Object>>> adminTeachers() {
        return ApiResponse.ok(evaluationService.adminTeachers());
    }

    @GetMapping("/api/admin/evaluations/teachers/{teacherId}/assignments")
    public ApiResponse<PageResult<Map<String, Object>>> adminTeacherAssignments(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(evaluationService.adminTeacherAssignments(teacherId, page, size));
    }

    @GetMapping("/api/admin/evaluations/assignments/{assignmentId}")
    public ApiResponse<List<Map<String, Object>>> adminAssignmentEvaluations(@PathVariable Long assignmentId) {
        return ApiResponse.ok(evaluationService.adminAssignmentEvaluations(assignmentId));
    }
}
