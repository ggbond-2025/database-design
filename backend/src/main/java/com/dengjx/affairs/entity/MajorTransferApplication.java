package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("dengjx_majortransferapplications13")
public class MajorTransferApplication {

    @TableId(value = "djx_applicationid13", type = IdType.AUTO)
    private Long applicationId;

    @TableField("djx_studentid13")
    private Long studentId;

    @TableField("djx_frommajorid13")
    private Long fromMajorId;

    @TableField("djx_targetmajorid13")
    private Long targetMajorId;

    @TableField("djx_targetclassid13")
    private Long targetClassId;

    @TableField("djx_reason13")
    private String reason;

    @TableField("djx_status13")
    private String status;

    @TableField("djx_reviewcomment13")
    private String reviewComment;

    @TableField("djx_appliedat13")
    private LocalDateTime appliedAt;

    @TableField("djx_reviewedat13")
    private LocalDateTime reviewedAt;
}
