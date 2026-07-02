package com.dengjx.affairs.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTests {

    @Test
    void okWrapsDataWithSuccessFlag() {
        ApiResponse<String> response = ApiResponse.ok("value");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("success");
        assertThat(response.data()).isEqualTo("value");
    }

    @Test
    void failWrapsMessageWithoutData() {
        ApiResponse<Object> response = ApiResponse.fail("invalid");

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("invalid");
        assertThat(response.data()).isNull();
    }
}
