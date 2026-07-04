package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.GradeService;
import com.dengjx.affairs.entity.Grade;
import java.util.List;
import java.util.Map;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.GradeRequest;
import com.dengjx.affairs.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping("/api/student/grades/mine")
    public ApiResponse<List<Map<String, Object>>> studentGrades(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(gradeService.studentGrades(user.userId()));
    }

    @GetMapping("/api/teacher/grades/assignments/{assignmentId}")
    public ApiResponse<List<Map<String, Object>>> teacherAssignmentGrades(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return ApiResponse.ok(gradeService.teacherAssignmentGrades(user.userId(), assignmentId));
    }

    @PostMapping("/api/teacher/grades")
    public ApiResponse<Grade> teacherCreate(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody GradeRequest request) {
        return ApiResponse.ok(gradeService.teacherCreate(user.userId(), request));
    }

    @PutMapping("/api/teacher/grades/{id}")
    public ApiResponse<Grade> teacherUpdate(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        return ApiResponse.ok(gradeService.teacherUpdate(user.userId(), id, request));
    }

    @GetMapping("/api/admin/grades/assignments")
    public ApiResponse<PageResult<Map<String, Object>>> adminAssignmentList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(gradeService.adminAssignmentList(page, size));
    }

    @GetMapping("/api/admin/grades/assignments/{assignmentId}/students")
    public ApiResponse<List<Map<String, Object>>> adminAssignmentGrades(@PathVariable Long assignmentId) {
        return ApiResponse.ok(gradeService.adminAssignmentGrades(assignmentId));
    }

    @GetMapping("/api/admin/grades")
    public ApiResponse<PageResult<Grade>> adminList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(gradeService.adminList(page, size));
    }

    @PostMapping("/api/admin/grades")
    public ApiResponse<Grade> adminCreate(@Valid @RequestBody GradeRequest request) {
        return ApiResponse.ok(gradeService.adminCreate(request));
    }

    @PutMapping("/api/admin/grades/{id}")
    public ApiResponse<Grade> adminUpdate(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        return ApiResponse.ok(gradeService.adminUpdate(id, request));
    }

    @DeleteMapping("/api/admin/grades/{id}")
    public ApiResponse<Void> adminDelete(@PathVariable Long id) {
        gradeService.adminDelete(id);
        return ApiResponse.ok(null);
    }
}
