package com.dengjx.affairs.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<Map<String, Object>> regionCounts();

    List<Map<String, Object>> adminCourseAverages();

    List<Map<String, Object>> studentCreditSummaries();

    List<Map<String, Object>> teacherAssignments();

    List<Map<String, Object>> classCourses();

    List<Map<String, Object>> teacherCourseAverages(Long userId);

    Map<String, Object> teacherSchedule(Long userId);

    List<Map<String, Object>> studentYearScores(Long studentId, String academicYear);

    List<Map<String, Object>> courseRank(Long assignmentId);

    Map<String, Object> studentCredits(Long userId);

    List<Map<String, Object>> studentYearScoresForCurrentUser(Long userId, String academicYear);

    List<Map<String, Object>> teacherCourseRank(Long userId, Long assignmentId);

    List<Map<String, Object>> studentCourseRank(Long userId, Long assignmentId);
}
