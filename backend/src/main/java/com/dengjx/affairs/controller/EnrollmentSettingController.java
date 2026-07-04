package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.dto.EnrollmentSettingRequest;
import com.dengjx.affairs.service.EnrollmentSettingService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentSettingController {

    private final EnrollmentSettingService enrollmentSettingService;

    public EnrollmentSettingController(EnrollmentSettingService enrollmentSettingService) {
        this.enrollmentSettingService = enrollmentSettingService;
    }

    @GetMapping("/api/admin/enrollment-setting")
    public ApiResponse<Map<String, Object>> current() {
        return ApiResponse.ok(enrollmentSettingService.current());
    }

    @PutMapping("/api/admin/enrollment-setting")
    public ApiResponse<Map<String, Object>> update(@Valid @RequestBody EnrollmentSettingRequest request) {
        return ApiResponse.ok(enrollmentSettingService.update(request));
    }
}
