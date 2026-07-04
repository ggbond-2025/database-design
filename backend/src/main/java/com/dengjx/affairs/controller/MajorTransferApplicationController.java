package com.dengjx.affairs.controller;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorTransferReviewRequest;
import com.dengjx.affairs.dto.MajorTransferSubmitRequest;
import com.dengjx.affairs.entity.MajorTransferApplication;
import com.dengjx.affairs.security.AuthenticatedUser;
import com.dengjx.affairs.service.MajorTransferApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MajorTransferApplicationController {

    private final MajorTransferApplicationService applicationService;

    public MajorTransferApplicationController(MajorTransferApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/api/admin/major-transfer-applications")
    public ApiResponse<PageResult<Map<String, Object>>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(applicationService.adminList(keyword, page, size));
    }

    @PutMapping("/api/admin/major-transfer-applications/{id}/review")
    public ApiResponse<MajorTransferApplication> review(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody MajorTransferReviewRequest request) {
        return ApiResponse.ok(applicationService.review(user.userId(), id, request));
    }

    @GetMapping("/api/student/major-transfer-applications")
    public ApiResponse<List<Map<String, Object>>> studentMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(applicationService.studentMine(user.userId()));
    }

    @PostMapping("/api/student/major-transfer-applications")
    public ApiResponse<MajorTransferApplication> submit(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MajorTransferSubmitRequest request) {
        return ApiResponse.ok(applicationService.submit(user.userId(), request));
    }
}
