package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_classes13")
public class SchoolClass {

    @TableId(value = "djx_classid13", type = IdType.AUTO)
    private Long classId;

    @TableField("djx_classname13")
    private String className;

    @TableField("djx_majorid13")
    private Long majorId;

    @TableField("djx_gradeyear13")
    private Integer gradeYear;
}
