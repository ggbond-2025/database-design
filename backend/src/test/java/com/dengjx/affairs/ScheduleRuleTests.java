package com.dengjx.affairs;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.service.impl.AssignmentScheduleRules;
import com.dengjx.affairs.service.impl.AssignmentScheduleRules.EffectiveSlot;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScheduleRuleTests {

    @Test
    void assignmentMapsWeeklyTimeColumns() throws NoSuchFieldException {
        assertColumn("weekdayOne", "djx_weekdayone13");
        assertColumn("startTimeOne", "djx_starttimeone13");
        assertColumn("endTimeOne", "djx_endtimeone13");
        assertColumn("weekdayTwo", "djx_weekdaytwo13");
        assertColumn("startTimeTwo", "djx_starttimetwo13");
        assertColumn("endTimeTwo", "djx_endtimetwo13");
        assertColumn("classroomId", "djx_classroomid13");
    }

    @Test
    void sixtyFourHourCourseUsesBothSlotsForBothHalves() {
        Assignment assignment = assignment(1, "08:00", "09:40", 3, "10:00", "11:40");

        List<EffectiveSlot> slots = AssignmentScheduleRules.effectiveSlots(assignment, 64);

        assertThat(slots)
                .extracting(EffectiveSlot::weekday, EffectiveSlot::firstHalf, EffectiveSlot::secondHalf)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1, true, true),
                        org.assertj.core.groups.Tuple.tuple(3, true, true));
    }

    @Test
    void fortyEightHourCourseUsesSecondSlotOnlyInFirstHalf() {
        Assignment assignment = assignment(2, "08:00", "09:40", 4, "14:00", "15:40");

        List<EffectiveSlot> slots = AssignmentScheduleRules.effectiveSlots(assignment, 48);

        assertThat(slots)
                .extracting(EffectiveSlot::weekday, EffectiveSlot::firstHalf, EffectiveSlot::secondHalf)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(2, true, true),
                        org.assertj.core.groups.Tuple.tuple(4, true, false));
    }

    @Test
    void thirtyTwoHourCourseUsesFirstSlotOnlyInFirstHalf() {
        Assignment assignment = assignment(5, "10:00", "11:40", null, null, null);

        List<EffectiveSlot> slots = AssignmentScheduleRules.effectiveSlots(assignment, 32);

        assertThat(slots)
                .extracting(EffectiveSlot::weekday, EffectiveSlot::firstHalf, EffectiveSlot::secondHalf)
                .containsExactly(org.assertj.core.groups.Tuple.tuple(5, true, false));
    }

    @Test
    void detectsConflictOnlyWhenTimeAndEffectiveHalfOverlap() {
        Assignment fullTerm = assignment(1, "08:00", "09:40", null, null, null);
        Assignment firstHalfOnly = assignment(1, "09:00", "10:40", null, null, null);
        Assignment secondHalfOnly = assignment(1, "09:00", "10:40", null, null, null);

        List<EffectiveSlot> existing = AssignmentScheduleRules.effectiveSlots(fullTerm, 64);
        List<EffectiveSlot> firstHalfSlots = AssignmentScheduleRules.effectiveSlots(firstHalfOnly, 32);
        List<EffectiveSlot> secondHalfSlots = List.of(new EffectiveSlot(1, LocalTime.of(9, 0), LocalTime.of(10, 40), false, true));

        assertThat(AssignmentScheduleRules.hasConflict(existing, firstHalfSlots)).isTrue();
        assertThat(AssignmentScheduleRules.hasConflict(firstHalfSlots, secondHalfSlots)).isFalse();
    }

    private static void assertColumn(String fieldName, String columnName) throws NoSuchFieldException {
        Field field = Assignment.class.getDeclaredField(fieldName);
        TableField tableField = field.getAnnotation(TableField.class);

        assertThat(tableField).isNotNull();
        assertThat(tableField.value()).isEqualTo(columnName);
    }

    private static Assignment assignment(
            Integer weekdayOne,
            String startTimeOne,
            String endTimeOne,
            Integer weekdayTwo,
            String startTimeTwo,
            String endTimeTwo) {
        Assignment assignment = new Assignment();
        assignment.setWeekdayOne(weekdayOne);
        assignment.setStartTimeOne(parse(startTimeOne));
        assignment.setEndTimeOne(parse(endTimeOne));
        assignment.setWeekdayTwo(weekdayTwo);
        assignment.setStartTimeTwo(parse(startTimeTwo));
        assignment.setEndTimeTwo(parse(endTimeTwo));
        return assignment;
    }

    private static LocalTime parse(String value) {
        return value == null ? null : LocalTime.parse(value);
    }
}
