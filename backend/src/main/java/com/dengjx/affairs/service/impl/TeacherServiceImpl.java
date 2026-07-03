package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.TeacherMapper;
import com.dengjx.affairs.entity.Teacher;
import com.dengjx.affairs.service.TeacherService;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.TeacherRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TeacherServiceImpl implements TeacherService {

    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE");

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    public PageResult<Teacher> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Teacher::getTno, keyword).or().like(Teacher::getTname, keyword);
        }
        wrapper.orderByAsc(Teacher::getTeacherId);
        Page<Teacher> result = teacherMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Teacher getById(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        return teacher;
    }

    public Teacher create(TeacherRequest request) {
        validate(request);
        Teacher teacher = new Teacher();
        apply(request, teacher);
        teacherMapper.insert(teacher);
        return teacher;
    }

    public Teacher update(Long id, TeacherRequest request) {
        validate(request);
        Teacher teacher = getById(id);
        apply(request, teacher);
        teacherMapper.updateById(teacher);
        return teacher;
    }

    public void delete(Long id) {
        if (teacherMapper.deleteById(id) == 0) {
            throw new BusinessException("教师不存在");
        }
    }

    private void validate(TeacherRequest request) {
        if (request.age() == null || request.age() < 20 || request.age() > 75) {
            throw new BusinessException("教师年龄必须在20到75之间");
        }
        if (!GENDERS.contains(request.gender())) {
            throw new BusinessException("教师性别必须为MALE或FEMALE");
        }
    }

    private void apply(TeacherRequest request, Teacher teacher) {
        teacher.setTno(request.tno());
        teacher.setTname(request.tname());
        teacher.setGender(request.gender());
        teacher.setAge(request.age());
        teacher.setTitle(request.title());
        teacher.setPhone(request.phone());
    }
}
