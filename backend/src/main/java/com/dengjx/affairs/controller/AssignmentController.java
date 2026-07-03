package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.AssignmentService;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
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
@RequestMapping("/api/admin/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ApiResponse<PageResult<Assignment>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(assignmentService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Assignment> detail(@PathVariable Long id) {
        return ApiResponse.ok(assignmentService.getById(id));
    }

    @PostMapping
    public ApiResponse<Assignment> create(@Valid @RequestBody AssignmentRequest request) {
        return ApiResponse.ok(assignmentService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Assignment> update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return ApiResponse.ok(assignmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ApiResponse.ok(null);
    }
}
