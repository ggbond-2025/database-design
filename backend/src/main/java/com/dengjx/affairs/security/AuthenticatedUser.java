package com.dengjx.affairs.security;

public record AuthenticatedUser(Long userId, String username, String role) {
}
