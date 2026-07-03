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
import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.service.StudentService;
import com.dengjx.affairs.service.impl.StudentServiceImpl;
import com.dengjx.affairs.dto.StudentRequest;
import com.dengjx.affairs.mapper.TeacherMapper;
import com.dengjx.affairs.service.TeacherService;
import com.dengjx.affairs.service.impl.TeacherServiceImpl;
import com.dengjx.affairs.dto.TeacherRequest;
import org.junit.jupiter.api.Test;

class AcademicValidationTests {

    @Test
    void studentAgeMustBeBetween15And35() {
        StudentService service = new StudentServiceImpl(mock(StudentMapper.class));
        StudentRequest request = new StudentRequest("20239999", "测试学生", "MALE", 14, 1L, 1L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学生年龄");
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
    void assignmentSemesterAndCapacityMustBeValid() {
        AssignmentService service = new AssignmentServiceImpl(mock(AssignmentMapper.class));
        AssignmentRequest request = new AssignmentRequest(1L, 1L, 1L, "2023-2024", 3, "REQUIRED", 30, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学期");
    }
}
