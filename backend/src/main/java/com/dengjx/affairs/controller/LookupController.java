package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.dto.LookupOption;
import com.dengjx.affairs.service.LookupService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LookupController {

    private final LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/api/admin/lookups/students")
    public ApiResponse<List<LookupOption>> students() {
        return ApiResponse.ok(lookupService.studentOptions());
    }

    @GetMapping("/api/admin/lookups/regions")
    public ApiResponse<List<LookupOption>> regions() {
        return ApiResponse.ok(lookupService.regionOptions());
    }

    @GetMapping("/api/admin/lookups/majors")
    public ApiResponse<List<LookupOption>> majors() {
        return ApiResponse.ok(lookupService.majorOptions());
    }

    @GetMapping("/api/student/lookups/majors")
    public ApiResponse<List<LookupOption>> studentMajors() {
        return ApiResponse.ok(lookupService.majorOptions());
    }

    @GetMapping("/api/admin/lookups/teachers")
    public ApiResponse<List<LookupOption>> teachers() {
        return ApiResponse.ok(lookupService.teacherOptions());
    }

    @GetMapping("/api/admin/lookups/courses")
    public ApiResponse<List<LookupOption>> courses() {
        return ApiResponse.ok(lookupService.courseOptions());
    }

    @GetMapping("/api/admin/lookups/major-courses")
    public ApiResponse<List<LookupOption>> majorCourses() {
        return ApiResponse.ok(lookupService.majorCourseOptions());
    }

    @GetMapping("/api/admin/lookups/classes")
    public ApiResponse<List<LookupOption>> classes() {
        return ApiResponse.ok(lookupService.classOptions());
    }

    @GetMapping("/api/admin/lookups/teaching-buildings")
    public ApiResponse<List<LookupOption>> teachingBuildings() {
        return ApiResponse.ok(lookupService.teachingBuildingOptions());
    }

    @GetMapping("/api/admin/lookups/classrooms")
    public ApiResponse<List<LookupOption>> classrooms() {
        return ApiResponse.ok(lookupService.classroomOptions());
    }

    @GetMapping("/api/admin/lookups/assignments")
    public ApiResponse<List<LookupOption>> assignments() {
        return ApiResponse.ok(lookupService.assignmentOptions());
    }

    @GetMapping("/api/admin/lookups/active-enrollments")
    public ApiResponse<List<LookupOption>> activeEnrollments() {
        return ApiResponse.ok(lookupService.activeEnrollmentOptions());
    }
}
