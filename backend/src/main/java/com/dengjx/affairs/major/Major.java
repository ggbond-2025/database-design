package com.dengjx.affairs.major;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Majors13")
public class Major {

    @TableId(value = "djx_MajorId13", type = IdType.AUTO)
    private Long majorId;

    @TableField("djx_MajorName13")
    private String majorName;
}
