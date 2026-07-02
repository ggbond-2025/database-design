package com.dengjx.affairs.major;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.major.dto.MajorRequest;
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
@RequestMapping("/api/admin/majors")
public class MajorController {

    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    public ApiResponse<PageResult<Major>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(majorService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Major> detail(@PathVariable Long id) {
        return ApiResponse.ok(majorService.getById(id));
    }

    @PostMapping
    public ApiResponse<Major> create(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.ok(majorService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Major> update(@PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        return ApiResponse.ok(majorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        majorService.delete(id);
        return ApiResponse.ok(null);
    }
}
