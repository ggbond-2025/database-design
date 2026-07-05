package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.entity.Grade;
import com.dengjx.affairs.entity.MajorTransferApplication;
import com.dengjx.affairs.entity.Student;
import com.dengjx.affairs.entity.TeachingEvaluation;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.mapper.MajorTransferApplicationMapper;
import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.mapper.TeachingEvaluationMapper;
import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.service.StudentService;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.StudentImportResult;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.security.UserContextService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Set<String> GENDERS = Set.of("MALE", "FEMALE");
    private static final String DEFAULT_IMPORTED_STUDENT_PASSWORD = "123456";
    private static final List<String> REQUIRED_CSV_HEADERS = List.of(
            "sno",
            "sname",
            "gender",
            "age",
            "classId",
            "regionId",
            "admissionDate");

    private final StudentMapper studentMapper;
    private final UserAccountMapper userAccountMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final GradeMapper gradeMapper;
    private final TeachingEvaluationMapper teachingEvaluationMapper;
    private final MajorTransferApplicationMapper majorTransferApplicationMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final UserContextService userContextService;
    private final AcademicTermService academicTermService;

    @Autowired
    public StudentServiceImpl(
            StudentMapper studentMapper,
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate,
            UserContextService userContextService,
            AcademicTermService academicTermService,
            EnrollmentMapper enrollmentMapper,
            GradeMapper gradeMapper,
            TeachingEvaluationMapper teachingEvaluationMapper,
            MajorTransferApplicationMapper majorTransferApplicationMapper) {
        this.studentMapper = studentMapper;
        this.userAccountMapper = userAccountMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.gradeMapper = gradeMapper;
        this.teachingEvaluationMapper = teachingEvaluationMapper;
        this.majorTransferApplicationMapper = majorTransferApplicationMapper;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.userContextService = userContextService;
        this.academicTermService = academicTermService;
    }

    public StudentServiceImpl(StudentMapper studentMapper) {
        this(studentMapper, null, null);
    }

    public StudentServiceImpl(
            StudentMapper studentMapper,
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder) {
        this(studentMapper, userAccountMapper, passwordEncoder, null, null, null, null);
    }

    public StudentServiceImpl(
            StudentMapper studentMapper,
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder,
            EnrollmentMapper enrollmentMapper,
            GradeMapper gradeMapper,
            TeachingEvaluationMapper teachingEvaluationMapper,
            MajorTransferApplicationMapper majorTransferApplicationMapper) {
        this.studentMapper = studentMapper;
        this.userAccountMapper = userAccountMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.gradeMapper = gradeMapper;
        this.teachingEvaluationMapper = teachingEvaluationMapper;
        this.majorTransferApplicationMapper = majorTransferApplicationMapper;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional
    public void delete(Long id) {
        List<Long> enrollmentIds = enrollmentMapper.selectList(new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getStudentId, id))
                .stream()
                .map(Enrollment::getEnrollmentId)
                .toList();
        if (!enrollmentIds.isEmpty()) {
            teachingEvaluationMapper.delete(new LambdaQueryWrapper<TeachingEvaluation>()
                    .in(TeachingEvaluation::getEnrollmentId, enrollmentIds));
            gradeMapper.delete(new LambdaQueryWrapper<Grade>()
                    .in(Grade::getEnrollmentId, enrollmentIds));
        }
        enrollmentMapper.delete(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentId, id));
        majorTransferApplicationMapper.delete(new LambdaQueryWrapper<MajorTransferApplication>()
                .eq(MajorTransferApplication::getStudentId, id));
        userAccountMapper.delete(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getStudentId, id));
        if (studentMapper.deleteById(id) == 0) {
            throw new BusinessException("学生不存在");
        }
    }

    public StudentImportResult importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择需要导入的CSV文件");
        }

        int successCount = 0;
        List<String> errors = new ArrayList<>();
        try (CSVParser parser = parseCsv(file)) {
            validateCsvHeaders(parser);
            for (CSVRecord record : parser) {
                try {
                    Student student = create(toStudentRequest(record));
                    createDefaultStudentAccount(student);
                    successCount++;
                } catch (BusinessException exception) {
                    errors.add(rowError(record, exception.getMessage()));
                } catch (DataIntegrityViolationException exception) {
                    errors.add(rowError(record, "数据已存在或关联数据不存在"));
                }
            }
        } catch (IOException exception) {
            throw new BusinessException("CSV文件读取失败");
        }
        return new StudentImportResult(successCount, errors.size(), errors);
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

    private void createDefaultStudentAccount(Student student) {
        if (userAccountMapper == null || passwordEncoder == null) {
            return;
        }
        UserAccount account = new UserAccount();
        account.setUsername("s" + student.getSno());
        account.setPassword(passwordEncoder.encode(DEFAULT_IMPORTED_STUDENT_PASSWORD));
        account.setRole("STUDENT");
        account.setStudentId(student.getStudentId());
        account.setTeacherId(null);
        account.setEnabled(true);
        userAccountMapper.insert(account);
    }

    private CSVParser parseCsv(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build()
                .parse(new StringReader(content));
    }

    private void validateCsvHeaders(CSVParser parser) {
        Set<String> headers = parser.getHeaderMap().keySet();
        List<String> missingHeaders = REQUIRED_CSV_HEADERS.stream()
                .filter(header -> !headers.contains(header))
                .toList();
        if (!missingHeaders.isEmpty()) {
            throw new BusinessException("CSV文件缺少表头字段：" + String.join("、", missingHeaders));
        }
    }

    private StudentRequest toStudentRequest(CSVRecord record) {
        return new StudentRequest(
                requiredText(record, "sno"),
                requiredText(record, "sname"),
                requiredText(record, "gender"),
                parseInteger(record, "age"),
                parseLong(record, "classId"),
                parseLong(record, "regionId"),
                parseDate(record, "admissionDate"));
    }

    private String requiredText(CSVRecord record, String header) {
        String value = record.get(header);
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(header + "不能为空");
        }
        return value.trim();
    }

    private Integer parseInteger(CSVRecord record, String header) {
        String value = requiredText(record, header);
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new BusinessException(header + "必须为整数");
        }
    }

    private Long parseLong(CSVRecord record, String header) {
        String value = requiredText(record, header);
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new BusinessException(header + "必须为整数");
        }
    }

    private LocalDate parseDate(CSVRecord record, String header) {
        String value = requiredText(record, header);
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(header + "必须为yyyy-MM-dd格式");
        }
    }

    private String rowError(CSVRecord record, String message) {
        return "第" + (record.getRecordNumber() + 1) + "行：" + message;
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
