package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.service.AuthService;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.dengjx.affairs.dto.ChangePasswordRequest;
import com.dengjx.affairs.dto.CurrentUserProfileResponse;
import com.dengjx.affairs.dto.LoginRequest;
import com.dengjx.affairs.dto.LoginResponse;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.security.JwtService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String BAD_CREDENTIALS_MESSAGE = "用户名或密码错误";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AcademicTermService academicTermService;

    public AuthServiceImpl(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AcademicTermService academicTermService) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.academicTermService = academicTermService;
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

    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        PasswordAccount account = findPasswordAccount(userId);
        if (!passwordEncoder.matches(request.oldPassword(), account.password())) {
            throw new BusinessException("原密码错误");
        }
        jdbcTemplate.update(
                "UPDATE Dengjx_Users13 SET djx_Password13 = ? WHERE djx_UserId13 = ? AND djx_Enabled13 = TRUE",
                passwordEncoder.encode(request.newPassword()),
                userId);
    }

    public CurrentUserProfileResponse currentProfile(Long userId) {
        String sql = """
                SELECT u.djx_UserId13,
                       u.djx_Username13,
                       u.djx_Role13,
                       u.djx_Enabled13,
                       s.djx_StudentId13,
                       s.djx_Sno13,
                       s.djx_Sname13,
                       s.djx_Gender13 AS student_gender,
                       s.djx_Age13 AS student_age,
                       s.djx_AdmissionDate13,
                       cs.djx_TotalCredits13,
                       cl.djx_ClassName13,
                       m.djx_MajorName13,
                       r.djx_RegionName13,
                       t.djx_TeacherId13,
                       t.djx_Tno13,
                       t.djx_Tname13,
                       t.djx_Gender13 AS teacher_gender,
                       t.djx_Age13 AS teacher_age,
                       t.djx_Title13,
                       t.djx_Phone13
                FROM Dengjx_Users13 u
                LEFT JOIN Dengjx_Students13 s ON s.djx_StudentId13 = u.djx_StudentId13
                LEFT JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
                LEFT JOIN Dengjx_Majors13 m ON m.djx_MajorId13 = cl.djx_MajorId13
                LEFT JOIN Dengjx_Regions13 r ON r.djx_RegionId13 = s.djx_RegionId13
                LEFT JOIN V_Dengjx_StudentCreditSummary13 cs ON cs.djx_StudentId13 = s.djx_StudentId13
                LEFT JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = u.djx_TeacherId13
                WHERE u.djx_UserId13 = ?
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapProfile, userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new BusinessException("当前账号不存在");
        }
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

    private PasswordAccount findPasswordAccount(Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    """
                            SELECT djx_UserId13, djx_Password13
                            FROM Dengjx_Users13
                            WHERE djx_UserId13 = ?
                              AND djx_Enabled13 = TRUE
                            """,
                    (rs, rowNum) -> new PasswordAccount(
                            rs.getLong("djx_UserId13"),
                            rs.getString("djx_Password13")),
                    userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new BusinessException("当前账号不存在或已禁用");
        }
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

    private CurrentUserProfileResponse mapProfile(ResultSet rs, int rowNum) throws SQLException {
        String role = rs.getString("djx_Role13");
        String studentName = rs.getString("djx_Sname13");
        String teacherName = rs.getString("djx_Tname13");
        AcademicTermService.AcademicTerm term = currentTerm(rs);
        String displayName = displayName(new UserAccount(
                rs.getLong("djx_UserId13"),
                rs.getString("djx_Username13"),
                null,
                role,
                studentName,
                teacherName));
        return new CurrentUserProfileResponse(
                rs.getLong("djx_UserId13"),
                rs.getString("djx_Username13"),
                role,
                displayName,
                rs.getBoolean("djx_Enabled13"),
                nullableLong(rs, "djx_StudentId13"),
                rs.getString("djx_Sno13"),
                studentName,
                rs.getString("student_gender"),
                nullableInteger(rs, "student_age"),
                rs.getString("djx_ClassName13"),
                rs.getString("djx_MajorName13"),
                rs.getString("djx_RegionName13"),
                term == null ? null : term.grade(),
                term == null ? null : term.semester(),
                term == null ? null : term.label(),
                rs.getBigDecimal("djx_TotalCredits13"),
                nullableLong(rs, "djx_TeacherId13"),
                rs.getString("djx_Tno13"),
                teacherName,
                rs.getString("teacher_gender"),
                nullableInteger(rs, "teacher_age"),
                rs.getString("djx_Title13"),
                rs.getString("djx_Phone13"));
    }

    private AcademicTermService.AcademicTerm currentTerm(ResultSet rs) throws SQLException {
        Date admissionDate = rs.getDate("djx_AdmissionDate13");
        if (admissionDate == null) {
            return null;
        }
        LocalDate localDate = admissionDate.toLocalDate();
        return academicTermService.current(localDate);
    }

    private Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private Integer nullableInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private record UserAccount(
            Long userId,
            String username,
            String password,
            String role,
            String studentName,
            String teacherName) {
    }

    private record PasswordAccount(Long userId, String password) {
    }
}
