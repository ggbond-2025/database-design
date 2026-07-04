package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.ClassroomRequest;
import com.dengjx.affairs.entity.Classroom;
import com.dengjx.affairs.service.ClassroomService;
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
@RequestMapping("/api/admin/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ApiResponse<PageResult<Classroom>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(classroomService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Classroom> detail(@PathVariable Long id) {
        return ApiResponse.ok(classroomService.getById(id));
    }

    @PostMapping
    public ApiResponse<Classroom> create(@Valid @RequestBody ClassroomRequest request) {
        return ApiResponse.ok(classroomService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Classroom> update(@PathVariable Long id, @Valid @RequestBody ClassroomRequest request) {
        return ApiResponse.ok(classroomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return ApiResponse.ok(null);
    }
}
