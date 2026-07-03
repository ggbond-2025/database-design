package com.dengjx.affairs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeacherRequest(
        @NotBlank(message = "教师编号不能为空") String tno,
        @NotBlank(message = "教师姓名不能为空") String tname,
        @NotBlank(message = "性别不能为空") String gender,
        @NotNull(message = "年龄不能为空") Integer age,
        @NotBlank(message = "职称不能为空") String title,
        @NotBlank(message = "联系电话不能为空") String phone) {
}
