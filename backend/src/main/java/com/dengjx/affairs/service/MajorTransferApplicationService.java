package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorTransferReviewRequest;
import com.dengjx.affairs.dto.MajorTransferSubmitRequest;
import com.dengjx.affairs.entity.MajorTransferApplication;
import java.util.List;
import java.util.Map;

public interface MajorTransferApplicationService {

    PageResult<Map<String, Object>> adminList(String keyword, long page, long size);

    List<Map<String, Object>> studentMine(Long userId);

    MajorTransferApplication submit(Long userId, MajorTransferSubmitRequest request);

    MajorTransferApplication review(Long adminUserId, Long applicationId, MajorTransferReviewRequest request);
}
