package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_teachers13")
public class Teacher {

    @TableId(value = "djx_teacherid13", type = IdType.AUTO)
    private Long teacherId;

    @TableField("djx_tno13")
    private String tno;

    @TableField("djx_tname13")
    private String tname;

    @TableField("djx_gender13")
    private String gender;

    @TableField("djx_age13")
    private Integer age;

    @TableField("djx_title13")
    private String title;

    @TableField("djx_phone13")
    private String phone;
}
