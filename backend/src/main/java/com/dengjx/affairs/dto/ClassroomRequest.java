package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClassroomRequest(
        @NotNull(message = "教学楼不能为空") Long buildingId,
        @NotBlank(message = "教室名称不能为空") String classroomName,
        @NotNull(message = "教室容量不能为空") Integer capacity) {
}
