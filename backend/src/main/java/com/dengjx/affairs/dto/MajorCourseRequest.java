package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MajorCourseRequest(
        @NotNull(message = "专业不能为空") Long majorId,
        @NotNull(message = "课程不能为空") Long courseId,
        @NotBlank(message = "课程类型不能为空") String courseType,
        @NotNull(message = "开设年级不能为空") Integer targetGrade,
        @NotNull(message = "开设学期不能为空") Integer targetSemester) {
}
