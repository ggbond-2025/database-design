package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.SchoolClassService;
import com.dengjx.affairs.entity.SchoolClass;
import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.SchoolClassRequest;
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
@RequestMapping("/api/admin/classes")
public class SchoolClassController {

    private final SchoolClassService classService;

    public SchoolClassController(SchoolClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ApiResponse<PageResult<SchoolClass>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(classService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<SchoolClass> detail(@PathVariable Long id) {
        return ApiResponse.ok(classService.getById(id));
    }

    @PostMapping
    public ApiResponse<SchoolClass> create(@Valid @RequestBody SchoolClassRequest request) {
        return ApiResponse.ok(classService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SchoolClass> update(@PathVariable Long id, @Valid @RequestBody SchoolClassRequest request) {
        return ApiResponse.ok(classService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return ApiResponse.ok(null);
    }
}
