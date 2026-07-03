package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.AuthService;
import com.dengjx.affairs.dto.ChangePasswordRequest;
import com.dengjx.affairs.dto.CurrentUserProfileResponse;
import com.dengjx.affairs.dto.LoginRequest;
import com.dengjx.affairs.dto.LoginResponse;
import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(user.userId(), request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserProfileResponse> currentProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(authService.currentProfile(user.userId()));
    }
}
