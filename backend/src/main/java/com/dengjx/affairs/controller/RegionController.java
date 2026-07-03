package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.RegionService;
import com.dengjx.affairs.entity.Region;
import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.RegionRequest;
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
@RequestMapping("/api/admin/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ApiResponse<PageResult<Region>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(regionService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Region> detail(@PathVariable Long id) {
        return ApiResponse.ok(regionService.getById(id));
    }

    @PostMapping
    public ApiResponse<Region> create(@Valid @RequestBody RegionRequest request) {
        return ApiResponse.ok(regionService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Region> update(@PathVariable Long id, @Valid @RequestBody RegionRequest request) {
        return ApiResponse.ok(regionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        regionService.delete(id);
        return ApiResponse.ok(null);
    }
}
