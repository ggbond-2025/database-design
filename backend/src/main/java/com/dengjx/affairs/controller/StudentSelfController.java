package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.service.StudentService;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentSelfController {

    private final StudentService studentService;

    public StudentSelfController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/api/student/profile/academic-status")
    public ApiResponse<Map<String, Object>> academicStatus(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(studentService.academicStatus(user.userId()));
    }

    @GetMapping("/api/student/classmates")
    public ApiResponse<List<Map<String, Object>>> classmates(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(studentService.classmates(user.userId()));
    }
}
