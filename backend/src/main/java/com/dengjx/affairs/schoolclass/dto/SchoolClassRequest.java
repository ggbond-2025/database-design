package com.dengjx.affairs.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SchoolClassRequest(
        @NotBlank(message = "班级名称不能为空") String className,
        @NotNull(message = "专业不能为空") Long majorId,
        @NotNull(message = "年级不能为空") Integer gradeYear) {
}
