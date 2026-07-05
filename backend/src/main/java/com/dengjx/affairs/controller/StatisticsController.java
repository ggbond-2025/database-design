package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.StatisticsService;
import java.util.List;
import java.util.Map;

import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/api/admin/statistics/regions")
    public ApiResponse<List<Map<String, Object>>> regionCounts() {
        return ApiResponse.ok(statisticsService.regionCounts());
    }

    @GetMapping("/api/admin/statistics/course-averages")
    public ApiResponse<List<Map<String, Object>>> adminCourseAverages() {
        return ApiResponse.ok(statisticsService.adminCourseAverages());
    }

    @GetMapping("/api/admin/statistics/student-credits")
    public ApiResponse<List<Map<String, Object>>> studentCreditSummaries() {
        return ApiResponse.ok(statisticsService.studentCreditSummaries());
    }

    @GetMapping("/api/admin/statistics/teacher-assignments")
    public ApiResponse<List<Map<String, Object>>> teacherAssignments() {
        return ApiResponse.ok(statisticsService.teacherAssignments());
    }

    @GetMapping("/api/admin/statistics/class-courses")
    public ApiResponse<List<Map<String, Object>>> classCourses() {
        return ApiResponse.ok(statisticsService.classCourses());
    }

    @GetMapping("/api/admin/statistics/student-year-scores")
    public ApiResponse<List<Map<String, Object>>> adminStudentYearScores(
            @RequestParam Long studentId,
            @RequestParam String academicYear) {
        return ApiResponse.ok(statisticsService.studentYearScores(studentId, academicYear));
    }

    @GetMapping("/api/admin/statistics/course-rank")
    public ApiResponse<List<Map<String, Object>>> adminCourseRank(@RequestParam Long assignmentId) {
        return ApiResponse.ok(statisticsService.courseRank(assignmentId));
    }

    @GetMapping("/api/teacher/statistics/course-averages")
    public ApiResponse<List<Map<String, Object>>> teacherCourseAverages(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(statisticsService.teacherCourseAverages(user.userId()));
    }

    @GetMapping("/api/teacher/schedule")
    public ApiResponse<Map<String, Object>> teacherSchedule(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(statisticsService.teacherSchedule(user.userId()));
    }

    @GetMapping("/api/teacher/statistics/course-rank")
    public ApiResponse<List<Map<String, Object>>> teacherCourseRank(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Long assignmentId) {
        return ApiResponse.ok(statisticsService.teacherCourseRank(user.userId(), assignmentId));
    }

    @GetMapping("/api/student/statistics/credits")
    public ApiResponse<Map<String, Object>> studentCredits(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(statisticsService.studentCredits(user.userId()));
    }

    @GetMapping("/api/student/statistics/year-scores")
    public ApiResponse<List<Map<String, Object>>> studentYearScores(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam String academicYear) {
        return ApiResponse.ok(statisticsService.studentYearScoresForCurrentUser(user.userId(), academicYear));
    }

    @GetMapping("/api/student/statistics/rank")
    public ApiResponse<List<Map<String, Object>>> studentRank(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam Long assignmentId) {
        return ApiResponse.ok(statisticsService.studentCourseRank(user.userId(), assignmentId));
    }
}
