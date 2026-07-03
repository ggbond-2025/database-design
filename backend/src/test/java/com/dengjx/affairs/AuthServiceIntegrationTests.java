package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.dto.LoginRequest;
import com.dengjx.affairs.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceIntegrationTests {

    @Autowired
    private AuthService authService;

    @Test
    void invalidPasswordReturnsBusinessException() {
        LoginRequest request = new LoginRequest("admin", "not-the-password");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名或密码错误");
    }
}
