package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingBuildingRequest;
import com.dengjx.affairs.entity.TeachingBuilding;
import com.dengjx.affairs.service.TeachingBuildingService;
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
@RequestMapping("/api/admin/teaching-buildings")
public class TeachingBuildingController {

    private final TeachingBuildingService teachingBuildingService;

    public TeachingBuildingController(TeachingBuildingService teachingBuildingService) {
        this.teachingBuildingService = teachingBuildingService;
    }

    @GetMapping
    public ApiResponse<PageResult<TeachingBuilding>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(teachingBuildingService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<TeachingBuilding> detail(@PathVariable Long id) {
        return ApiResponse.ok(teachingBuildingService.getById(id));
    }

    @PostMapping
    public ApiResponse<TeachingBuilding> create(@Valid @RequestBody TeachingBuildingRequest request) {
        return ApiResponse.ok(teachingBuildingService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TeachingBuilding> update(@PathVariable Long id, @Valid @RequestBody TeachingBuildingRequest request) {
        return ApiResponse.ok(teachingBuildingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        teachingBuildingService.delete(id);
        return ApiResponse.ok(null);
    }
}
