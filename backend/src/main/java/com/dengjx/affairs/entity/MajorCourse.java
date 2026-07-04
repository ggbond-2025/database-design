package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_majorcourses13")
public class MajorCourse {

    @TableId(value = "djx_majorcourseid13", type = IdType.AUTO)
    private Long majorCourseId;

    @TableField("djx_majorid13")
    private Long majorId;

    @TableField("djx_courseid13")
    private Long courseId;

    @TableField("djx_coursetype13")
    private String courseType;

    @TableField("djx_targetgrade13")
    private Integer targetGrade;

    @TableField("djx_targetsemester13")
    private Integer targetSemester;
}
