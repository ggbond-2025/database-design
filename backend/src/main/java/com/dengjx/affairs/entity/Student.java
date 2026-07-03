package com.dengjx.affairs.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_students13")
public class Student {

    @TableId(value = "djx_studentid13", type = IdType.AUTO)
    private Long studentId;

    @TableField("djx_sno13")
    private String sno;

    @TableField("djx_sname13")
    private String sname;

    @TableField("djx_gender13")
    private String gender;

    @TableField("djx_age13")
    private Integer age;

    @TableField("djx_classid13")
    private Long classId;

    @TableField("djx_regionid13")
    private Long regionId;

    @TableField("djx_totalcredits13")
    private BigDecimal totalCredits;
}
