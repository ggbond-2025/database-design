package com.dengjx.affairs;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dengjx.affairs.config.SecurityConfig;
import com.dengjx.affairs.controller.AuthController;
import com.dengjx.affairs.dto.ChangePasswordRequest;
import com.dengjx.affairs.dto.CurrentUserProfileResponse;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.security.JwtAuthenticationFilter;
import com.dengjx.affairs.security.JwtService;
import com.dengjx.affairs.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "app.jwt.secret=test-secret-for-academic-affairs",
        "app.jwt.expiration-minutes=120"
})
class AuthControllerAccountTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserAccountMapper userAccountMapper;

    @Test
    void authenticatedUserCanChangeOwnPassword() throws Exception {
        when(userAccountMapper.selectById(7L)).thenReturn(userAccount(7L, "student01", "STUDENT"));
        String token = jwtService.generateToken(7L, "student01", "STUDENT", 0);

        mockMvc.perform(post("/api/auth/password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old-pass",
                                  "newPassword": "new-pass-123",
                                  "confirmPassword": "new-pass-123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(authService).changePassword(eq(7L), eq(new ChangePasswordRequest(
                "old-pass",
                "new-pass-123",
                "new-pass-123")));
    }

    @Test
    void authenticatedUserCanReadCurrentProfile() throws Exception {
        when(userAccountMapper.selectById(9L)).thenReturn(userAccount(9L, "teacher01", "TEACHER"));
        String token = jwtService.generateToken(9L, "teacher01", "TEACHER", 0);
        when(authService.currentProfile(9L)).thenReturn(new CurrentUserProfileResponse(
                9L,
                "teacher01",
                "TEACHER",
                "教师一",
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                3L,
                "T001",
                "教师一",
                "FEMALE",
                38,
                "副教授",
                "13800000000"));

        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("teacher01"))
                .andExpect(jsonPath("$.data.role").value("TEACHER"))
                .andExpect(jsonPath("$.data.teacherName").value("教师一"))
                .andExpect(jsonPath("$.data.enabled").value(true));
    }

    private UserAccount userAccount(Long id, String username, String role) {
        UserAccount account = new UserAccount();
        account.setUserId(id);
        account.setUsername(username);
        account.setRole(role);
        account.setEnabled(true);
        account.setTokenVersion(0);
        return account;
    }
}
