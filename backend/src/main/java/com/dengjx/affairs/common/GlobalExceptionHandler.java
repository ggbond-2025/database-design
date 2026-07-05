package com.dengjx.affairs.common;

import java.util.Locale;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String INTERNAL_ERROR_MESSAGE = "系统异常，请联系管理员并提供错误追踪编号";

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorDetail> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request) {
        return fail(exception.getCode(), exception.getMessage(), exception, request, exception.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorDetail> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("参数校验失败");
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return fail("VALIDATION_ERROR", message, exception, request, details);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<ErrorDetail> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {
        return fail("ACCESS_DENIED", "没有权限访问该资源", exception, request, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorDetail> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        return fail("DATA_INTEGRITY_ERROR", resolveDataIntegrityMessage(exception), exception, request, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorDetail> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        return fail(
                "ILLEGAL_ARGUMENT",
                hasText(exception.getMessage()) ? exception.getMessage() : "请求参数不合法",
                exception,
                request,
                List.of());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<ErrorDetail> handleException(Throwable exception, HttpServletRequest request) {
        return fail("INTERNAL_ERROR", INTERNAL_ERROR_MESSAGE, exception, request, List.of(), true);
    }

    private String resolveDataIntegrityMessage(DataIntegrityViolationException exception) {
        String message = collectExceptionText(exception).toLowerCase(Locale.ROOT);
        if (message.contains("dengjx_enrollments13") && message.contains("djx_status13")) {
            return "选课状态不合法，仅支持SELECTED、DROPPED、COMPLETED；如果录入成绩时报错，请同步最新数据库约束";
        }
        if (message.contains("dengjx_grades13") && message.contains("djx_enrollmentid13")
                && (message.contains("unique") || message.contains("duplicate"))) {
            return "该选课记录已存在成绩";
        }
        if (message.contains("dengjx_grades13") && message.contains("djx_score13")) {
            return "成绩必须在0到100之间";
        }
        if (message.contains("foreign key")) {
            return "关联数据不存在或仍被引用，无法完成操作";
        }
        if (message.contains("unique") || message.contains("duplicate")) {
            return "数据已存在，不能重复添加";
        }
        if (message.contains("check constraint")) {
            return "数据不符合取值范围或状态约束";
        }
        return "数据完整性校验失败，请检查唯一性、外键关联或取值范围";
    }

    private String collectExceptionText(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        Throwable current = throwable;
        while (current != null) {
            if (hasText(current.getMessage())) {
                builder.append(current.getMessage()).append('\n');
            }
            current = current.getCause();
        }
        return builder.toString();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private ApiResponse<ErrorDetail> fail(
            String code,
            String message,
            Throwable exception,
            HttpServletRequest request,
            List<String> details) {
        return fail(code, message, exception, request, details, false);
    }

    private ApiResponse<ErrorDetail> fail(
            String code,
            String message,
            Throwable exception,
            HttpServletRequest request,
            List<String> details,
            boolean sanitizeErrorDetailMessage) {
        ErrorDetail errorDetail = ErrorDetail.from(code, exception, request, details);
        if (sanitizeErrorDetailMessage) {
            errorDetail = errorDetail.withPublicMessage(message);
        }
        log.error(
                "Request failed: traceId={} code={} request={} {} message={}",
                errorDetail.traceId(),
                errorDetail.code(),
                errorDetail.method(),
                errorDetail.path(),
                message,
                exception);
        return ApiResponse.fail(message, errorDetail);
    }
}
