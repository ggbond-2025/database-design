package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.EnrollmentService;
import com.dengjx.affairs.entity.Enrollment;
import java.util.List;
import java.util.Map;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.EnrollmentRequest;
import com.dengjx.affairs.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/api/student/enrollments/available")
    public ApiResponse<List<Map<String, Object>>> available(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(enrollmentService.available(user.userId()));
    }

    @PostMapping("/api/student/enrollments/{assignmentId}")
    public ApiResponse<Enrollment> enroll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return ApiResponse.ok(enrollmentService.enroll(user.userId(), assignmentId));
    }

    @DeleteMapping("/api/student/enrollments/{assignmentId}")
    public ApiResponse<Enrollment> drop(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return ApiResponse.ok(enrollmentService.studentDrop(user.userId(), assignmentId));
    }

    @GetMapping("/api/student/enrollments/mine")
    public ApiResponse<List<Map<String, Object>>> mine(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(enrollmentService.mine(user.userId()));
    }

    @GetMapping("/api/teacher/enrollments/assignments/{assignmentId}/students")
    public ApiResponse<List<Map<String, Object>>> assignmentStudents(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return ApiResponse.ok(enrollmentService.assignmentStudents(user, assignmentId));
    }

    @GetMapping("/api/admin/enrollments")
    public ApiResponse<PageResult<Map<String, Object>>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(enrollmentService.adminList(page, size));
    }

    @PostMapping("/api/admin/enrollments")
    public ApiResponse<Enrollment> adminCreate(@Valid @RequestBody EnrollmentRequest request) {
        return ApiResponse.ok(enrollmentService.adminCreate(request));
    }

    @DeleteMapping("/api/admin/enrollments/{id}")
    public ApiResponse<Enrollment> adminDrop(@PathVariable Long id) {
        return ApiResponse.ok(enrollmentService.adminDrop(id));
    }
}
