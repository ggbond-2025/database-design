package com.dengjx.affairs.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record GradeRequest(
        @NotNull(message = "选课记录不能为空") Long enrollmentId,
        @NotNull(message = "成绩不能为空") BigDecimal score) {
}
