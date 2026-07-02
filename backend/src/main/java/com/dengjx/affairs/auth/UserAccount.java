package com.dengjx.affairs.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Users13")
public class UserAccount {

    @TableId(value = "djx_UserId13", type = IdType.AUTO)
    private Long userId;

    @TableField("djx_Username13")
    private String username;

    @TableField(value = "djx_Password13", select = false)
    private String password;

    @TableField("djx_Role13")
    private String role;

    @TableField("djx_StudentId13")
    private Long studentId;

    @TableField("djx_TeacherId13")
    private Long teacherId;

    @TableField("djx_Enabled13")
    private Boolean enabled;
}
