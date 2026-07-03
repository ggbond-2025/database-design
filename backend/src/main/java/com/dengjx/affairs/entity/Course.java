package com.dengjx.affairs.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_courses13")
public class Course {

    @TableId(value = "djx_courseid13", type = IdType.AUTO)
    private Long courseId;

    @TableField("djx_coursecode13")
    private String courseCode;

    @TableField("djx_coursename13")
    private String courseName;

    @TableField("djx_hours13")
    private Integer hours;

    @TableField("djx_assessmenttype13")
    private String assessmentType;

    @TableField("djx_credit13")
    private BigDecimal credit;
}
