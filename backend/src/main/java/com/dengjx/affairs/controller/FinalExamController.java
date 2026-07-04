package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.service.FinalExamService;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinalExamController {

    private final FinalExamService finalExamService;

    public FinalExamController(FinalExamService finalExamService) {
        this.finalExamService = finalExamService;
    }

    @GetMapping("/api/student/final-exams")
    public ApiResponse<List<Map<String, Object>>> studentMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(finalExamService.studentMine(user.userId()));
    }

    @GetMapping("/api/teacher/final-exams")
    public ApiResponse<List<Map<String, Object>>> teacherMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(finalExamService.teacherMine(user.userId()));
    }
}
