package com.dengjx.affairs.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Courses13")
public class Course {

    @TableId(value = "djx_CourseId13", type = IdType.AUTO)
    private Long courseId;

    @TableField("djx_CourseCode13")
    private String courseCode;

    @TableField("djx_CourseName13")
    private String courseName;

    @TableField("djx_Hours13")
    private Integer hours;

    @TableField("djx_AssessmentType13")
    private String assessmentType;

    @TableField("djx_Credit13")
    private BigDecimal credit;
}
