package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import com.dengjx.affairs.assignment.Assignment;
import com.dengjx.affairs.assignment.AssignmentMapper;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.enrollment.EnrollmentMapper;
import com.dengjx.affairs.enrollment.EnrollmentService;
import com.dengjx.affairs.grade.GradeMapper;
import com.dengjx.affairs.grade.GradeService;
import com.dengjx.affairs.grade.dto.GradeRequest;
import com.dengjx.affairs.security.UserContextService;
import org.junit.jupiter.api.Test;

class BusinessRuleTests {

    @Test
    void studentCannotEnrollWhenAssignmentIsClosed() {
        EnrollmentMapper enrollmentMapper = mock(EnrollmentMapper.class);
        AssignmentMapper assignmentMapper = mock(AssignmentMapper.class);
        GradeMapper gradeMapper = mock(GradeMapper.class);
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(3L);
        assignment.setEnrollmentOpen(false);

        when(assignmentMapper.selectById(3L)).thenReturn(assignment);

        EnrollmentService service = new EnrollmentService(
                enrollmentMapper,
                assignmentMapper,
                gradeMapper,
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.enroll(10L, 3L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("未开放选课");
    }

    @Test
    void gradeScoreMustBeBetween0And100() {
        GradeService service = new GradeService(
                mock(GradeMapper.class),
                mock(EnrollmentMapper.class),
                mock(AssignmentMapper.class),
                new FakeUserContextService(),
                null);

        assertThatThrownBy(() -> service.adminCreate(new GradeRequest(1L, BigDecimal.valueOf(101))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("成绩");
    }

    private static class FakeUserContextService extends UserContextService {

        FakeUserContextService() {
            super(null);
        }

        @Override
        public Long getStudentId(Long userId) {
            return 1L;
        }

        @Override
        public Long getTeacherId(Long userId) {
            return 1L;
        }
    }
}
