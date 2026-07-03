package com.dengjx.affairs.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.config.SecurityConfig;
import com.dengjx.affairs.controller.RegionController;
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

    @Test
    void protectedApiWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/regions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedApiWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminApiWithTeacherTokenReturnsForbidden() throws Exception {
        String token = jwtService.generateToken(1L, "teacher01", "TEACHER");

        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
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
        String token = jwtService.generateToken(1L, "teacher01", "TEACHER");

        mockMvc.perform(get("/api/admin/regions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        assertThat(output.getOut())
                .contains("Security request rejected")
                .contains("status=403")
                .contains("GET /api/admin/regions");
    }
}
