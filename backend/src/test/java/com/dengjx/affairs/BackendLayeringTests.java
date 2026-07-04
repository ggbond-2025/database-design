package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.service.AssignmentService;
import com.dengjx.affairs.service.AuthService;
import com.dengjx.affairs.service.CourseService;
import com.dengjx.affairs.service.EnrollmentService;
import com.dengjx.affairs.service.GradeService;
import com.dengjx.affairs.service.LookupService;
import com.dengjx.affairs.service.MajorService;
import com.dengjx.affairs.service.RegionService;
import com.dengjx.affairs.service.SchoolClassService;
import com.dengjx.affairs.service.StatisticsService;
import com.dengjx.affairs.service.StudentService;
import com.dengjx.affairs.service.TeacherService;
import com.dengjx.affairs.service.UserAccountService;
import com.dengjx.affairs.service.impl.AssignmentServiceImpl;
import com.dengjx.affairs.service.impl.AuthServiceImpl;
import com.dengjx.affairs.service.impl.CourseServiceImpl;
import com.dengjx.affairs.service.impl.EnrollmentServiceImpl;
import com.dengjx.affairs.service.impl.GradeServiceImpl;
import com.dengjx.affairs.service.impl.LookupServiceImpl;
import com.dengjx.affairs.service.impl.MajorServiceImpl;
import com.dengjx.affairs.service.impl.RegionServiceImpl;
import com.dengjx.affairs.service.impl.SchoolClassServiceImpl;
import com.dengjx.affairs.service.impl.StatisticsServiceImpl;
import com.dengjx.affairs.service.impl.StudentServiceImpl;
import com.dengjx.affairs.service.impl.TeacherServiceImpl;
import com.dengjx.affairs.service.impl.UserAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

class BackendLayeringTests {

    @Test
    void servicesExposeInterfacesAndImplementationsStayUnderImplPackage() {
        assertServiceContract(AssignmentService.class, AssignmentServiceImpl.class);
        assertServiceContract(AuthService.class, AuthServiceImpl.class);
        assertServiceContract(CourseService.class, CourseServiceImpl.class);
        assertServiceContract(EnrollmentService.class, EnrollmentServiceImpl.class);
        assertServiceContract(GradeService.class, GradeServiceImpl.class);
        assertServiceContract(LookupService.class, LookupServiceImpl.class);
        assertServiceContract(MajorService.class, MajorServiceImpl.class);
        assertServiceContract(RegionService.class, RegionServiceImpl.class);
        assertServiceContract(SchoolClassService.class, SchoolClassServiceImpl.class);
        assertServiceContract(StatisticsService.class, StatisticsServiceImpl.class);
        assertServiceContract(StudentService.class, StudentServiceImpl.class);
        assertServiceContract(TeacherService.class, TeacherServiceImpl.class);
        assertServiceContract(UserAccountService.class, UserAccountServiceImpl.class);
    }

    @Test
    void gradeServiceExposesAdminTeachingClassQueries() throws NoSuchMethodException {
        GradeService.class.getMethod("adminAssignmentList", long.class, long.class);
        GradeService.class.getMethod("adminAssignmentGrades", Long.class);
    }

    private <T> void assertServiceContract(Class<T> serviceInterface, Class<? extends T> implementationClass) {
        assertThat(serviceInterface).isInterface();
        assertThat(implementationClass).isAssignableTo(serviceInterface);
        assertThat(implementationClass).hasAnnotation(Service.class);
        assertThat(implementationClass.getPackageName()).isEqualTo("com.dengjx.affairs.service.impl");
    }
}
