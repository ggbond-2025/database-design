package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dengjx.affairs.entity.Student;
import java.io.IOException;
import java.lang.reflect.Field;
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
                .contains("CREATE TABLE Dengjx_MajorTransferApplications13")
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
}
