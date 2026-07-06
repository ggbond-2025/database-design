package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.service.AssignmentService;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.dto.StudentImportResult;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.mapper.CourseMapper;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.mapper.MajorTransferApplicationMapper;
import com.dengjx.affairs.service.CourseService;
import com.dengjx.affairs.service.impl.CourseServiceImpl;
import com.dengjx.affairs.dto.CourseRequest;
import com.dengjx.affairs.dto.MajorRequest;
import com.dengjx.affairs.dto.MajorCourseRequest;
import com.dengjx.affairs.mapper.MajorMapper;
import com.dengjx.affairs.mapper.MajorCourseMapper;
import com.dengjx.affairs.service.MajorService;
import com.dengjx.affairs.service.MajorCourseService;
import com.dengjx.affairs.service.impl.MajorServiceImpl;
import com.dengjx.affairs.service.impl.MajorCourseServiceImpl;
import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.mapper.TeachingEvaluationMapper;
import com.dengjx.affairs.mapper.UserAccountMapper;
import com.dengjx.affairs.service.StudentService;
import com.dengjx.affairs.service.impl.StudentServiceImpl;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.mapper.TeacherMapper;
import com.dengjx.affairs.service.TeacherService;
import com.dengjx.affairs.service.impl.TeacherServiceImpl;
import com.dengjx.affairs.dto.TeacherRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

class AcademicValidationTests {

    @Test
    void studentAgeMustBeBetween15And35() {
        StudentService service = new StudentServiceImpl(mock(StudentMapper.class));
        StudentRequest request = new StudentRequest("20239999", "测试学生", "MALE", 14, 1L, 1L, LocalDate.of(2023, 9, 1));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学生年龄");
    }

