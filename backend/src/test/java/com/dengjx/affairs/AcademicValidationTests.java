package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.service.AssignmentService;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.mapper.CourseMapper;
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
import com.dengjx.affairs.service.StudentService;
import com.dengjx.affairs.service.impl.StudentServiceImpl;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.mapper.TeacherMapper;
import com.dengjx.affairs.service.TeacherService;
import com.dengjx.affairs.service.impl.TeacherServiceImpl;
import com.dengjx.affairs.dto.TeacherRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

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
        AssignmentRequest request = new AssignmentRequest(1L, 1L, 1L, "2023-2024", 3, 30, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学期");
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
