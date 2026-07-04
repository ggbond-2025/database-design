package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("dengjx_teachingevaluations13")
public class TeachingEvaluation {

    @TableId(value = "djx_evaluationid13", type = IdType.AUTO)
    private Long evaluationId;

    @TableField("djx_enrollmentid13")
    private Long enrollmentId;

    @TableField("djx_rating13")
    private Integer rating;

    @TableField("djx_comment13")
    private String comment;

    @TableField("djx_evaluatedat13")
    private LocalDateTime evaluatedAt;
}
