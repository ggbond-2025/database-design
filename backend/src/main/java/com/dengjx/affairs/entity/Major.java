package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_majors13")
public class Major {

    @TableId(value = "djx_majorid13", type = IdType.AUTO)
    private Long majorId;

    @TableField("djx_majorname13")
    private String majorName;
}
