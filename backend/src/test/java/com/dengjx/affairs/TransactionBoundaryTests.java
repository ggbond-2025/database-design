package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
import com.dengjx.affairs.service.impl.GradeServiceImpl;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.service.impl.StudentServiceImpl;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class TransactionBoundaryTests {

    @Test
    void assignmentWriteOperationsAreTransactional() throws NoSuchMethodException {
        assertTransactional(AssignmentServiceImpl.class.getMethod("create", com.dengjx.affairs.dto.AssignmentRequest.class));
        assertTransactional(AssignmentServiceImpl.class.getMethod("update", Long.class, com.dengjx.affairs.dto.AssignmentRequest.class));
        assertTransactional(AssignmentServiceImpl.class.getMethod("delete", Long.class));
    }

    @Test
    void enrollmentWriteOperationsAreTransactional() throws NoSuchMethodException {
        assertTransactional(EnrollmentServiceImpl.class.getMethod("enroll", Long.class, Long.class));
        assertTransactional(EnrollmentServiceImpl.class.getMethod("studentDrop", Long.class, Long.class));
        assertTransactional(EnrollmentServiceImpl.class.getMethod("adminCreate", com.dengjx.affairs.dto.EnrollmentRequest.class));
        assertTransactional(EnrollmentServiceImpl.class.getMethod("adminDrop", Long.class));
    }

    @Test
    void gradeWriteOperationsAreTransactional() throws NoSuchMethodException {
        assertTransactional(GradeServiceImpl.class.getMethod("teacherCreate", Long.class, com.dengjx.affairs.dto.GradeRequest.class));
        assertTransactional(GradeServiceImpl.class.getMethod("teacherUpdate", Long.class, Long.class, com.dengjx.affairs.dto.GradeRequest.class));
        assertTransactional(GradeServiceImpl.class.getMethod("adminCreate", com.dengjx.affairs.dto.GradeRequest.class));
        assertTransactional(GradeServiceImpl.class.getMethod("adminUpdate", Long.class, com.dengjx.affairs.dto.GradeRequest.class));
        assertTransactional(GradeServiceImpl.class.getMethod("adminDelete", Long.class));
    }

    @Test
    void studentCascadeDeleteIsTransactional() throws NoSuchMethodException {
        assertTransactional(StudentServiceImpl.class.getMethod("delete", Long.class));
    }

    private void assertTransactional(Method method) {
        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
    }
}
