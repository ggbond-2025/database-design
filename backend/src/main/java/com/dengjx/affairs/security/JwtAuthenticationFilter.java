package com.dengjx.affairs.security;

import java.io.IOException;
import java.util.List;

import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.mapper.UserAccountMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserAccountMapper userAccountMapper;

    public JwtAuthenticationFilter(JwtService jwtService, UserAccountMapper userAccountMapper) {
        this.jwtService = jwtService;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            authenticate(authorization.substring(BEARER_PREFIX.length()));
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        if (SecurityContextHolder.getContext().getAuthentication() != null || !jwtService.isValid(token)) {
            return;
        }

        Claims claims = jwtService.parseToken(token);
        String username = claims.getSubject();
        Long userId = jwtService.getUserId(token);
        UserAccount account = userAccountMapper.selectById(userId);
        if (account == null || !Boolean.TRUE.equals(account.getEnabled())) {
            return;
        }
        int currentTokenVersion = currentTokenVersion(userId);
        if (jwtService.getTokenVersion(token) != currentTokenVersion) {
            return;
        }
        String role = account.getRole();
        AuthenticatedUser user = new AuthenticatedUser(userId, username, role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                token,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private int currentTokenVersion(Long userId) {
        try {
            Integer tokenVersion = userAccountMapper.selectTokenVersionById(userId);
            return tokenVersion == null ? 0 : tokenVersion;
        } catch (RuntimeException exception) {
            log.warn(
                    "Token version lookup failed for userId={}, falling back to version 0: {}",
                    userId,
                    exception.getMessage());
            return 0;
        }
    }
}
