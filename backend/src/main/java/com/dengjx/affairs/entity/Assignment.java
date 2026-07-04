package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalTime;
import lombok.Data;

@Data
@TableName("dengjx_teachingassignments13")
public class Assignment {

    @TableId(value = "djx_assignmentid13", type = IdType.AUTO)
    private Long assignmentId;

    @TableField("djx_majorcourseid13")
    private Long majorCourseId;

    @TableField("djx_classid13")
    private Long classId;

    @TableField("djx_teacherid13")
    private Long teacherId;

    @TableField("djx_classroomid13")
    private Long classroomId;

    @TableField("djx_academicyear13")
    private String academicYear;

    @TableField("djx_semester13")
    private Integer semester;

    @TableField("djx_capacity13")
    private Integer capacity;

    @TableField("djx_enrollmentopen13")
    private Boolean enrollmentOpen;

    @TableField("djx_weekdayone13")
    private Integer weekdayOne;

    @TableField("djx_starttimeone13")
    private LocalTime startTimeOne;

    @TableField("djx_endtimeone13")
    private LocalTime endTimeOne;

    @TableField("djx_weekdaytwo13")
    private Integer weekdayTwo;

    @TableField("djx_starttimetwo13")
    private LocalTime startTimeTwo;

    @TableField("djx_endtimetwo13")
    private LocalTime endTimeTwo;
}
