package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.service.UserAccountService;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.dto.UserAccountRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final Set<String> ROLES = Set.of("ADMIN", "TEACHER", "STUDENT");

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserAccountServiceImpl(
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserAccountServiceImpl(UserAccountMapper userAccountMapper, PasswordEncoder passwordEncoder) {
        this(userAccountMapper, passwordEncoder, null);
    }

    public PageResult<UserAccount> list(String keyword, long page, long size) {
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UserAccount::getUsername, keyword).or().like(UserAccount::getRole, keyword);
        }
        wrapper.orderByAsc(UserAccount::getUserId);
        Page<UserAccount> result = userAccountMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public UserAccount getById(Long id) {
        UserAccount user = userAccountMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户账号不存在");
        }
        return user;
    }

    public UserAccount create(UserAccountRequest request) {
        validate(request, true);
        UserAccount user = new UserAccount();
        apply(request, user, true);
        userAccountMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    public UserAccount update(Long id, UserAccountRequest request) {
        validate(request, false);
        UserAccount user = getById(id);
        apply(request, user, false);
        userAccountMapper.updateById(user);
        incrementTokenVersion(id);
        user.setPassword(null);
        return user;
    }

    public void delete(Long id) {
        if (userAccountMapper.deleteById(id) == 0) {
            throw new BusinessException("用户账号不存在");
        }
    }

    private void validate(UserAccountRequest request, boolean creating) {
        if (!ROLES.contains(request.role())) {
            throw new BusinessException("角色必须为ADMIN、TEACHER或STUDENT");
        }
        if (creating && !StringUtils.hasText(request.password())) {
            throw new BusinessException("创建账号时密码不能为空");
        }
        if ("STUDENT".equals(request.role()) && (request.studentId() == null || request.teacherId() != null)) {
            throw new BusinessException("学生账号必须绑定学生且不能绑定教师");
        }
        if ("TEACHER".equals(request.role()) && (request.teacherId() == null || request.studentId() != null)) {
            throw new BusinessException("教师账号必须绑定教师且不能绑定学生");
        }
        if ("ADMIN".equals(request.role()) && (request.studentId() != null || request.teacherId() != null)) {
            throw new BusinessException("管理员账号不能绑定学生或教师");
        }
    }

    private void apply(UserAccountRequest request, UserAccount user, boolean creating) {
        user.setUsername(request.username());
        if (creating || StringUtils.hasText(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setRole(request.role());
        user.setStudentId(request.studentId());
        user.setTeacherId(request.teacherId());
        user.setEnabled(request.enabled() == null || request.enabled());
    }

    private void incrementTokenVersion(Long userId) {
        if (jdbcTemplate == null) {
            return;
        }
        try {
            jdbcTemplate.update(
                    "UPDATE Dengjx_Users13 SET djx_TokenVersion13 = djx_TokenVersion13 + 1 WHERE djx_UserId13 = ?",
                    userId);
        } catch (DataAccessException exception) {
            // Older local databases may not have this column until the schema script is reapplied.
        }
    }
}
