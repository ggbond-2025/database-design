package com.dengjx.affairs.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRequest(
        @NotBlank(message = "学号不能为空") String sno,
        @NotBlank(message = "姓名不能为空") String sname,
        @NotBlank(message = "性别不能为空") String gender,
        @NotNull(message = "年龄不能为空") Integer age,
        @NotNull(message = "班级不能为空") Long classId,
        @NotNull(message = "地区不能为空") Long regionId) {
}
