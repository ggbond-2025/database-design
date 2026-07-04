package com.dengjx.affairs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TeachingEvaluationRequest(
        @NotNull(message = "选课记录不能为空") Long enrollmentId,
        @NotNull(message = "评价等级不能为空")
        @Min(value = 1, message = "评价等级必须在1到5之间")
        @Max(value = 5, message = "评价等级必须在1到5之间") Integer rating,
        @Size(max = 500, message = "评价理由不能超过500字") String comment) {
}
