package com.dengjx.affairs.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_enrollments13")
public class Enrollment {

    @TableId(value = "djx_enrollmentid13", type = IdType.AUTO)
    private Long enrollmentId;

    @TableField("djx_studentid13")
    private Long studentId;

    @TableField("djx_assignmentid13")
    private Long assignmentId;

    @TableField("djx_status13")
    private String status;

    @TableField("djx_selectedat13")
    private LocalDateTime selectedAt;

    @TableField("djx_droppedat13")
    private LocalDateTime droppedAt;
}
