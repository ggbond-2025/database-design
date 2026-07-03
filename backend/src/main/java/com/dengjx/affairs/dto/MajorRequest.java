package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;

public record MajorRequest(@NotBlank(message = "专业名称不能为空") String majorName) {
}