    @Test
    void studentAdmissionDateCannotBeInFuture() {
        StudentService service = new StudentServiceImpl(mock(StudentMapper.class));
        StudentRequest request = new StudentRequest("20239999", "测试学生", "MALE", 20, 1L, 1L, LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("入学时间");
    }

    @Test
    void importStudentsFromCsvCreatesValidRowsAndReportsRowErrors() {
        StudentMapper studentMapper = mock(StudentMapper.class);
        UserAccountMapper userAccountMapper = mock(UserAccountMapper.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(studentMapper.insert(any(com.dengjx.affairs.entity.Student.class))).thenAnswer(invocation -> {
            com.dengjx.affairs.entity.Student student = invocation.getArgument(0);
            student.setStudentId(99L);
            return 1;
        });
        StudentService service = new StudentServiceImpl(studentMapper, userAccountMapper, passwordEncoder);
        String csv = """
                sno,sname,gender,age,classId,regionId,admissionDate
                20260001,张三,MALE,18,1,2,2025-09-01
                20260002,李四,FEMALE,14,1,2,2025-09-01
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        StudentImportResult result = service.importCsv(file);

        assertThat(result.successCount()).isEqualTo(1);
        assertThat(result.failureCount()).isEqualTo(1);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0)).contains("第3行").contains("学生年龄");

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountMapper).insert(accountCaptor.capture());
        UserAccount account = accountCaptor.getValue();
        assertThat(account.getUsername()).isEqualTo("s20260001");
        assertThat(account.getPassword()).isEqualTo("encoded-password");
        assertThat(account.getRole()).isEqualTo("STUDENT");
        assertThat(account.getStudentId()).isEqualTo(99L);
        assertThat(account.getTeacherId()).isNull();
        assertThat(account.getEnabled()).isTrue();
    }

    @Test
    void importStudentsFromCsvRemovesInsertedStudentWhenDefaultAccountCreationFails() {
        StudentMapper studentMapper = mock(StudentMapper.class);
        UserAccountMapper userAccountMapper = mock(UserAccountMapper.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(studentMapper.insert(any(com.dengjx.affairs.entity.Student.class))).thenAnswer(invocation -> {
            com.dengjx.affairs.entity.Student student = invocation.getArgument(0);
            student.setStudentId(99L);
            return 1;
        });
        when(userAccountMapper.insert(any(UserAccount.class))).thenThrow(new DataIntegrityViolationException("duplicate account"));
        StudentService service = new StudentServiceImpl(studentMapper, userAccountMapper, passwordEncoder);
        String csv = """
                sno,sname,gender,age,classId,regionId,admissionDate
                20260001,张三,MALE,18,1,2,2025-09-01
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        StudentImportResult result = service.importCsv(file);

        assertThat(result.successCount()).isZero();
        assertThat(result.failureCount()).isEqualTo(1);
        verify(studentMapper).deleteById(99L);
    }

    @Test
    void deleteStudentCleansDependentRecordsBeforeStudent() {
        StudentMapper studentMapper = mock(StudentMapper.class);
        UserAccountMapper userAccountMapper = mock(UserAccountMapper.class);
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        GradeMapper gradeMapper = mock(GradeMapper.class);
        TeachingEvaluationMapper teachingEvaluationMapper = mock(TeachingEvaluationMapper.class);
        MajorTransferApplicationMapper majorTransferApplicationMapper = mock(MajorTransferApplicationMapper.class);
        Enrollment first = new Enrollment();
        first.setEnrollmentId(101L);
        Enrollment second = new Enrollment();
        second.setEnrollmentId(102L);
        when(enrollmentMapper.selectList(any())).thenReturn(List.of(first, second));
        when(studentMapper.deleteById(7L)).thenReturn(1);
        StudentService service = new StudentServiceImpl(
                studentMapper,
                userAccountMapper,
                null,
                enrollmentMapper,
                gradeMapper,
                teachingEvaluationMapper,
                majorTransferApplicationMapper);

        service.delete(7L);

        InOrder order = inOrder(
                enrollmentMapper,
                gradeMapper,
                teachingEvaluationMapper,
                majorTransferApplicationMapper,
                userAccountMapper,
                studentMapper);
        order.verify(enrollmentMapper).selectList(any());
        order.verify(teachingEvaluationMapper).delete(any());
        order.verify(gradeMapper).delete(any());
        order.verify(enrollmentMapper).delete(any());
        order.verify(majorTransferApplicationMapper).delete(any());
        order.verify(userAccountMapper).delete(any());
        order.verify(studentMapper).deleteById(7L);
    }

    @Test
    void teacherAgeMustBeBetween20And75() {
        TeacherService service = new TeacherServiceImpl(mock(TeacherMapper.class));
        TeacherRequest request = new TeacherRequest("T9999999", "测试教师", "FEMALE", 19, "讲师", "13800009999");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("教师年龄");
    }

    @Test
    void courseHoursAndCreditMustBePositive() {
        CourseService service = new CourseServiceImpl(mock(CourseMapper.class));
        CourseRequest request = new CourseRequest("CS999", "测试课程", 0, "EXAM", BigDecimal.ONE);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("课程学时");
    }

    @Test
    void majorCourseTargetGradeSemesterAndTypeMustBeValid() {
        MajorCourseService service = new MajorCourseServiceImpl(mock(MajorCourseMapper.class));
        MajorCourseRequest request = new MajorCourseRequest(1L, 1L, "REQUIRED", 5, 1);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("开设年级");
    }

    @Test
    void assignmentSemesterAndCapacityMustBeValid() {
        AssignmentService service = new AssignmentServiceImpl(mock(AssignmentMapper.class));
        AssignmentRequest request = new AssignmentRequest(
                1L,
                1L,
                1L,
                1L,
                "2023-2024",
                3,
                30,
                true,
                1,
                LocalTime.of(8, 0),
                LocalTime.of(9, 40),
                null,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学期");
    }

    @Test
    void assignmentCapacityCannotExceedClassroomCapacity() {
        AssignmentService service = new AssignmentServiceImpl(mock(AssignmentMapper.class), new AssignmentMetadataJdbcTemplate(32, 20));
        AssignmentRequest request = new AssignmentRequest(
                1L,
                1L,
                1L,
                1L,
                "2023-2024",
                1,
                30,
                true,
                1,
                LocalTime.of(8, 0),
                LocalTime.of(9, 40),
                null,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不能超过教室容量");
    }

    @Test
    void assignmentWeekdayMustBeBetweenMondayAndFriday() {
        AssignmentService service = new AssignmentServiceImpl(mock(AssignmentMapper.class));
        AssignmentRequest request = new AssignmentRequest(
                1L,
                1L,
                1L,
                1L,
                "2023-2024",
                1,
                30,
                true,
                6,
                LocalTime.of(8, 0),
                LocalTime.of(9, 40),
                null,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("星期");
    }

    @Test
    void fortyEightHourAssignmentRequiresSecondSlot() {
        AssignmentService service = new AssignmentServiceImpl(mock(AssignmentMapper.class), new FixedHoursJdbcTemplate(48));
        AssignmentRequest request = new AssignmentRequest(
                1L,
                1L,
                1L,
                1L,
                "2023-2024",
                1,
                30,
                true,
                1,
                LocalTime.of(8, 0),
                LocalTime.of(9, 40),
                null,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("48学时课程需要配置第二个上课时间");
    }

    @Test
    void teacherCannotHaveOverlappingAssignmentsInSameTerm() {
        AssignmentMapper mapper = mock(AssignmentMapper.class);
        Assignment existing = new Assignment();
        existing.setAssignmentId(9L);
        existing.setMajorCourseId(2L);
        existing.setTeacherId(1L);
        existing.setAcademicYear("2023-2024");
        existing.setSemester(1);
        existing.setWeekdayOne(1);
        existing.setStartTimeOne(LocalTime.of(8, 0));
        existing.setEndTimeOne(LocalTime.of(9, 40));
        when(mapper.selectList(any())).thenReturn(List.of(existing));

        AssignmentService service = new AssignmentServiceImpl(mapper, new FixedHoursJdbcTemplate(32));
        AssignmentRequest request = new AssignmentRequest(
                1L,
                1L,
                1L,
                1L,
                "2023-2024",
                1,
                30,
                true,
                1,
                LocalTime.of(9, 0),
                LocalTime.of(10, 40),
                null,
                null,
                null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("教师任课时间冲突");
    }

    private static class FixedHoursJdbcTemplate extends JdbcTemplate {
        private final Integer hours;

        FixedHoursJdbcTemplate(Integer hours) {
            this.hours = hours;
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            return requiredType.cast(hours);
        }
    }

    private static class AssignmentMetadataJdbcTemplate extends JdbcTemplate {
        private final Integer hours;
        private final Integer classroomCapacity;

        AssignmentMetadataJdbcTemplate(Integer hours, Integer classroomCapacity) {
            this.hours = hours;
            this.classroomCapacity = classroomCapacity;
        }

        @Override
        public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
            if (sql.contains("dengjx_classrooms13")) {
                return requiredType.cast(classroomCapacity);
            }
            return requiredType.cast(hours);
        }
    }

    @Test
    void majorGraduationCreditsMustBePositive() {
        MajorService service = new MajorServiceImpl(mock(MajorMapper.class));
        MajorRequest request = new MajorRequest("测试专业", BigDecimal.ZERO);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("毕业学分");
    }
}
