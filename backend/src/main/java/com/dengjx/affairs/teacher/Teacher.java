package com.dengjx.affairs.teacher;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Teachers13")
public class Teacher {

    @TableId(value = "djx_TeacherId13", type = IdType.AUTO)
    private Long teacherId;

    @TableField("djx_Tno13")
    private String tno;

    @TableField("djx_Tname13")
    private String tname;

    @TableField("djx_Gender13")
    private String gender;

    @TableField("djx_Age13")
    private Integer age;

    @TableField("djx_Title13")
    private String title;

    @TableField("djx_Phone13")
    private String phone;
}
