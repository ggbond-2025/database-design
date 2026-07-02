package com.dengjx.affairs.schoolclass;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Classes13")
public class SchoolClass {

    @TableId(value = "djx_ClassId13", type = IdType.AUTO)
    private Long classId;

    @TableField("djx_ClassName13")
    private String className;

    @TableField("djx_MajorId13")
    private Long majorId;

    @TableField("djx_GradeYear13")
    private Integer gradeYear;
}
