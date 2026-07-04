package com.dengjx.affairs.service;

import com.dengjx.affairs.dto.LookupOption;
import java.util.List;

public interface LookupService {

    List<LookupOption> studentOptions();

    List<LookupOption> regionOptions();

    List<LookupOption> majorOptions();

    List<LookupOption> teacherOptions();

    List<LookupOption> courseOptions();

    List<LookupOption> majorCourseOptions();

    List<LookupOption> classOptions();

    List<LookupOption> teachingBuildingOptions();

    List<LookupOption> classroomOptions();

    List<LookupOption> assignmentOptions();

    List<LookupOption> activeEnrollmentOptions();
}
