package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_users13")
public class UserAccount {

    @TableId(value = "djx_userid13", type = IdType.AUTO)
    private Long userId;

    @TableField("djx_username13")
    private String username;

    @TableField(value = "djx_password13", select = false)
    private String password;

    @TableField("djx_role13")
    private String role;

    @TableField("djx_studentid13")
    private Long studentId;

    @TableField("djx_teacherid13")
    private Long teacherId;

    @TableField("djx_enabled13")
    private Boolean enabled;

    @TableField(value = "djx_tokenversion13", select = false)
    private Integer tokenVersion;
}
