package com.dengjx.affairs.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

import com.dengjx.affairs.config.SecurityConfig;
import com.dengjx.affairs.controller.RegionController;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.service.RegionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RegionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {
        "app.jwt.secret=test-secret-for-academic-affairs",
        "app.jwt.expiration-minutes=120"
})
class SecurityResponseTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private RegionService regionService;

    @MockBean
    private UserAccountMapper userAccountMapper;

    @Test
    void protectedApiWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/regions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未登录或登录已过期"))
                .andExpect(jsonPath("$.data.code").value("AUTHENTICATION_ERROR"))
                .andExpect(jsonPath("$.data.path").value("/api/admin/regions"))
                .andExpect(jsonPath("$.data.method").value("GET"))
                .andExpect(jsonPath("$.data.traceId").isNotEmpty());
    }

    @Test
    void protectedApiWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminApiWithTeacherTokenReturnsForbidden() throws Exception {
        UserAccount account = userAccount(1L, "teacher01", "TEACHER", true, 0);
        when(userAccountMapper.selectById(1L)).thenReturn(account);
        String token = jwtService.generateToken(1L, "teacher01", "TEACHER", 0);

        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("没有权限访问该资源"))
                .andExpect(jsonPath("$.data.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.data.path").value("/api/admin/regions"))
                .andExpect(jsonPath("$.data.method").value("GET"))
                .andExpect(jsonPath("$.data.traceId").isNotEmpty());
    }

    @Test
    void protectedApiWithoutTokenWritesSecurityLog(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/api/admin/regions"))
                .andExpect(status().isUnauthorized());

        assertThat(output.getOut())
                .contains("Security request rejected")
                .contains("status=401")
                .contains("GET /api/admin/regions");
    }

    @Test
    void adminApiWithTeacherTokenWritesSecurityLog(CapturedOutput output) throws Exception {
        UserAccount account = userAccount(1L, "teacher01", "TEACHER", true, 0);
        when(userAccountMapper.selectById(1L)).thenReturn(account);
        String token = jwtService.generateToken(1L, "teacher01", "TEACHER", 0);

        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        assertThat(output.getOut())
                .contains("Security request rejected")
                .contains("status=403")
                .contains("GET /api/admin/regions");
    }

    @Test
    void protectedApiWithStaleTokenVersionReturnsUnauthorized() throws Exception {
        UserAccount account = userAccount(1L, "teacher01", "TEACHER", true, 2);
        when(userAccountMapper.selectById(1L)).thenReturn(account);
        when(userAccountMapper.selectTokenVersionById(eq(1L))).thenReturn(2);
        String token = jwtService.generateToken(1L, "teacher01", "TEACHER", 1);

        mockMvc.perform(get("/api/teacher/statistics/course-averages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    private UserAccount userAccount(Long id, String username, String role, boolean enabled, int tokenVersion) {
        UserAccount account = new UserAccount();
        account.setUserId(id);
        account.setUsername(username);
        account.setRole(role);
        account.setEnabled(enabled);
        account.setTokenVersion(tokenVersion);
        return account;
    }
}
