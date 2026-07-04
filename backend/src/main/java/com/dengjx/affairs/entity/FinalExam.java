package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("dengjx_finalexams13")
public class FinalExam {

    @TableId(value = "djx_examid13", type = IdType.AUTO)
    private Long examId;

    @TableField("djx_courseid13")
    private Long courseId;

    @TableField("djx_academicyear13")
    private String academicYear;

    @TableField("djx_semester13")
    private Integer semester;

    @TableField("djx_examtime13")
    private LocalDateTime examTime;
}
