package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dengjx.affairs.entity.Student;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class SqlInitializationContractTests {

    private static final Path SQL_DIR = Path.of("..", "design", "sql");

    @Test
    void schemaAndTriggersKeepStudentTotalCreditsConsistent() throws IOException {
        String schema = readSql("02_schema.sql");
        String triggers = readSql("04_triggers.sql");

        assertThat(schema)
                .contains("djx_TotalCredits13 NUMERIC(8, 2) NOT NULL DEFAULT 0");
        assertThat(triggers)
                .contains("CREATE OR REPLACE FUNCTION Dengjx_RecalculateStudentCredits13")
                .contains("CREATE TRIGGER Dengjx_GradesAfterInsertUpdate13")
                .contains("CREATE TRIGGER Dengjx_GradesAfterDelete13")
                .contains("UPDATE Dengjx_Students13")
                .contains("djx_TotalCredits13");
    }

    @Test
    void studentEntityMapsTotalCreditsCacheColumn() throws NoSuchFieldException {
        Field totalCredits = Student.class.getDeclaredField("totalCredits");
        TableField tableField = totalCredits.getAnnotation(TableField.class);

        assertThat(tableField).isNotNull();
        assertThat(tableField.value()).isEqualTo("djx_totalcredits13");
    }

    @Test
    void seedDataCoversAllCurrentOpeningGrades() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(seed)
                .contains("'软件工程2501'")
                .contains("'计科2501'")
                .contains("'大数据2501'")
                .contains("'2025-09-01'")
                .contains("'2025-2026'")
                .contains("'CS102'")
                .contains("'CS104'")
                .contains("'CS106'");
    }

    @Test
    void seedDataIsFocusedOnFreshmanSecondSemesterTestTerm() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(seed)
                .contains("('软件工程', 160.00)")
                .contains("('计算机科学与技术', 160.00)")
                .contains("('数据科学与大数据技术', 160.00)")
                .doesNotContain("('网络工程', 160.00)")
                .doesNotContain("'2022-09-01'")
                .doesNotContain("'2023-09-01'")
                .doesNotContain("'2024-09-01'")
                .doesNotContain("'2023-2024'")
                .doesNotContain("'2024-2025'");
    }

    @Test
    void seedDataProvidesAtLeastEightCoursesForEachMajor() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(countMajorCourseRows(seed, "软件工程")).isGreaterThanOrEqualTo(8);
        assertThat(countMajorCourseRows(seed, "计算机科学与技术")).isGreaterThanOrEqualTo(8);
        assertThat(countMajorCourseRows(seed, "数据科学与大数据技术")).isGreaterThanOrEqualTo(8);
    }

    @Test
    void seedDataUsesFixedZhejiangCollegeTimeSlots() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(seed)
                .contains("TIME '08:00:00'")
                .contains("TIME '09:40:00'")
                .contains("TIME '09:55:00'")
                .contains("TIME '11:35:00'")
                .contains("TIME '13:30:00'")
                .contains("TIME '15:10:00'")
                .contains("TIME '15:25:00'")
                .contains("TIME '17:05:00'")
                .contains("TIME '18:30:00'")
                .contains("TIME '20:10:00'")
                .doesNotContain("TIME '10:00:00'")
                .doesNotContain("TIME '11:40:00'")
                .doesNotContain("TIME '14:00:00'")
                .doesNotContain("TIME '15:40:00'")
                .doesNotContain("TIME '16:00:00'")
                .doesNotContain("TIME '17:40:00'");
    }

    @Test
    void seedTeachingAssignmentsDoNotOverlapForSameTeacherInSameTerm() throws IOException {
        String seed = readSql("06_seed_data.sql");

        List<String> conflicts = findTeacherScheduleConflicts(seed);

        assertThat(conflicts).isEmpty();
    }

    @Test
    void schemaDefinesFinalTeachingEvaluationsPerEnrollment() throws IOException {
        String schema = readSql("02_schema.sql");

        assertThat(schema)
                .contains("CREATE TABLE Dengjx_TeachingEvaluations13")
                .contains("djx_EnrollmentId13 BIGINT NOT NULL UNIQUE REFERENCES Dengjx_Enrollments13")
                .contains("djx_Rating13 INTEGER NOT NULL CHECK (djx_Rating13 BETWEEN 1 AND 5)")
                .contains("djx_Comment13 VARCHAR(500) NULL")
                .contains("djx_EvaluatedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
    }

    @Test
    void schemaDefinesClassroomsMajorTransfersAndFinalExams() throws IOException {
        String schema = readSql("02_schema.sql");

        assertThat(schema)
                .contains("CREATE TABLE Dengjx_TeachingBuildings13")
                .contains("CREATE TABLE Dengjx_Classrooms13")
                .contains("djx_ClassroomId13 BIGINT NOT NULL REFERENCES Dengjx_Classrooms13")
                .contains("CREATE TABLE Dengjx_MajorTransferSettings13")
                .contains("djx_Enabled13 BOOLEAN NOT NULL DEFAULT TRUE")
                .contains("CREATE TABLE Dengjx_MajorTransferApplications13")
                .contains("djx_FromClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13")
                .contains("djx_EffectiveAcademicYear13 VARCHAR(20) NULL")
                .contains("djx_EffectiveSemester13 INTEGER NULL CHECK (djx_EffectiveSemester13 IN (1, 2))")
                .contains("djx_Status13 VARCHAR(20) NOT NULL DEFAULT 'PENDING'")
                .contains("CREATE TABLE Dengjx_FinalExams13")
                .contains("UNIQUE (djx_CourseId13, djx_AcademicYear13, djx_Semester13)");
    }

    @Test
    void seedDataIncludesZjutTeachingBuildingsAndClassrooms() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(seed)
                .contains("Dengjx_TeachingBuildings13")
                .contains("'健行楼'")
                .contains("'广知楼'")
                .contains("'博易楼'")
                .contains("Dengjx_Classrooms13");
    }

    @Test
    void seedDataIncludesSampleTeachingEvaluationsForCompletedCourses() throws IOException {
        String seed = readSql("06_seed_data.sql");

        assertThat(seed)
                .contains("Dengjx_TeachingEvaluations13")
                .contains("课程讲解清晰")
                .contains("课堂互动充分");
    }

    private String readSql(String fileName) throws IOException {
        return Files.readString(SQL_DIR.resolve(fileName));
    }

    private long countMajorCourseRows(String seed, String majorName) {
        int insertStart = seed.indexOf("Dengjx_MajorCourses13");
        int valuesEnd = seed.indexOf(") AS v (major_name, course_code, course_type)", insertStart);
        String majorCoursesBlock = seed.substring(insertStart, valuesEnd);
        Pattern rowPattern = Pattern.compile("\\(" + Pattern.quote("'" + majorName + "'")
                + "\\s*,\\s*'CS\\d+'\\s*,\\s*'(REQUIRED|ELECTIVE)'\\)");
        Matcher matcher = rowPattern.matcher(majorCoursesBlock);
        long count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private List<String> findTeacherScheduleConflicts(String seed) {
        Map<String, Integer> courseHours = parseCourseHours(seed);
        Map<String, List<String>> classesByMajor = parseClassesByMajor(seed);
        List<SeedAssignment> assignments = parseSeedAssignments(seed, classesByMajor);
        List<String> conflicts = new ArrayList<>();

        for (int i = 0; i < assignments.size(); i++) {
            SeedAssignment left = assignments.get(i);
            for (int j = i + 1; j < assignments.size(); j++) {
                SeedAssignment right = assignments.get(j);
                if (!left.teacherNo().equals(right.teacherNo())) {
                    continue;
                }
                if (hasConflict(
                        effectiveSlots(left, courseHours.get(left.courseCode())),
                        effectiveSlots(right, courseHours.get(right.courseCode())))) {
                    conflicts.add(left.teacherNo() + " " + left.className() + " " + left.courseCode()
                            + " <-> " + right.className() + " " + right.courseCode());
                }
            }
        }
        return conflicts;
    }

    private Map<String, Integer> parseCourseHours(String seed) {
        int insertStart = seed.indexOf("INSERT INTO\n    Dengjx_Courses13");
        int valuesEnd = seed.indexOf("INSERT INTO\n    Dengjx_MajorCourses13", insertStart);
        String block = seed.substring(insertStart, valuesEnd);
        Pattern pattern = Pattern.compile("\\(\\s*'(CS\\d+)'\\s*,\\s*'[^']+'\\s*,\\s*(\\d+)\\s*,", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(block);
        Map<String, Integer> courseHours = new HashMap<>();
        while (matcher.find()) {
            courseHours.put(matcher.group(1), Integer.parseInt(matcher.group(2)));
        }
        return courseHours;
    }

    private Map<String, List<String>> parseClassesByMajor(String seed) {
        int insertStart = seed.indexOf("INSERT INTO\n    Dengjx_Classes13");
        int valuesEnd = seed.indexOf(") AS v (class_name, major_name)", insertStart);
        String block = seed.substring(insertStart, valuesEnd);
        Pattern pattern = Pattern.compile("\\('([^']+)'\\s*,\\s*'([^']+)'\\)");
        Matcher matcher = pattern.matcher(block);
        Map<String, List<String>> classesByMajor = new HashMap<>();
        while (matcher.find()) {
            classesByMajor.computeIfAbsent(matcher.group(2), ignored -> new ArrayList<>()).add(matcher.group(1));
        }
        return classesByMajor;
    }

    private List<SeedAssignment> parseSeedAssignments(String seed, Map<String, List<String>> classesByMajor) {
        int insertStart = seed.indexOf("INSERT INTO\n    Dengjx_TeachingAssignments13");
        int valuesStart = seed.indexOf("FROM (", insertStart);
        int valuesEnd = seed.indexOf(") AS v (", valuesStart);
        String block = seed.substring(valuesStart, valuesEnd);
        List<SeedAssignment> directAssignments = parseDirectSeedAssignments(block);
        if (!directAssignments.isEmpty()) {
            return directAssignments;
        }
        return parseMajorExpandedSeedAssignments(block, classesByMajor);
    }

    private List<SeedAssignment> parseDirectSeedAssignments(String block) {
        Pattern pattern = Pattern.compile("\\('([^']+)'\\s*,\\s*'([^']+)'\\s*,\\s*'(CS\\d+)'\\s*,\\s*'(T\\d+)'\\s*,"
                + "\\s*\\d+\\s*,\\s*(?:TRUE|FALSE)\\s*,\\s*(\\d+)\\s*,\\s*TIME '([^']+)'\\s*,\\s*TIME '([^']+)'\\s*,"
                + "\\s*(NULL|\\d+)\\s*,\\s*(?:NULL|TIME '([^']+)')\\s*,\\s*(?:NULL|TIME '([^']+)')\\)");
        Matcher matcher = pattern.matcher(block);
        List<SeedAssignment> assignments = new ArrayList<>();
        while (matcher.find()) {
            assignments.add(new SeedAssignment(
                    matcher.group(1),
                    matcher.group(3),
                    matcher.group(4),
                    Integer.parseInt(matcher.group(5)),
                    LocalTime.parse(matcher.group(6)),
                    LocalTime.parse(matcher.group(7)),
                    parseNullableInteger(matcher.group(8)),
                    parseNullableTime(matcher.group(9)),
                    parseNullableTime(matcher.group(10))));
        }
        return assignments;
    }

    private List<SeedAssignment> parseMajorExpandedSeedAssignments(String block, Map<String, List<String>> classesByMajor) {
        Pattern pattern = Pattern.compile("\\('([^']+)'\\s*,\\s*'(CS\\d+)'\\s*,\\s*'(T\\d+)'\\s*,\\s*\\d+\\s*,\\s*(?:TRUE|FALSE)\\)");
        Matcher matcher = pattern.matcher(block);
        List<SeedAssignment> assignments = new ArrayList<>();
        while (matcher.find()) {
            String majorName = matcher.group(1);
            String courseCode = matcher.group(2);
            String teacherNo = matcher.group(3);
            for (String className : classesByMajor.getOrDefault(majorName, List.of())) {
                assignments.add(new SeedAssignment(
                        className,
                        courseCode,
                        teacherNo,
                        weekdayOne(courseCode),
                        startTimeOne(courseCode),
                        endTimeOne(courseCode),
                        weekdayTwo(courseCode),
                        startTimeTwo(courseCode),
                        endTimeTwo(courseCode)));
            }
        }
        return assignments;
    }

    private List<EffectiveSlot> effectiveSlots(SeedAssignment assignment, int hours) {
        List<EffectiveSlot> slots = new ArrayList<>();
        slots.add(new EffectiveSlot(
                assignment.weekdayOne(),
                assignment.startTimeOne(),
                assignment.endTimeOne(),
                true,
                hours != 32));
        if (hours >= 48 && assignment.weekdayTwo() != null) {
            slots.add(new EffectiveSlot(
                    assignment.weekdayTwo(),
                    assignment.startTimeTwo(),
                    assignment.endTimeTwo(),
                    true,
                    hours == 64));
        }
        return slots;
    }

    private boolean hasConflict(List<EffectiveSlot> left, List<EffectiveSlot> right) {
        for (EffectiveSlot leftSlot : left) {
            for (EffectiveSlot rightSlot : right) {
                if (leftSlot.overlaps(rightSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer parseNullableInteger(String value) {
        return "NULL".equals(value) ? null : Integer.parseInt(value);
    }

    private LocalTime parseNullableTime(String value) {
        return value == null ? null : LocalTime.parse(value);
    }

    private Integer weekdayOne(String courseCode) {
        return switch (courseCode) {
            case "CS101", "CS102", "CS104", "CS105", "CS114" -> 1;
            case "CS103", "CS106", "CS110", "CS112", "CS116", "CS109" -> 2;
            case "CS113" -> 3;
            case "CS107", "CS108", "CS111" -> 5;
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private LocalTime startTimeOne(String courseCode) {
        return switch (courseCode) {
            case "CS101", "CS109" -> LocalTime.parse("15:25:00");
            case "CS102", "CS103", "CS108" -> LocalTime.parse("08:00:00");
            case "CS106", "CS104", "CS111" -> LocalTime.parse("09:55:00");
            case "CS107", "CS110", "CS105" -> LocalTime.parse("13:30:00");
            case "CS112", "CS113", "CS114", "CS116" -> LocalTime.parse("18:30:00");
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private LocalTime endTimeOne(String courseCode) {
        return switch (courseCode) {
            case "CS101", "CS109" -> LocalTime.parse("17:05:00");
            case "CS102", "CS103", "CS108" -> LocalTime.parse("09:40:00");
            case "CS106", "CS104", "CS111" -> LocalTime.parse("11:35:00");
            case "CS107", "CS110", "CS105" -> LocalTime.parse("15:10:00");
            case "CS112", "CS113", "CS114", "CS116" -> LocalTime.parse("20:10:00");
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private Integer weekdayTwo(String courseCode) {
        return switch (courseCode) {
            case "CS108", "CS111" -> null;
            case "CS101", "CS106", "CS110", "CS109", "CS114" -> 4;
            case "CS102", "CS104", "CS105", "CS103", "CS107" -> 3;
            case "CS112", "CS113", "CS116" -> 5;
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private LocalTime startTimeTwo(String courseCode) {
        return switch (courseCode) {
            case "CS108", "CS111" -> null;
            case "CS101", "CS102", "CS104" -> LocalTime.parse("08:00:00");
            case "CS105", "CS110" -> LocalTime.parse("13:30:00");
            case "CS103", "CS106" -> LocalTime.parse("09:55:00");
            case "CS109", "CS113" -> LocalTime.parse("18:30:00");
            case "CS107", "CS112", "CS114", "CS116" -> LocalTime.parse("15:25:00");
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private LocalTime endTimeTwo(String courseCode) {
        return switch (courseCode) {
            case "CS108", "CS111" -> null;
            case "CS101", "CS102", "CS104" -> LocalTime.parse("09:40:00");
            case "CS105", "CS110" -> LocalTime.parse("15:10:00");
            case "CS103", "CS106" -> LocalTime.parse("11:35:00");
            case "CS109", "CS113" -> LocalTime.parse("20:10:00");
            case "CS107", "CS112", "CS114", "CS116" -> LocalTime.parse("17:05:00");
            default -> throw new IllegalArgumentException("未配置课程时间: " + courseCode);
        };
    }

    private record SeedAssignment(
            String className,
            String courseCode,
            String teacherNo,
            Integer weekdayOne,
            LocalTime startTimeOne,
            LocalTime endTimeOne,
            Integer weekdayTwo,
            LocalTime startTimeTwo,
            LocalTime endTimeTwo) {
    }

    private record EffectiveSlot(
            int weekday,
            LocalTime startTime,
            LocalTime endTime,
            boolean firstHalf,
            boolean secondHalf) {

        private boolean overlaps(EffectiveSlot other) {
            return weekday == other.weekday
                    && ((firstHalf && other.firstHalf) || (secondHalf && other.secondHalf))
                    && startTime.isBefore(other.endTime)
                    && other.startTime.isBefore(endTime);
        }
    }
}
