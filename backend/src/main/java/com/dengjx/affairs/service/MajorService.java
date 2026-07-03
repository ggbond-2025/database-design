package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorRequest;
import com.dengjx.affairs.entity.Major;

public interface MajorService {

    PageResult<Major> list(String keyword, long page, long size);

    Major getById(Long id);

    Major create(MajorRequest request);

    Major update(Long id, MajorRequest request);

    void delete(Long id);
}
