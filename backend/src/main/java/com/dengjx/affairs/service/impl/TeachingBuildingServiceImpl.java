package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeachingBuildingRequest;
import com.dengjx.affairs.entity.TeachingBuilding;
import com.dengjx.affairs.mapper.TeachingBuildingMapper;
import com.dengjx.affairs.service.TeachingBuildingService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TeachingBuildingServiceImpl implements TeachingBuildingService {

    private final TeachingBuildingMapper teachingBuildingMapper;

    public TeachingBuildingServiceImpl(TeachingBuildingMapper teachingBuildingMapper) {
        this.teachingBuildingMapper = teachingBuildingMapper;
    }

    public PageResult<TeachingBuilding> list(String keyword, long page, long size) {
        LambdaQueryWrapper<TeachingBuilding> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TeachingBuilding::getBuildingName, keyword);
        }
        wrapper.orderByAsc(TeachingBuilding::getBuildingId);
        Page<TeachingBuilding> result = teachingBuildingMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public TeachingBuilding getById(Long id) {
        TeachingBuilding building = teachingBuildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("教学楼不存在");
        }
        return building;
    }

    public TeachingBuilding create(TeachingBuildingRequest request) {
        TeachingBuilding building = new TeachingBuilding();
        building.setBuildingName(request.buildingName());
        teachingBuildingMapper.insert(building);
        return building;
    }

    public TeachingBuilding update(Long id, TeachingBuildingRequest request) {
        TeachingBuilding building = getById(id);
        building.setBuildingName(request.buildingName());
        teachingBuildingMapper.updateById(building);
        return building;
    }

    public void delete(Long id) {
        if (teachingBuildingMapper.deleteById(id) == 0) {
            throw new BusinessException("教学楼不存在");
        }
    }
}
