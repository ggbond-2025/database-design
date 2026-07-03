package com.dengjx.affairs.dto;

public record LoginResponse(String token, String role, String displayName) {
}
