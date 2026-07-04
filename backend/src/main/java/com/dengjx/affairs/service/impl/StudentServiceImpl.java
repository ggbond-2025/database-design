package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.entity.Student;
import com.dengjx.affairs.service.StudentService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.security.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE");

    private final StudentMapper studentMapper;
    private final JdbcTemplate jdbcTemplate;
    private final UserContextService userContextService;
    private final AcademicTermService academicTermService;

    @Autowired
    public StudentServiceImpl(
            StudentMapper studentMapper,
            JdbcTemplate jdbcTemplate,
            UserContextService userContextService,
            AcademicTermService academicTermService) {
        this.studentMapper = studentMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.userContextService = userContextService;
        this.academicTermService = academicTermService;
    }

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
        this.jdbcTemplate = null;
        this.userContextService = null;
        this.academicTermService = new AcademicTermService();
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
        if (request.admissionDate() == null || request.admissionDate().isAfter(LocalDate.now())) {
            throw new BusinessException("入学时间不能晚于当前日期");
        }
    }

    private void apply(StudentRequest request, Student student) {
        student.setSno(request.sno());
        student.setSname(request.sname());
        student.setGender(request.gender());
        student.setAge(request.age());
        student.setClassId(request.classId());
        student.setRegionId(request.regionId());
        student.setAdmissionDate(request.admissionDate());
    }

    public Map<String, Object> academicStatus(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        Student student = getById(studentId);
        AcademicTermService.AcademicTerm term = academicTermService.current(student.getAdmissionDate());
        Map<String, Object> base = jdbcTemplate.queryForMap("""
                SELECT s.djx_studentid13 AS studentId,
                       s.djx_sno13 AS sno,
                       s.djx_sname13 AS sname,
                       s.djx_admissiondate13 AS admissionDate,
                       cl.djx_classid13 AS classId,
                       cl.djx_classname13 AS className,
                       m.djx_majorid13 AS majorId,
                       m.djx_majorname13 AS majorName,
                       m.djx_graduationcredits13 AS graduationCredits
                FROM dengjx_students13 s
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                JOIN dengjx_majors13 m ON m.djx_majorid13 = cl.djx_majorid13
                WHERE s.djx_studentid13 = ?
                """, studentId);
        base.put("currentGrade", term.grade());
        base.put("currentSemester", term.semester());
        base.put("currentTermLabel", term.label());
        return base;
    }

    public List<Map<String, Object>> classmates(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        Student student = getById(studentId);
        return jdbcTemplate.queryForList("""
                SELECT s.djx_sno13 AS sno,
                       s.djx_sname13 AS sname,
                       s.djx_gender13 AS gender,
                       cl.djx_classname13 AS className
                FROM dengjx_students13 s
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                WHERE s.djx_classid13 = ?
                ORDER BY s.djx_sno13
                """, student.getClassId());
    }
}
