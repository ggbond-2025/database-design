package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MajorTransferSubmitRequest(
        @NotNull(message = "目标专业不能为空") Long targetMajorId,
        @Size(max = 500, message = "申请理由不能超过500字") String reason) {
}
