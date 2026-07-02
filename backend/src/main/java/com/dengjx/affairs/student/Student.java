package com.dengjx.affairs.student;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Students13")
public class Student {

    @TableId(value = "djx_StudentId13", type = IdType.AUTO)
    private Long studentId;

    @TableField("djx_Sno13")
    private String sno;

    @TableField("djx_Sname13")
    private String sname;

    @TableField("djx_Gender13")
    private String gender;

    @TableField("djx_Age13")
    private Integer age;

    @TableField("djx_ClassId13")
    private Long classId;

    @TableField("djx_RegionId13")
    private Long regionId;

    @TableField("djx_TotalCredits13")
    private BigDecimal totalCredits;
}
