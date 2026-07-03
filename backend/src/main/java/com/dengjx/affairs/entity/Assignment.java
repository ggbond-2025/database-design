package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_teachingassignments13")
public class Assignment {

    @TableId(value = "djx_assignmentid13", type = IdType.AUTO)
    private Long assignmentId;

    @TableField("djx_courseid13")
    private Long courseId;

    @TableField("djx_classid13")
    private Long classId;

    @TableField("djx_teacherid13")
    private Long teacherId;

    @TableField("djx_academicyear13")
    private String academicYear;

    @TableField("djx_semester13")
    private Integer semester;

    @TableField("djx_coursetype13")
    private String courseType;

    @TableField("djx_capacity13")
    private Integer capacity;

    @TableField("djx_enrollmentopen13")
    private Boolean enrollmentOpen;
}
