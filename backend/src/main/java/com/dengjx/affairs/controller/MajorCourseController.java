package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorCourseRequest;
import com.dengjx.affairs.entity.MajorCourse;
import com.dengjx.affairs.service.MajorCourseService;
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
@RequestMapping("/api/admin/major-courses")
public class MajorCourseController {

    private final MajorCourseService majorCourseService;

    public MajorCourseController(MajorCourseService majorCourseService) {
        this.majorCourseService = majorCourseService;
    }

    @GetMapping
    public ApiResponse<PageResult<MajorCourse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(majorCourseService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<MajorCourse> detail(@PathVariable Long id) {
        return ApiResponse.ok(majorCourseService.getById(id));
    }

    @PostMapping
    public ApiResponse<MajorCourse> create(@Valid @RequestBody MajorCourseRequest request) {
        return ApiResponse.ok(majorCourseService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MajorCourse> update(@PathVariable Long id, @Valid @RequestBody MajorCourseRequest request) {
        return ApiResponse.ok(majorCourseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        majorCourseService.delete(id);
        return ApiResponse.ok(null);
    }
}
