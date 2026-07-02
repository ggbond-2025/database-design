package com.dengjx.affairs.auth.dto;

public record LoginResponse(String token, String role, String displayName) {
}
