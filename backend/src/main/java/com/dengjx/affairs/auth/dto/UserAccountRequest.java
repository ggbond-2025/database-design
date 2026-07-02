package com.dengjx.affairs.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAccountRequest(
        @NotBlank(message = "用户名不能为空") String username,
        String password,
        @NotBlank(message = "角色不能为空") String role,
        Long studentId,
        Long teacherId,
        Boolean enabled) {
}
