package com.dengjx.affairs.service;

import com.dengjx.affairs.dto.EnrollmentSettingRequest;
import java.util.Map;

public interface EnrollmentSettingService {

    boolean isEnabled();

    Map<String, Object> current();

    Map<String, Object> update(EnrollmentSettingRequest request);
}
