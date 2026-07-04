package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record AssignmentRequest(
        @NotNull(message = "专业课程计划不能为空") Long majorCourseId,
        @NotNull(message = "班级不能为空") Long classId,
        @NotNull(message = "教师不能为空") Long teacherId,
        @NotNull(message = "上课教室不能为空") Long classroomId,
        @NotBlank(message = "学年不能为空") String academicYear,
        @NotNull(message = "学期不能为空") Integer semester,
        @NotNull(message = "容量不能为空") Integer capacity,
        Boolean enrollmentOpen,
        Integer weekdayOne,
        LocalTime startTimeOne,
        LocalTime endTimeOne,
        Integer weekdayTwo,
        LocalTime startTimeTwo,
        LocalTime endTimeTwo) {
}
