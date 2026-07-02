package com.dengjx.affairs.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTests {

    @Test
    void generatedTokenContainsUserIdUsernameAndRole() {
        JwtService jwtService = new JwtService("test-secret-for-academic-affairs", 120);

        String token = jwtService.generateToken(7L, "teacher01", "TEACHER");

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.getUserId(token)).isEqualTo(7L);
        assertThat(jwtService.parseToken(token).getSubject()).isEqualTo("teacher01");
        assertThat(jwtService.getRole(token)).isEqualTo("TEACHER");
    }

    @Test
    void invalidTokenReturnsFalse() {
        JwtService jwtService = new JwtService("test-secret-for-academic-affairs", 120);

        assertThat(jwtService.isValid("invalid-token")).isFalse();
    }
}
