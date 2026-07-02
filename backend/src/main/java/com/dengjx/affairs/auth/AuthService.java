package com.dengjx.affairs.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.dengjx.affairs.auth.dto.LoginRequest;
import com.dengjx.affairs.auth.dto.LoginResponse;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.security.JwtService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String BAD_CREDENTIALS_MESSAGE = "用户名或密码错误";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        UserAccount account = findAccount(request.username())
                .orElseThrow(() -> new BusinessException(BAD_CREDENTIALS_MESSAGE));
        if (!passwordEncoder.matches(request.password(), account.password())) {
            throw new BusinessException(BAD_CREDENTIALS_MESSAGE);
        }

        String token = jwtService.generateToken(account.userId(), account.username(), account.role());
        return new LoginResponse(token, account.role(), displayName(account));
    }

    private Optional<UserAccount> findAccount(String username) {
        String sql = """
                SELECT u.djx_UserId13,
                       u.djx_Username13,
                       u.djx_Password13,
                       u.djx_Role13,
                       s.djx_Sname13,
                       t.djx_Tname13
                FROM Dengjx_Users13 u
                LEFT JOIN Dengjx_Students13 s ON s.djx_StudentId13 = u.djx_StudentId13
                LEFT JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = u.djx_TeacherId13
                WHERE u.djx_Username13 = ?
                  AND u.djx_Enabled13 = TRUE
                """;
        return jdbcTemplate.query(sql, ps -> ps.setString(1, username), rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(mapAccount(rs));
        });
    }

    private UserAccount mapAccount(ResultSet rs) throws SQLException {
        return new UserAccount(
                rs.getLong("djx_UserId13"),
                rs.getString("djx_Username13"),
                rs.getString("djx_Password13"),
                rs.getString("djx_Role13"),
                rs.getString("djx_Sname13"),
                rs.getString("djx_Tname13"));
    }

    private String displayName(UserAccount account) {
        if ("ADMIN".equals(account.role())) {
            return "管理员";
        }
        if (account.teacherName() != null && !account.teacherName().isBlank()) {
            return account.teacherName();
        }
        if (account.studentName() != null && !account.studentName().isBlank()) {
            return account.studentName();
        }
        return account.username();
    }

    private record UserAccount(
            Long userId,
            String username,
            String password,
            String role,
            String studentName,
            String teacherName) {
    }
}
