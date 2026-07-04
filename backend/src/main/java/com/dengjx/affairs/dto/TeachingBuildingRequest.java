package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;

public record TeachingBuildingRequest(
        @NotBlank(message = "教学楼名称不能为空") String buildingName) {
}
