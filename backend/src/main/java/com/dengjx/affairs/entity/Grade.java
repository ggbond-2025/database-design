package com.dengjx.affairs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_grades13")
public class Grade {

    @TableId(value = "djx_gradeid13", type = IdType.AUTO)
    private Long gradeId;

    @TableField("djx_enrollmentid13")
    private Long enrollmentId;

    @TableField("djx_score13")
    private BigDecimal score;

    @TableField("djx_gradedat13")
    private LocalDateTime gradedAt;
}
