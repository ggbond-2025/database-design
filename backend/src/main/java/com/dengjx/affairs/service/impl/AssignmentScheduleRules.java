package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.entity.Assignment;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class AssignmentScheduleRules {

    private AssignmentScheduleRules() {
    }

    public static List<EffectiveSlot> effectiveSlots(Assignment assignment, int hours) {
        List<EffectiveSlot> slots = new ArrayList<>();
        addSlot(slots, assignment.getWeekdayOne(), assignment.getStartTimeOne(), assignment.getEndTimeOne(), true, hours != 32);
        if (hours >= 48) {
            addSlot(slots, assignment.getWeekdayTwo(), assignment.getStartTimeTwo(), assignment.getEndTimeTwo(), true, hours == 64);
        }
        return slots;
    }

    public static boolean hasConflict(List<EffectiveSlot> left, List<EffectiveSlot> right) {
        for (EffectiveSlot leftSlot : left) {
            for (EffectiveSlot rightSlot : right) {
                if (leftSlot.overlaps(rightSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addSlot(
            List<EffectiveSlot> slots,
            Integer weekday,
            LocalTime startTime,
            LocalTime endTime,
            boolean firstHalf,
            boolean secondHalf) {
        if (weekday == null || startTime == null || endTime == null) {
            return;
        }
        slots.add(new EffectiveSlot(weekday, startTime, endTime, firstHalf, secondHalf));
    }

    public record EffectiveSlot(
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
