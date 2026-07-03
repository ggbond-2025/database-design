package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.SchoolClassRequest;
import com.dengjx.affairs.entity.SchoolClass;

public interface SchoolClassService {

    PageResult<SchoolClass> list(String keyword, long page, long size);

    SchoolClass getById(Long id);

    SchoolClass create(SchoolClassRequest request);

    SchoolClass update(Long id, SchoolClassRequest request);

    void delete(Long id);
}
