package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.dto.UserAccountRequest;
import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.service.UserAccountService;
import com.dengjx.affairs.service.impl.UserAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserAccountValidationTests {

    private final UserAccountService service = new UserAccountServiceImpl(
            mock(UserAccountMapper.class),
            mock(PasswordEncoder.class));

    @Test
    void studentAccountMustBindOnlyStudent() {
        UserAccountRequest request = new UserAccountRequest("s20230001", "123456", "STUDENT", null, 1L, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学生账号必须绑定学生");
    }

    @Test
    void teacherAccountMustBindOnlyTeacher() {
        UserAccountRequest request = new UserAccountRequest("t2023001", "123456", "TEACHER", 1L, null, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("教师账号必须绑定教师");
    }

    @Test
    void adminAccountCannotBindPersonRecord() {
        UserAccountRequest request = new UserAccountRequest("admin2", "123456", "ADMIN", 1L, null, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("管理员账号不能绑定");
    }
}
