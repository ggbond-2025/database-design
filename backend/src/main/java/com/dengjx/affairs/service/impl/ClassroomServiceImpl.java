package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.ClassroomRequest;
import com.dengjx.affairs.entity.Classroom;
import com.dengjx.affairs.mapper.ClassroomMapper;
import com.dengjx.affairs.service.ClassroomService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomMapper classroomMapper;

    public ClassroomServiceImpl(ClassroomMapper classroomMapper) {
        this.classroomMapper = classroomMapper;
    }

    public PageResult<Classroom> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Classroom::getClassroomName, keyword);
        }
        wrapper.orderByAsc(Classroom::getClassroomId);
        Page<Classroom> result = classroomMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Classroom getById(Long id) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }
        return classroom;
    }

    public Classroom create(ClassroomRequest request) {
        validate(request);
        Classroom classroom = new Classroom();
        apply(request, classroom);
        classroomMapper.insert(classroom);
        return classroom;
    }

    public Classroom update(Long id, ClassroomRequest request) {
        validate(request);
        Classroom classroom = getById(id);
        apply(request, classroom);
        classroomMapper.updateById(classroom);
        return classroom;
    }

    public void delete(Long id) {
        if (classroomMapper.deleteById(id) == 0) {
            throw new BusinessException("教室不存在");
        }
    }

    private void validate(ClassroomRequest request) {
        if (request.capacity() == null || request.capacity() <= 0) {
            throw new BusinessException("教室容量必须大于0");
        }
    }

    private void apply(ClassroomRequest request, Classroom classroom) {
        classroom.setBuildingId(request.buildingId());
        classroom.setClassroomName(request.classroomName());
        classroom.setCapacity(request.capacity());
    }
}
