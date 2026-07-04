package com.dengjx.affairs.dto;

import java.util.Map;

public record LookupOption(Long value, String label, Map<String, Object> meta) {

    public LookupOption(Long value, String label) {
        this(value, label, Map.of());
    }
}
