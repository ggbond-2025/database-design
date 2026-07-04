package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_teachingbuildings13")
public class TeachingBuilding {

    @TableId(value = "djx_buildingid13", type = IdType.AUTO)
    private Long buildingId;

    @TableField("djx_buildingname13")
    private String buildingName;
}
