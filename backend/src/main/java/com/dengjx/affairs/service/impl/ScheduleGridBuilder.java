package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.entity.Assignment;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ScheduleGridBuilder {

    private ScheduleGridBuilder() {
    }

    static Map<String, Object> build(List<Map<String, Object>> rows) {
        Map<Integer, List<Map<String, Object>>> firstHalf = emptyWeekdayMap();
        Map<Integer, List<Map<String, Object>>> secondHalf = emptyWeekdayMap();
        for (Map<String, Object> row : rows) {
            Assignment assignment = assignmentFromRow(row);
            int hours = numberValue(row.get("djx_hours13")).intValue();
            for (AssignmentScheduleRules.EffectiveSlot slot : AssignmentScheduleRules.effectiveSlots(assignment, hours)) {
                Map<String, Object> item = new LinkedHashMap<>(row);
                item.put("weekday", slot.weekday());
                item.put("startTime", slot.startTime().toString());
                item.put("endTime", slot.endTime().toString());
                if (slot.firstHalf()) {
                    firstHalf.get(slot.weekday()).add(item);
                }
                if (slot.secondHalf()) {
                    secondHalf.get(slot.weekday()).add(item);
                }
            }
        }
        return Map.of("firstHalf", firstHalf, "secondHalf", secondHalf);
    }

    static Assignment assignmentFromRow(Map<String, Object> row) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(longValue(row.get("djx_assignmentid13")));
        assignment.setWeekdayOne(integerValue(row.get("djx_weekdayone13")));
        assignment.setStartTimeOne(toLocalTime(row.get("djx_starttimeone13")));
        assignment.setEndTimeOne(toLocalTime(row.get("djx_endtimeone13")));
        assignment.setWeekdayTwo(integerValue(row.get("djx_weekdaytwo13")));
        assignment.setStartTimeTwo(toLocalTime(row.get("djx_starttimetwo13")));
        assignment.setEndTimeTwo(toLocalTime(row.get("djx_endtimetwo13")));
        return assignment;
    }

    private static Number numberValue(Object value) {
        if (value instanceof Number number) {
            return number;
        }
        throw new BusinessException("课程学时配置不完整");
    }

    private static Long longValue(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private static Integer integerValue(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private static LocalTime toLocalTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalTime localTime) {
            return localTime;
        }
        if (value instanceof Time time) {
            return time.toLocalTime();
        }
        return LocalTime.parse(String.valueOf(value));
    }

    private static Map<Integer, List<Map<String, Object>>> emptyWeekdayMap() {
        Map<Integer, List<Map<String, Object>>> result = new LinkedHashMap<>();
        for (int weekday = 1; weekday <= 5; weekday++) {
            result.put(weekday, new ArrayList<>());
        }
        return result;
    }
}
