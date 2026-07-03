package com.dengjx.affairs.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Enrollments13")
public class Enrollment {

    @TableId(value = "djx_EnrollmentId13", type = IdType.AUTO)
    private Long enrollmentId;

    @TableField("djx_StudentId13")
    private Long studentId;

    @TableField("djx_AssignmentId13")
    private Long assignmentId;

    @TableField("djx_Status13")
    private String status;

    @TableField("djx_SelectedAt13")
    private LocalDateTime selectedAt;

    @TableField("djx_DroppedAt13")
    private LocalDateTime droppedAt;
}
