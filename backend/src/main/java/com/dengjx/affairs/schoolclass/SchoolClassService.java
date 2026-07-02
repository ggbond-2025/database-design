package com.dengjx.affairs.schoolclass;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.schoolclass.dto.SchoolClassRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SchoolClassService {

    private final SchoolClassMapper classMapper;

    public SchoolClassService(SchoolClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    public PageResult<SchoolClass> list(String keyword, long page, long size) {
        LambdaQueryWrapper<SchoolClass> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SchoolClass::getClassName, keyword);
        }
        wrapper.orderByAsc(SchoolClass::getClassId);
        Page<SchoolClass> result = classMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public SchoolClass getById(Long id) {
        SchoolClass schoolClass = classMapper.selectById(id);
        if (schoolClass == null) {
            throw new BusinessException("班级不存在");
        }
        return schoolClass;
    }

    public SchoolClass create(SchoolClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        apply(request, schoolClass);
        classMapper.insert(schoolClass);
        return schoolClass;
    }

    public SchoolClass update(Long id, SchoolClassRequest request) {
        SchoolClass schoolClass = getById(id);
        apply(request, schoolClass);
        classMapper.updateById(schoolClass);
        return schoolClass;
    }

    public void delete(Long id) {
        if (classMapper.deleteById(id) == 0) {
            throw new BusinessException("班级不存在");
        }
    }

    private void apply(SchoolClassRequest request, SchoolClass schoolClass) {
        schoolClass.setClassName(request.className());
        schoolClass.setMajorId(request.majorId());
        schoolClass.setGradeYear(request.gradeYear());
    }
}
