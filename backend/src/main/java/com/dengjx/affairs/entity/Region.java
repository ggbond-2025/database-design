package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Dengjx_Regions13")
public class Region {

    @TableId(value = "djx_RegionId13", type = IdType.AUTO)
    private Long regionId;

    @TableField("djx_RegionName13")
    private String regionName;
}
