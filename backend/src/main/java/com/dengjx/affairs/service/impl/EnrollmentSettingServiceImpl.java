package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.dto.EnrollmentSettingRequest;
import com.dengjx.affairs.service.EnrollmentSettingService;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentSettingServiceImpl implements EnrollmentSettingService {

    private final JdbcTemplate jdbcTemplate;

    public EnrollmentSettingServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isEnabled() {
        Boolean enabled = jdbcTemplate.queryForObject(
                "SELECT djx_enabled13 FROM dengjx_enrollmentsettings13 WHERE djx_settingid13 = 1",
                Boolean.class);
        return Boolean.TRUE.equals(enabled);
    }

    @Override
    public Map<String, Object> current() {
        return jdbcTemplate.queryForMap("""
                SELECT djx_settingid13 AS settingId,
                       djx_enabled13 AS enabled,
                       djx_updatedat13 AS updatedAt
                FROM dengjx_enrollmentsettings13
                WHERE djx_settingid13 = 1
                """);
    }

    @Override
    @Transactional
    public Map<String, Object> update(EnrollmentSettingRequest request) {
        jdbcTemplate.update("""
                UPDATE dengjx_enrollmentsettings13
                SET djx_enabled13 = ?,
                    djx_updatedat13 = CURRENT_TIMESTAMP
                WHERE djx_settingid13 = 1
                """, request.enabled());
        return current();
    }
}
