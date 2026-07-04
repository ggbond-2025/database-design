package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentSettingRequest(@NotNull(message = "选课开关不能为空") Boolean enabled) {
}
