package com.dengjx.affairs.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MajorRequest(
        @NotBlank(message = "专业名称不能为空") String majorName,
        @NotNull(message = "毕业学分不能为空") BigDecimal graduationCredits) {
}
