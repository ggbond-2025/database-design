package com.dengjx.affairs.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssignmentRequest(
        @NotNull(message = "课程不能为空") Long courseId,
        @NotNull(message = "班级不能为空") Long classId,
        @NotNull(message = "教师不能为空") Long teacherId,
        @NotBlank(message = "学年不能为空") String academicYear,
        @NotNull(message = "学期不能为空") Integer semester,
        @NotBlank(message = "课程类型不能为空") String courseType,
        @NotNull(message = "容量不能为空") Integer capacity,
        Boolean enrollmentOpen) {
}
