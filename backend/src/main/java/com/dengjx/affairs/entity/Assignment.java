package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_TeachingAssignments13")
public class Assignment {

    @TableId(value = "djx_AssignmentId13", type = IdType.AUTO)
    private Long assignmentId;

    @TableField("djx_CourseId13")
    private Long courseId;

    @TableField("djx_ClassId13")
    private Long classId;

    @TableField("djx_TeacherId13")
    private Long teacherId;

    @TableField("djx_AcademicYear13")
    private String academicYear;

    @TableField("djx_Semester13")
    private Integer semester;

    @TableField("djx_CourseType13")
    private String courseType;

    @TableField("djx_Capacity13")
    private Integer capacity;

    @TableField("djx_EnrollmentOpen13")
    private Boolean enrollmentOpen;
}
