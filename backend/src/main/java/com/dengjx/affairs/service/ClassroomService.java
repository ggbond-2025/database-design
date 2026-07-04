package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.ClassroomRequest;
import com.dengjx.affairs.entity.Classroom;

public interface ClassroomService {

    PageResult<Classroom> list(String keyword, long page, long size);

    Classroom getById(Long id);

    Classroom create(ClassroomRequest request);

    Classroom update(Long id, ClassroomRequest request);

    void delete(Long id);
}
