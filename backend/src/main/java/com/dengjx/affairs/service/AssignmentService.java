package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.entity.Assignment;

public interface AssignmentService {

    PageResult<Assignment> list(String keyword, long page, long size);

    Assignment getById(Long id);

    Assignment create(AssignmentRequest request);

    Assignment update(Long id, AssignmentRequest request);

    void delete(Long id);
}
