package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import com.dengjx.affairs.assignment.AssignmentMapper;
import com.dengjx.affairs.assignment.AssignmentService;
import com.dengjx.affairs.assignment.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.course.CourseMapper;
import com.dengjx.affairs.course.CourseService;
import com.dengjx.affairs.course.dto.CourseRequest;
import com.dengjx.affairs.student.StudentMapper;
import com.dengjx.affairs.student.StudentService;
import com.dengjx.affairs.student.dto.StudentRequest;
import com.dengjx.affairs.teacher.TeacherMapper;
import com.dengjx.affairs.teacher.TeacherService;
import com.dengjx.affairs.teacher.dto.TeacherRequest;
import org.junit.jupiter.api.Test;

class AcademicValidationTests {

    @Test
    void studentAgeMustBeBetween15And35() {
        StudentService service = new StudentService(mock(StudentMapper.class));
        StudentRequest request = new StudentRequest("20239999", "测试学生", "MALE", 14, 1L, 1L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学生年龄");
    }

    @Test
    void teacherAgeMustBeBetween20And75() {
        TeacherService service = new TeacherService(mock(TeacherMapper.class));
        TeacherRequest request = new TeacherRequest("T9999999", "测试教师", "FEMALE", 19, "讲师", "13800009999");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("教师年龄");
    }

    @Test
    void courseHoursAndCreditMustBePositive() {
        CourseService service = new CourseService(mock(CourseMapper.class));
        CourseRequest request = new CourseRequest("CS999", "测试课程", 0, "EXAM", BigDecimal.ONE);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("课程学时");
    }

    @Test
    void assignmentSemesterAndCapacityMustBeValid() {
        AssignmentService service = new AssignmentService(mock(AssignmentMapper.class));
        AssignmentRequest request = new AssignmentRequest(1L, 1L, 1L, "2023-2024", 3, "REQUIRED", 30, true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("学期");
    }
}
