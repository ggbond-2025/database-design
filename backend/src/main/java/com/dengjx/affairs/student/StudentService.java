package com.dengjx.affairs.student;

import java.math.BigDecimal;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.student.dto.StudentRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentService {

    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE");

    private final StudentMapper studentMapper;

    public StudentService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    public PageResult<Student> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Student::getSno, keyword).or().like(Student::getSname, keyword);
        }
        wrapper.orderByAsc(Student::getStudentId);
        Page<Student> result = studentMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Student getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return student;
    }

    public Student create(StudentRequest request) {
        validate(request);
        Student student = new Student();
        apply(request, student);
        student.setTotalCredits(BigDecimal.ZERO);
        studentMapper.insert(student);
        return student;
    }

    public Student update(Long id, StudentRequest request) {
        validate(request);
        Student student = getById(id);
        apply(request, student);
        studentMapper.updateById(student);
        return student;
    }

    public void delete(Long id) {
        if (studentMapper.deleteById(id) == 0) {
            throw new BusinessException("学生不存在");
        }
    }

    private void validate(StudentRequest request) {
        if (request.age() == null || request.age() < 15 || request.age() > 35) {
            throw new BusinessException("学生年龄必须在15到35之间");
        }
        if (!GENDERS.contains(request.gender())) {
            throw new BusinessException("学生性别必须为MALE或FEMALE");
        }
    }

    private void apply(StudentRequest request, Student student) {
        student.setSno(request.sno());
        student.setSname(request.sname());
        student.setGender(request.gender());
        student.setAge(request.age());
        student.setClassId(request.classId());
        student.setRegionId(request.regionId());
    }
}
