package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "学生不能为空") Long studentId,
        @NotNull(message = "开课安排不能为空") Long assignmentId,
        String status) {
}
