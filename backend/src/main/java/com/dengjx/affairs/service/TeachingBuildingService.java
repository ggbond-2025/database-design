package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingBuildingRequest;
import com.dengjx.affairs.entity.TeachingBuilding;

public interface TeachingBuildingService {

    PageResult<TeachingBuilding> list(String keyword, long page, long size);

    TeachingBuilding getById(Long id);

    TeachingBuilding create(TeachingBuildingRequest request);

    TeachingBuilding update(Long id, TeachingBuildingRequest request);

    void delete(Long id);
}
