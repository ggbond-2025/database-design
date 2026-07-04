package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FinalExamRequest(
        @NotNull(message = "课程不能为空") Long courseId,
        @NotBlank(message = "学年不能为空") String academicYear,
        @NotNull(message = "学期不能为空") Integer semester,
        @NotNull(message = "考试时间不能为空") LocalDateTime examTime) {
}
