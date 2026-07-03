package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;

public record RegionRequest(@NotBlank(message = "地区名称不能为空") String regionName) {
}
