package com.dengjx.affairs.common;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

public record ErrorDetail(
        String traceId,
        String code,
        String path,
        String method,
        String exception,
        String message,
        String rootCause,
        String location,
        List<String> details,
        Instant timestamp) {

    private static final String APPLICATION_PACKAGE = "com.dengjx.affairs";

    public static ErrorDetail from(String code, Throwable throwable, HttpServletRequest request, List<String> details) {
        Throwable rootCause = rootCauseOf(throwable);
        return new ErrorDetail(
                UUID.randomUUID().toString(),
                hasText(code) ? code : "INTERNAL_ERROR",
                request == null ? "" : request.getRequestURI(),
                request == null ? "" : request.getMethod(),
                throwable.getClass().getName(),
                messageOf(throwable),
                messageOf(rootCause),
                locationOf(throwable),
                details == null ? List.of() : List.copyOf(details),
                Instant.now());
    }

    private static Throwable rootCauseOf(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private static String messageOf(Throwable throwable) {
        return hasText(throwable.getMessage()) ? throwable.getMessage() : throwable.getClass().getSimpleName();
    }

    private static String locationOf(Throwable throwable) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith(APPLICATION_PACKAGE)) {
                return format(element);
            }
        }
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        return stackTrace.length == 0 ? "" : format(stackTrace[0]);
    }

    private static String format(StackTraceElement element) {
        return element.getClassName()
                + "."
                + element.getMethodName()
                + "("
                + element.getFileName()
                + ":"
                + element.getLineNumber()
                + ")";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
