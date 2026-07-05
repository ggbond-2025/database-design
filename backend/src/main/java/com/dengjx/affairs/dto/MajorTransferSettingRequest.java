package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotNull;

public record MajorTransferSettingRequest(@NotNull(message = "转专业申请开关不能为空") Boolean enabled) {
}
