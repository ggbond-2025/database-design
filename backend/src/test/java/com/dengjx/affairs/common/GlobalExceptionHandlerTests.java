package com.dengjx.affairs.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void enrollmentStatusConstraintViolationReturnsSpecificMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/enrollments");
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "update Dengjx_Enrollments13 failed",
                new SQLException("violates check constraint \"Dengjx_Enrollments13_djx_Status13_check\""));

        ApiResponse<ErrorDetail> response = handler.handleDataIntegrityViolationException(exception, request);

        assertThat(response.message()).contains("选课状态不合法");
        assertThat(response.message()).contains("SELECTED");
        assertThat(response.message()).contains("COMPLETED");
        assertThat(response.data().code()).isEqualTo("DATA_INTEGRITY_ERROR");
    }

    @Test
    void duplicateGradeConstraintViolationReturnsSpecificMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/grades");
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "insert Dengjx_Grades13 failed",
                new SQLException("duplicate key value violates unique constraint \"Dengjx_Grades13_djx_EnrollmentId13_key\""));

        ApiResponse<ErrorDetail> response = handler.handleDataIntegrityViolationException(exception, request);

        assertThat(response.message()).isEqualTo("该选课记录已存在成绩");
        assertThat(response.data().path()).isEqualTo("/api/admin/grades");
    }

    @Test
    void unexpectedExceptionReturnsDebuggableDetails() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/students");
        RuntimeException exception = new RuntimeException("student insert failed");

        ApiResponse<ErrorDetail> response = handler.handleException(exception, request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("student insert failed");
        assertThat(response.data().path()).isEqualTo("/api/admin/students");
        assertThat(response.data().method()).isEqualTo("POST");
        assertThat(response.data().exception()).isEqualTo(RuntimeException.class.getName());
        assertThat(response.data().traceId()).isNotBlank();
        assertThat(response.data().location()).contains("GlobalExceptionHandlerTests");
    }

    @Test
    void businessExceptionReturnsCustomErrorDetails() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/courses");
        BusinessException exception = new BusinessException("课程不存在");

        ApiResponse<ErrorDetail> response = handler.handleBusinessException(exception, request);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("课程不存在");
        assertThat(response.data().path()).isEqualTo("/api/admin/courses");
        assertThat(response.data().method()).isEqualTo("GET");
        assertThat(response.data().exception()).isEqualTo(BusinessException.class.getName());
        assertThat(response.data().code()).isEqualTo("BUSINESS_ERROR");
    }

    @Test
    void customBusinessExceptionCodeAndDetailsAreReturned() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/grades");
        BusinessException exception = new BusinessException(
                "GRADE_DUPLICATED",
                "该选课记录已存在成绩",
                List.of("enrollmentId=12"));

        ApiResponse<ErrorDetail> response = handler.handleBusinessException(exception, request);

        assertThat(response.message()).isEqualTo("该选课记录已存在成绩");
        assertThat(response.data().code()).isEqualTo("GRADE_DUPLICATED");
        assertThat(response.data().details()).containsExactly("enrollmentId=12");
    }
}
