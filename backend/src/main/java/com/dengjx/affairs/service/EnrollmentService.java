package com.dengjx.affairs.service;

import java.util.List;
import java.util.Map;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.EnrollmentRequest;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.security.AuthenticatedUser;

public interface EnrollmentService {

    List<Map<String, Object>> available(Long userId);

    Enrollment enroll(Long userId, Long assignmentId);

    Enrollment studentDrop(Long userId, Long assignmentId);

    List<Map<String, Object>> mine(Long userId);

    Map<String, Object> schedule(Long userId);

    List<Map<String, Object>> assignmentStudents(AuthenticatedUser user, Long assignmentId);

    default PageResult<Map<String, Object>> adminList(long page, long size) {
        return adminList(null, page, size);
    }

    PageResult<Map<String, Object>> adminList(String keyword, long page, long size);

    Enrollment adminCreate(EnrollmentRequest request);

    Enrollment adminDrop(Long id);
}
