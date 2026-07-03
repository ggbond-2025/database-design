package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.RegionRequest;
import com.dengjx.affairs.entity.Region;

public interface RegionService {

    PageResult<Region> list(String keyword, long page, long size);

    Region getById(Long id);

    Region create(RegionRequest request);

    Region update(Long id, RegionRequest request);

    void delete(Long id);
}
