package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_regions13")
public class Region {

    @TableId(value = "djx_regionid13", type = IdType.AUTO)
    private Long regionId;

    @TableField("djx_regionname13")
    private String regionName;
}
