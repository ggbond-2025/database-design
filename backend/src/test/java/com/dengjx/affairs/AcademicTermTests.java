package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.dengjx.affairs.service.impl.AcademicTermService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AcademicTermTests {

    @Test
    void calculatesGradeAndSemesterFromAdmissionDateUsingBeijingAcademicCalendar() {
        AcademicTermService service = new AcademicTermService();

        AcademicTermService.AcademicTerm term = service.calculate(
                LocalDate.of(2023, 9, 1),
                LocalDate.of(2026, 7, 3));

        assertThat(term.grade()).isEqualTo(3);
        assertThat(term.semester()).isEqualTo(2);
        assertThat(term.academicYear()).isEqualTo("2025-2026");
        assertThat(term.label()).isEqualTo("大三下学期");
    }
}
