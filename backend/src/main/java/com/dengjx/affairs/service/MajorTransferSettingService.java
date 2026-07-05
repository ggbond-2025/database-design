package com.dengjx.affairs.service;

import com.dengjx.affairs.dto.MajorTransferSettingRequest;
import java.util.Map;

public interface MajorTransferSettingService {

    boolean isEnabled();

    Map<String, Object> current();

    Map<String, Object> update(MajorTransferSettingRequest request);
}
