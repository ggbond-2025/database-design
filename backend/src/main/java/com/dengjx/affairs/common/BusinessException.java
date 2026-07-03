package com.dengjx.affairs.common;

import java.util.List;

public class BusinessException extends RuntimeException {

    private final String code;
    private final List<String> details;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.details = List.of();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = hasText(code) ? code : "BUSINESS_ERROR";
        this.details = List.of();
    }

    public BusinessException(String code, String message, List<String> details) {
        super(message);
        this.code = hasText(code) ? code : "BUSINESS_ERROR";
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public BusinessException(String code, String message, Throwable cause, List<String> details) {
        super(message, cause);
        this.code = hasText(code) ? code : "BUSINESS_ERROR";
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public String getCode() {
        return code;
    }

    public List<String> getDetails() {
        return details;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
