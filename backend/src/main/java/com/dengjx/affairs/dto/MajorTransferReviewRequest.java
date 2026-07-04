package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MajorTransferReviewRequest(
        @NotBlank(message = "审核状态不能为空") String status,
        Long targetClassId,
        @Size(max = 500, message = "审核意见不能超过500字") String reviewComment) {
}
