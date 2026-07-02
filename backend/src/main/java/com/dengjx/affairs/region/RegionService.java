package com.dengjx.affairs.region;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.region.dto.RegionRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RegionService {

    private final RegionMapper regionMapper;

    public RegionService(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    public PageResult<Region> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Region::getRegionName, keyword);
        }
        wrapper.orderByAsc(Region::getRegionId);
        Page<Region> result = regionMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Region getById(Long id) {
        Region region = regionMapper.selectById(id);
        if (region == null) {
            throw new BusinessException("地区不存在");
        }
        return region;
    }

    public Region create(RegionRequest request) {
        Region region = new Region();
        region.setRegionName(request.regionName());
        regionMapper.insert(region);
        return region;
    }

    public Region update(Long id, RegionRequest request) {
        Region region = getById(id);
        region.setRegionName(request.regionName());
        regionMapper.updateById(region);
        return region;
    }

    public void delete(Long id) {
        if (regionMapper.deleteById(id) == 0) {
            throw new BusinessException("地区不存在");
        }
    }
}
