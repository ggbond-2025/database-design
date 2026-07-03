package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.MajorMapper;
import com.dengjx.affairs.entity.Major;
import com.dengjx.affairs.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MajorServiceImpl implements MajorService {

    private final MajorMapper majorMapper;

    public MajorServiceImpl(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    public PageResult<Major> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Major::getMajorName, keyword);
        }
        wrapper.orderByAsc(Major::getMajorId);
        Page<Major> result = majorMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Major getById(Long id) {
        Major major = majorMapper.selectById(id);
        if (major == null) {
            throw new BusinessException("专业不存在");
        }
        return major;
    }

    public Major create(MajorRequest request) {
        Major major = new Major();
        major.setMajorName(request.majorName());
        majorMapper.insert(major);
        return major;
    }

    public Major update(Long id, MajorRequest request) {
        Major major = getById(id);
        major.setMajorName(request.majorName());
        majorMapper.updateById(major);
        return major;
    }

    public void delete(Long id) {
        if (majorMapper.deleteById(id) == 0) {
            throw new BusinessException("专业不存在");
        }
    }
}
