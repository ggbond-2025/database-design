package com.dengjx.affairs.service.impl;

import com.dengjx.affairs.mapper.AssignmentMapper;
import com.dengjx.affairs.entity.Assignment;
import com.dengjx.affairs.entity.Enrollment;
import com.dengjx.affairs.entity.Grade;
import com.dengjx.affairs.entity.TeachingEvaluation;
import com.dengjx.affairs.mapper.EnrollmentMapper;
import com.dengjx.affairs.mapper.GradeMapper;
import com.dengjx.affairs.mapper.TeachingEvaluationMapper;
import com.dengjx.affairs.service.AssignmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dengjx.affairs.dto.AssignmentRequest;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final JdbcTemplate jdbcTemplate;
    private final EnrollmentMapper enrollmentMapper;
    private final GradeMapper gradeMapper;
    private final TeachingEvaluationMapper teachingEvaluationMapper;

    @Autowired
    public AssignmentServiceImpl(
            AssignmentMapper assignmentMapper,
            JdbcTemplate jdbcTemplate,
            EnrollmentMapper enrollmentMapper,
            GradeMapper gradeMapper,
            TeachingEvaluationMapper teachingEvaluationMapper) {
        this.assignmentMapper = assignmentMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.enrollmentMapper = enrollmentMapper;
        this.gradeMapper = gradeMapper;
        this.teachingEvaluationMapper = teachingEvaluationMapper;
    }

    public AssignmentServiceImpl(AssignmentMapper assignmentMapper, JdbcTemplate jdbcTemplate) {
        this(assignmentMapper, jdbcTemplate, null, null, null);
    }

    public AssignmentServiceImpl(AssignmentMapper assignmentMapper) {
        this(assignmentMapper, null);
    }

    public PageResult<Assignment> list(String keyword, long page, long size) {
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim();
            String likeKeyword = "%" + trimmedKeyword + "%";
            wrapper.and(query -> query
                    .like(Assignment::getAcademicYear, trimmedKeyword)
                    .or().apply("""
                            djx_majorcourseid13 IN (
                                SELECT mc.djx_majorcourseid13
                                FROM dengjx_majorcourses13 mc
                                JOIN dengjx_majors13 m ON m.djx_majorid13 = mc.djx_majorid13
                                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                                WHERE m.djx_majorname13 LIKE {0}
                                   OR c.djx_coursecode13 LIKE {0}
                                   OR c.djx_coursename13 LIKE {0}
                                   OR mc.djx_coursetype13 LIKE {0}
                            )
                            """, likeKeyword)
                    .or().apply("""
                            djx_classid13 IN (
                                SELECT djx_classid13
                                FROM dengjx_classes13
                                WHERE djx_classname13 LIKE {0}
                            )
                            """, likeKeyword)
                    .or().apply("""
                            djx_teacherid13 IN (
                                SELECT djx_teacherid13
                                FROM dengjx_teachers13
                                WHERE djx_tno13 LIKE {0}
                                   OR djx_tname13 LIKE {0}
                            )
                            """, likeKeyword)
                    .or().apply("""
                            djx_classroomid13 IN (
                                SELECT cr.djx_classroomid13
                                FROM dengjx_classrooms13 cr
                                JOIN dengjx_teachingbuildings13 tb ON tb.djx_buildingid13 = cr.djx_buildingid13
                                WHERE cr.djx_classroomname13 LIKE {0}
                                   OR tb.djx_buildingname13 LIKE {0}
                            )
                            """, likeKeyword));
        }
        wrapper.orderByAsc(Assignment::getAssignmentId);
        Page<Assignment> result = assignmentMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public Assignment getById(Long id) {
        Assignment assignment = assignmentMapper.selectById(id);
        if (assignment == null) {
            throw new BusinessException("开课安排不存在");
        }
        return assignment;
    }

    @Transactional
    public Assignment create(AssignmentRequest request) {
        lockAssignmentsForScheduleCheck();
        validate(request, null);
        Assignment assignment = new Assignment();
        apply(request, assignment);
        assignmentMapper.insert(assignment);
        return assignment;
    }

    @Transactional
    public Assignment update(Long id, AssignmentRequest request) {
        lockAssignmentsForScheduleCheck();
        validate(request, id);
        Assignment assignment = getById(id);
        apply(request, assignment);
        assignmentMapper.updateById(assignment);
        return assignment;
    }

    @Transactional
    public void delete(Long id) {
        if (canCascadeDelete()) {
            List<Long> enrollmentIds = enrollmentIdsByAssignment(id);
            if (!enrollmentIds.isEmpty()) {
                teachingEvaluationMapper.delete(new LambdaQueryWrapper<TeachingEvaluation>()
                        .in(TeachingEvaluation::getEnrollmentId, enrollmentIds));
                gradeMapper.delete(new LambdaQueryWrapper<Grade>()
                        .in(Grade::getEnrollmentId, enrollmentIds));
            }
            enrollmentMapper.delete(new LambdaQueryWrapper<Enrollment>()
                    .eq(Enrollment::getAssignmentId, id));
        }
        if (assignmentMapper.deleteById(id) == 0) {
            throw new BusinessException("开课安排不存在");
        }
    }

    private boolean canCascadeDelete() {
        return enrollmentMapper != null && gradeMapper != null && teachingEvaluationMapper != null;
    }

    private List<Long> enrollmentIdsByAssignment(Long assignmentId) {
        return enrollmentMapper.selectList(new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getAssignmentId, assignmentId))
                .stream()
                .map(Enrollment::getEnrollmentId)
                .toList();
    }

    private void validate(AssignmentRequest request, Long excludeAssignmentId) {
        if (request.semester() == null || (request.semester() != 1 && request.semester() != 2)) {
            throw new BusinessException("学期必须为1或2");
        }
        if (request.capacity() == null || request.capacity() <= 0) {
            throw new BusinessException("课程容量必须大于0");
        }
        validateClassroomCapacity(request.classroomId(), request.capacity());
        validateSlot("第一个上课时间", request.weekdayOne(), request.startTimeOne(), request.endTimeOne());
        validateSlot("第二个上课时间", request.weekdayTwo(), request.startTimeTwo(), request.endTimeTwo());
        Integer hours = courseHours(request.majorCourseId());
        if (hours != null) {
            if (hours >= 48 && !hasSlot(request.weekdayTwo(), request.startTimeTwo(), request.endTimeTwo())) {
                throw new BusinessException(hours + "学时课程需要配置第二个上课时间");
            }
            if (hours == 32 && !hasSlot(request.weekdayOne(), request.startTimeOne(), request.endTimeOne())) {
                throw new BusinessException("32学时课程需要配置第一个上课时间");
            }
            validateScheduleConflict(request, excludeAssignmentId, hours, ScheduleResource.TEACHER);
            validateScheduleConflict(request, excludeAssignmentId, hours, ScheduleResource.CLASS);
            validateScheduleConflict(request, excludeAssignmentId, hours, ScheduleResource.CLASSROOM);
        }
    }

    private void validateScheduleConflict(
            AssignmentRequest request,
            Long excludeAssignmentId,
            int hours,
            ScheduleResource resource) {
        Assignment candidate = new Assignment();
        apply(request, candidate);
        List<AssignmentScheduleRules.EffectiveSlot> candidateSlots =
                AssignmentScheduleRules.effectiveSlots(candidate, hours);
        if (candidateSlots.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<Assignment>()
                .eq(Assignment::getAcademicYear, request.academicYear())
                .eq(Assignment::getSemester, request.semester());
        resource.apply(wrapper, request);
        if (excludeAssignmentId != null) {
            wrapper.ne(Assignment::getAssignmentId, excludeAssignmentId);
        }

        List<Assignment> existingAssignments = assignmentMapper.selectList(wrapper);
        for (Assignment existing : existingAssignments) {
            if (!resource.matches(existing, request)) {
                continue;
            }
            Integer existingHours = courseHours(existing.getMajorCourseId());
            if (existingHours == null) {
                continue;
            }
            if (AssignmentScheduleRules.hasConflict(
                    candidateSlots,
                    AssignmentScheduleRules.effectiveSlots(existing, existingHours))) {
                throw new BusinessException(resource.conflictMessage);
            }
        }
    }

    private void apply(AssignmentRequest request, Assignment assignment) {
        assignment.setMajorCourseId(request.majorCourseId());
        assignment.setClassId(request.classId());
        assignment.setTeacherId(request.teacherId());
        assignment.setClassroomId(request.classroomId());
        assignment.setAcademicYear(request.academicYear());
        assignment.setSemester(request.semester());
        assignment.setCapacity(request.capacity());
        assignment.setEnrollmentOpen(Boolean.TRUE.equals(request.enrollmentOpen()));
        assignment.setWeekdayOne(request.weekdayOne());
        assignment.setStartTimeOne(request.startTimeOne());
        assignment.setEndTimeOne(request.endTimeOne());
        assignment.setWeekdayTwo(request.weekdayTwo());
        assignment.setStartTimeTwo(request.startTimeTwo());
        assignment.setEndTimeTwo(request.endTimeTwo());
    }

    private void validateSlot(String label, Integer weekday, LocalTime startTime, LocalTime endTime) {
        boolean hasAny = weekday != null || startTime != null || endTime != null;
        if (!hasAny) {
            return;
        }
        if (!hasSlot(weekday, startTime, endTime)) {
            throw new BusinessException(label + "必须完整配置星期、开始时间和结束时间");
        }
        if (weekday < 1 || weekday > 5) {
            throw new BusinessException(label + "的星期必须为周一到周五");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(label + "的开始时间必须早于结束时间");
        }
    }

    private boolean hasSlot(Integer weekday, LocalTime startTime, LocalTime endTime) {
        return weekday != null && startTime != null && endTime != null;
    }

    private Integer courseHours(Long majorCourseId) {
        if (jdbcTemplate == null || majorCourseId == null) {
            return null;
        }
        return jdbcTemplate.queryForObject("""
                SELECT c.djx_hours13
                FROM dengjx_majorcourses13 mc
                JOIN dengjx_courses13 c ON c.djx_courseid13 = mc.djx_courseid13
                WHERE mc.djx_majorcourseid13 = ?
                """, Integer.class, majorCourseId);
    }

    private void validateClassroomCapacity(Long classroomId, Integer assignmentCapacity) {
        if (jdbcTemplate == null || classroomId == null || assignmentCapacity == null) {
            return;
        }
        Integer classroomCapacity;
        try {
            classroomCapacity = jdbcTemplate.queryForObject("""
                    SELECT djx_capacity13
                    FROM dengjx_classrooms13
                    WHERE djx_classroomid13 = ?
                    """, Integer.class, classroomId);
        } catch (DataAccessException exception) {
            return;
        }
        if (classroomCapacity != null && assignmentCapacity > classroomCapacity) {
            throw new BusinessException("课程容量不能超过教室容量");
        }
    }

    private void lockAssignmentsForScheduleCheck() {
        if (jdbcTemplate == null || jdbcTemplate.getDataSource() == null) {
            return;
        }
        jdbcTemplate.execute("LOCK TABLE dengjx_teachingassignments13 IN SHARE ROW EXCLUSIVE MODE");
    }

    private enum ScheduleResource {
        TEACHER("教师任课时间冲突：同一学期内该教师已有重叠课程") {
            @Override
            void apply(LambdaQueryWrapper<Assignment> wrapper, AssignmentRequest request) {
                wrapper.eq(Assignment::getTeacherId, request.teacherId());
            }

            @Override
            boolean matches(Assignment assignment, AssignmentRequest request) {
                return request.teacherId().equals(assignment.getTeacherId());
            }
        },
        CLASS("班级上课时间冲突：同一学期内该班级已有重叠课程") {
            @Override
            void apply(LambdaQueryWrapper<Assignment> wrapper, AssignmentRequest request) {
                wrapper.eq(Assignment::getClassId, request.classId());
            }

            @Override
            boolean matches(Assignment assignment, AssignmentRequest request) {
                return request.classId().equals(assignment.getClassId());
            }
        },
        CLASSROOM("教室使用时间冲突：同一学期内该教室已有重叠课程") {
            @Override
            void apply(LambdaQueryWrapper<Assignment> wrapper, AssignmentRequest request) {
                wrapper.eq(Assignment::getClassroomId, request.classroomId());
            }

            @Override
            boolean matches(Assignment assignment, AssignmentRequest request) {
                return request.classroomId().equals(assignment.getClassroomId());
            }
        };

        private final String conflictMessage;

        ScheduleResource(String conflictMessage) {
            this.conflictMessage = conflictMessage;
        }

        abstract void apply(LambdaQueryWrapper<Assignment> wrapper, AssignmentRequest request);

        abstract boolean matches(Assignment assignment, AssignmentRequest request);
    }
}
