package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
import com.dengjx.affairs.service.impl.GradeServiceImpl;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class TransactionBoundaryTests {

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

    private void assertTransactional(Method method) {
        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
    }
}
