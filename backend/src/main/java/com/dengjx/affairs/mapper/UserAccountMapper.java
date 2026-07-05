package com.dengjx.affairs.mapper;

import com.dengjx.affairs.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    @Select("SELECT djx_TokenVersion13 FROM Dengjx_Users13 WHERE djx_UserId13 = #{userId}")
    Integer selectTokenVersionById(@Param("userId") Long userId);
}
