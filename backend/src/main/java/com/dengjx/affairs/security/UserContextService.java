package com.dengjx.affairs.security;

import com.dengjx.affairs.common.BusinessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    private final JdbcTemplate jdbcTemplate;

    public UserContextService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long getStudentId(Long userId) {
        return queryBoundId(userId, "djx_StudentId13", "当前账号未绑定学生信息");
    }

    public Long getTeacherId(Long userId) {
        return queryBoundId(userId, "djx_TeacherId13", "当前账号未绑定教师信息");
    }

    private Long queryBoundId(Long userId, String column, String missingMessage) {
        try {
            Long id = jdbcTemplate.queryForObject(
                    "SELECT " + column + " FROM Dengjx_Users13 WHERE djx_UserId13 = ? AND djx_Enabled13 = TRUE",
                    Long.class,
                    userId);
            if (id == null) {
                throw new BusinessException(missingMessage);
            }
            return id;
        } catch (EmptyResultDataAccessException exception) {
            throw new BusinessException("当前账号不存在或已禁用");
        }
    }
}
