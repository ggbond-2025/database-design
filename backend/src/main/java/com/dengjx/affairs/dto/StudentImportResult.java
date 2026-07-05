package com.dengjx.affairs.dto;

import java.util.List;

public record StudentImportResult(
        int successCount,
        int failureCount,
        List<String> errors) {
}
