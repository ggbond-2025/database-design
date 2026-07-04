package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.FinalExamRequest;
import com.dengjx.affairs.entity.FinalExam;
import java.util.List;
import java.util.Map;

public interface FinalExamService {

    PageResult<FinalExam> list(String keyword, long page, long size);

    FinalExam getById(Long id);

    FinalExam create(FinalExamRequest request);

    FinalExam update(Long id, FinalExamRequest request);

    void delete(Long id);

    List<Map<String, Object>> studentMine(Long userId);

    List<Map<String, Object>> teacherMine(Long userId);
}
