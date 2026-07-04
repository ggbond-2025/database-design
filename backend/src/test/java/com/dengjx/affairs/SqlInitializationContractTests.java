package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dengjx.affairs.entity.Student;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private String readSql(String fileName) throws IOException {
        return Files.readString(SQL_DIR.resolve(fileName));
    }
}
