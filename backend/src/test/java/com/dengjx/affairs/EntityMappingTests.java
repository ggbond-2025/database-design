package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.entity.Course;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.entity.Grade;
import com.dengjx.affairs.entity.Major;
import com.dengjx.affairs.entity.Region;
import com.dengjx.affairs.entity.SchoolClass;
import com.dengjx.affairs.entity.Student;
import com.dengjx.affairs.entity.Teacher;
import com.dengjx.affairs.entity.UserAccount;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class EntityMappingTests {

    private static final List<Class<?>> ENTITY_CLASSES = List.of(
            Assignment.class,
            Course.class,
            Enrollment.class,
            Grade.class,
            Major.class,
            Region.class,
            SchoolClass.class,
            Student.class,
            Teacher.class,
            UserAccount.class);

    @Test
    void entityTableAndColumnMappingsUseLowercaseDatabaseIdentifiers() {
        for (Class<?> entityClass : ENTITY_CLASSES) {
            TableName tableName = entityClass.getAnnotation(TableName.class);
            assertLowercaseIdentifier(tableName.value(), entityClass.getSimpleName() + " table name");

            for (Field field : entityClass.getDeclaredFields()) {
                TableId tableId = field.getAnnotation(TableId.class);
                if (tableId != null) {
                    assertLowercaseIdentifier(tableId.value(), entityClass.getSimpleName() + "." + field.getName());
                }

                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null) {
                    assertLowercaseIdentifier(tableField.value(), entityClass.getSimpleName() + "." + field.getName());
                }
            }
        }
    }

    private void assertLowercaseIdentifier(String identifier, String mapping) {
        assertThat(identifier)
                .as(mapping)
                .isEqualTo(identifier.toLowerCase(Locale.ROOT));
    }
}
