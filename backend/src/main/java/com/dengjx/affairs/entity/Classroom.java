package com.dengjx.affairs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dengjx_classrooms13")
public class Classroom {

    @TableId(value = "djx_classroomid13", type = IdType.AUTO)
    private Long classroomId;

    @TableField("djx_buildingid13")
    private Long buildingId;

    @TableField("djx_classroomname13")
    private String classroomName;

    @TableField("djx_capacity13")
    private Integer capacity;
}
