package com.dengjx.affairs.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dengjx.affairs.common.BusinessException;
import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.MajorTransferReviewRequest;
import com.dengjx.affairs.dto.MajorTransferSubmitRequest;
import com.dengjx.affairs.entity.MajorTransferApplication;
import com.dengjx.affairs.mapper.MajorTransferApplicationMapper;
import com.dengjx.affairs.mapper.StudentMapper;
import com.dengjx.affairs.security.UserContextService;
import com.dengjx.affairs.service.MajorTransferApplicationService;
import com.dengjx.affairs.service.MajorTransferSettingService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MajorTransferApplicationServiceImpl implements MajorTransferApplicationService {

    private static final List<String> REVIEW_STATUSES = List.of("APPROVED", "REJECTED");

    private final MajorTransferApplicationMapper applicationMapper;
    private final StudentMapper studentMapper;
    private final UserContextService userContextService;
    private final JdbcTemplate jdbcTemplate;
    private final AcademicTermService academicTermService;
    private final MajorTransferSettingService settingService;

    @Autowired
    public MajorTransferApplicationServiceImpl(
            MajorTransferApplicationMapper applicationMapper,
            StudentMapper studentMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate,
            AcademicTermService academicTermService,
            MajorTransferSettingService settingService) {
        this.applicationMapper = applicationMapper;
        this.studentMapper = studentMapper;
        this.userContextService = userContextService;
        this.jdbcTemplate = jdbcTemplate;
        this.academicTermService = academicTermService;
        this.settingService = settingService;
    }

    public MajorTransferApplicationServiceImpl(
            MajorTransferApplicationMapper applicationMapper,
            StudentMapper studentMapper,
            UserContextService userContextService,
            JdbcTemplate jdbcTemplate) {
        this(applicationMapper, studentMapper, userContextService, jdbcTemplate, new AcademicTermService(), null);
    }

    public PageResult<Map<String, Object>> adminList(String keyword, long page, long size) {
        long offset = (page - 1) * size;
        String likeKeyword = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT app.djx_applicationid13 AS "applicationId",
                       app.djx_status13 AS "status",
                       app.djx_reason13 AS "reason",
                       app.djx_reviewcomment13 AS "reviewComment",
                       app.djx_effectiveacademicyear13 AS "effectiveAcademicYear",
                       app.djx_effectivesemester13 AS "effectiveSemester",
                       app.djx_appliedat13 AS "appliedAt",
                       app.djx_reviewedat13 AS "reviewedAt",
                       app.djx_targetmajorid13 AS "targetMajorId",
                       s.djx_sno13 AS "sno",
                       s.djx_sname13 AS "sname",
                       fm.djx_majorname13 AS "fromMajorName",
                       tm.djx_majorname13 AS "targetMajorName",
                       tc.djx_classname13 AS "targetClassName"
                FROM dengjx_majortransferapplications13 app
                JOIN dengjx_students13 s ON s.djx_studentid13 = app.djx_studentid13
                JOIN dengjx_majors13 fm ON fm.djx_majorid13 = app.djx_frommajorid13
                JOIN dengjx_majors13 tm ON tm.djx_majorid13 = app.djx_targetmajorid13
                LEFT JOIN dengjx_classes13 tc ON tc.djx_classid13 = app.djx_targetclassid13
                WHERE ? = '%%'
                   OR s.djx_sno13 LIKE ?
                   OR s.djx_sname13 LIKE ?
                   OR tm.djx_majorname13 LIKE ?
                ORDER BY app.djx_appliedat13 DESC
                LIMIT ? OFFSET ?
                """, likeKeyword, likeKeyword, likeKeyword, likeKeyword, size, offset);
        Long total = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM dengjx_majortransferapplications13 app
                JOIN dengjx_students13 s ON s.djx_studentid13 = app.djx_studentid13
                JOIN dengjx_majors13 tm ON tm.djx_majorid13 = app.djx_targetmajorid13
                WHERE ? = '%%'
                   OR s.djx_sno13 LIKE ?
                   OR s.djx_sname13 LIKE ?
                   OR tm.djx_majorname13 LIKE ?
                """, Long.class, likeKeyword, likeKeyword, likeKeyword, likeKeyword);
        return PageResult.of(rows, total == null ? 0 : total, page, size);
    }

    public List<Map<String, Object>> studentMine(Long userId) {
        Long studentId = userContextService.getStudentId(userId);
        return jdbcTemplate.queryForList("""
                SELECT app.djx_applicationid13 AS "applicationId",
                       app.djx_status13 AS "status",
                       app.djx_reason13 AS "reason",
                       app.djx_reviewcomment13 AS "reviewComment",
                       app.djx_effectiveacademicyear13 AS "effectiveAcademicYear",
                       app.djx_effectivesemester13 AS "effectiveSemester",
                       app.djx_appliedat13 AS "appliedAt",
                       app.djx_reviewedat13 AS "reviewedAt",
                       fm.djx_majorname13 AS "fromMajorName",
                       tm.djx_majorname13 AS "targetMajorName",
                       tc.djx_classname13 AS "targetClassName"
                FROM dengjx_majortransferapplications13 app
                JOIN dengjx_majors13 fm ON fm.djx_majorid13 = app.djx_frommajorid13
                JOIN dengjx_majors13 tm ON tm.djx_majorid13 = app.djx_targetmajorid13
                LEFT JOIN dengjx_classes13 tc ON tc.djx_classid13 = app.djx_targetclassid13
                WHERE app.djx_studentid13 = ?
                ORDER BY app.djx_appliedat13 DESC
                """, studentId);
    }

    @Transactional
    public MajorTransferApplication submit(Long userId, MajorTransferSubmitRequest request) {
        ensureMajorTransferOpen();
        Long studentId = userContextService.getStudentId(userId);
        Map<String, Object> current = currentStudentMajor(studentId);
        Long currentMajorId = longValue(current.get("majorid"));
        if (currentMajorId.equals(request.targetMajorId())) {
            throw new BusinessException("不能申请转入当前专业");
        }
        Long pending = applicationMapper.selectCount(Wrappers.<MajorTransferApplication>lambdaQuery()
                .eq(MajorTransferApplication::getStudentId, studentId)
                .eq(MajorTransferApplication::getStatus, "PENDING"));
        if (pending > 0) {
            throw new BusinessException("已有待审核转专业申请");
        }
        MajorTransferApplication application = new MajorTransferApplication();
        application.setStudentId(studentId);
        application.setFromMajorId(currentMajorId);
        application.setFromClassId(longValue(current.get("classid")));
        application.setTargetMajorId(request.targetMajorId());
        application.setReason(request.reason());
        application.setStatus("PENDING");
        application.setAppliedAt(LocalDateTime.now());
        applicationMapper.insert(application);
        return application;
    }

    @Transactional
    public MajorTransferApplication review(Long adminUserId, Long applicationId, MajorTransferReviewRequest request) {
        if (!REVIEW_STATUSES.contains(request.status())) {
            throw new BusinessException("审核状态必须为APPROVED或REJECTED");
        }
        MajorTransferApplication application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("转专业申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("只能审核待处理申请");
        }
        if ("APPROVED".equals(request.status())) {
            approve(application, request.targetClassId());
        } else {
            application.setTargetClassId(null);
        }
        application.setStatus(request.status());
        application.setReviewComment(request.reviewComment());
        application.setReviewedAt(LocalDateTime.now());
        applicationMapper.updateById(application);
        return application;
    }

    private void approve(MajorTransferApplication application, Long targetClassId) {
        if (targetClassId == null) {
            throw new BusinessException("审核通过时必须选择转入班级");
        }
        Long targetMajorId = classMajorId(targetClassId);
        if (!application.getTargetMajorId().equals(targetMajorId)) {
            throw new BusinessException("转入班级不属于目标专业");
        }
        if (application.getFromClassId() == null) {
            application.setFromClassId(longValue(currentStudentMajor(application.getStudentId()).get("classid")));
        }
        AcademicTermService.AcademicTerm currentTerm = currentStudentTerm(application.getStudentId());
        AcademicTermService.AcademicTerm effectiveTerm = nextTerm(currentTerm);
        application.setTargetClassId(targetClassId);
        application.setEffectiveAcademicYear(effectiveTerm.academicYear());
        application.setEffectiveSemester(effectiveTerm.semester());
    }

    private Map<String, Object> currentStudentMajor(Long studentId) {
        return jdbcTemplate.queryForMap("""
                SELECT s.djx_studentid13 AS studentid,
                       s.djx_classid13 AS classid,
                       cl.djx_majorid13 AS majorid
                       , s.djx_admissiondate13 AS admissiondate
                FROM dengjx_students13 s
                JOIN dengjx_classes13 cl ON cl.djx_classid13 = s.djx_classid13
                WHERE s.djx_studentid13 = ?
                """, studentId);
    }

    private AcademicTermService.AcademicTerm currentStudentTerm(Long studentId) {
        Map<String, Object> current = currentStudentMajor(studentId);
        return academicTermService.current(toLocalDate(current.get("admissiondate")));
    }

    private AcademicTermService.AcademicTerm nextTerm(AcademicTermService.AcademicTerm currentTerm) {
        if (currentTerm.semester() == 1) {
            return new AcademicTermService.AcademicTerm(
                    currentTerm.grade(),
                    2,
                    currentTerm.academicYear(),
                    currentTerm.label());
        }
        int startYear = Integer.parseInt(currentTerm.academicYear().substring(0, 4)) + 1;
        return new AcademicTermService.AcademicTerm(
                currentTerm.grade() + 1,
                1,
                startYear + "-" + (startYear + 1),
                currentTerm.label());
    }

    private void ensureMajorTransferOpen() {
        if (settingService != null && !settingService.isEnabled()) {
            throw new BusinessException("当前转专业申请未开放");
        }
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            throw new BusinessException("学生入学时间配置不完整");
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (StringUtils.hasText(String.valueOf(value))) {
            return LocalDate.parse(String.valueOf(value));
        }
        throw new BusinessException("学生入学时间配置不完整");
    }

    private Long classMajorId(Long classId) {
        Map<String, Object> row = jdbcTemplate.queryForMap("""
                SELECT djx_majorid13 AS majorid
                FROM dengjx_classes13
                WHERE djx_classid13 = ?
                """, classId);
        return longValue(row.get("majorid"));
    }

    private Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (StringUtils.hasText(String.valueOf(value))) {
            return Long.valueOf(String.valueOf(value));
        }
        throw new BusinessException("专业或班级数据不完整");
    }
}
