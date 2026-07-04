package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.FinalExamRequest;
import com.dengjx.affairs.entity.FinalExam;
import com.dengjx.affairs.service.FinalExamService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/final-exams")
public class AdminFinalExamController {

    private final FinalExamService finalExamService;

    public AdminFinalExamController(FinalExamService finalExamService) {
        this.finalExamService = finalExamService;
    }

    @GetMapping
    public ApiResponse<PageResult<FinalExam>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(finalExamService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<FinalExam> detail(@PathVariable Long id) {
        return ApiResponse.ok(finalExamService.getById(id));
    }

    @PostMapping
    public ApiResponse<FinalExam> create(@Valid @RequestBody FinalExamRequest request) {
        return ApiResponse.ok(finalExamService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FinalExam> update(@PathVariable Long id, @Valid @RequestBody FinalExamRequest request) {
        return ApiResponse.ok(finalExamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        finalExamService.delete(id);
        return ApiResponse.ok(null);
    }
}
