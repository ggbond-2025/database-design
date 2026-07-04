package com.dengjx.affairs.service.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class AcademicTermService {

    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    private final Clock clock;

    public AcademicTermService() {
        this(Clock.system(BEIJING_ZONE));
    }

    AcademicTermService(Clock clock) {
        this.clock = clock;
    }

    public AcademicTerm current(LocalDate admissionDate) {
        return calculate(admissionDate, LocalDate.now(clock));
    }

    public AcademicTerm calculate(LocalDate admissionDate, LocalDate currentDate) {
        int academicYearStart = currentDate.getMonthValue() >= 9
                ? currentDate.getYear()
                : currentDate.getYear() - 1;
        int admissionAcademicYear = admissionDate.getMonthValue() >= 9
                ? admissionDate.getYear()
                : admissionDate.getYear() - 1;
        int grade = Math.max(1, academicYearStart - admissionAcademicYear + 1);
        int semester = currentDate.getMonthValue() >= 9 || currentDate.getMonthValue() <= 1 ? 1 : 2;
        String academicYear = academicYearStart + "-" + (academicYearStart + 1);
        return new AcademicTerm(grade, semester, academicYear, gradeLabel(grade) + (semester == 1 ? "上学期" : "下学期"));
    }

    private String gradeLabel(int grade) {
        return switch (grade) {
            case 1 -> "大一";
            case 2 -> "大二";
            case 3 -> "大三";
            case 4 -> "大四";
            default -> "大" + grade;
        };
    }

    public record AcademicTerm(int grade, int semester, String academicYear, String label) {
    }
}
