package com.dengjx.affairs.dto;

import java.math.BigDecimal;

public record CurrentUserProfileResponse(
        Long userId,
        String username,
        String role,
        String displayName,
        Boolean enabled,
        Long studentId,
        String studentNo,
        String studentName,
        String studentGender,
        Integer studentAge,
        String className,
        String majorName,
        String regionName,
        BigDecimal totalCredits,
        Long teacherId,
        String teacherNo,
        String teacherName,
        String teacherGender,
        Integer teacherAge,
        String teacherTitle,
        String teacherPhone) {
}
