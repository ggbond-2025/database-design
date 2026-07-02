package com.dengjx.affairs.teacher;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.teacher.dto.TeacherRequest;
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
@RequestMapping("/api/admin/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ApiResponse<PageResult<Teacher>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(teacherService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Teacher> detail(@PathVariable Long id) {
        return ApiResponse.ok(teacherService.getById(id));
    }

    @PostMapping
    public ApiResponse<Teacher> create(@Valid @RequestBody TeacherRequest request) {
        return ApiResponse.ok(teacherService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Teacher> update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return ApiResponse.ok(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ApiResponse.ok(null);
    }
}
