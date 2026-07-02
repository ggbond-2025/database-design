package com.dengjx.affairs.course.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseRequest(
        @NotBlank(message = "课程编号不能为空") String courseCode,
        @NotBlank(message = "课程名称不能为空") String courseName,
        @NotNull(message = "学时不能为空") Integer hours,
        @NotBlank(message = "考核方式不能为空") String assessmentType,
        @NotNull(message = "学分不能为空") BigDecimal credit) {
}
