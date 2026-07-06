DROP DATABASE IF EXISTS DengjxMIS13;

CREATE DATABASE DengjxMIS13;

DROP VIEW IF EXISTS V_Dengjx_StudentCreditSummary13;

DROP VIEW IF EXISTS V_Dengjx_RegionStudentCount13;

DROP VIEW IF EXISTS V_Dengjx_CourseAverage13;

DROP VIEW IF EXISTS V_Dengjx_ClassCourses13;

DROP VIEW IF EXISTS V_Dengjx_TeacherAssignments13;

DROP VIEW IF EXISTS V_Dengjx_StudentGrades13;

DROP VIEW IF EXISTS V_Dengjx_TeachingEvaluations13;

DROP TABLE IF EXISTS Dengjx_Users13;

DROP TABLE IF EXISTS Dengjx_TeachingEvaluations13;

DROP TABLE IF EXISTS Dengjx_Grades13;

DROP TABLE IF EXISTS Dengjx_Enrollments13;

DROP TABLE IF EXISTS Dengjx_MajorTransferApplications13;

DROP TABLE IF EXISTS Dengjx_MajorTransferSettings13;

DROP TABLE IF EXISTS Dengjx_TeachingAssignments13;

DROP TABLE IF EXISTS Dengjx_FinalExams13;

DROP TABLE IF EXISTS Dengjx_EnrollmentSettings13;

DROP TABLE IF EXISTS Dengjx_MajorCourses13;

DROP TABLE IF EXISTS Dengjx_Courses13;

DROP TABLE IF EXISTS Dengjx_Teachers13;

DROP TABLE IF EXISTS Dengjx_Students13;

DROP TABLE IF EXISTS Dengjx_Classes13;

DROP TABLE IF EXISTS Dengjx_Classrooms13;

DROP TABLE IF EXISTS Dengjx_TeachingBuildings13;

DROP TABLE IF EXISTS Dengjx_Majors13;

DROP TABLE IF EXISTS Dengjx_Regions13;

-- 地区信息表，保存学生生源地区基础数据。
CREATE TABLE Dengjx_Regions13 (
    -- 地区主键编号。
    djx_RegionId13 BIGSERIAL PRIMARY KEY,
    -- 地区名称。
    djx_RegionName13 VARCHAR(100) NOT NULL UNIQUE
);

-- 专业信息表，保存专业名称和毕业学分要求。
CREATE TABLE Dengjx_Majors13 (
    -- 专业主键编号。
    djx_MajorId13 BIGSERIAL PRIMARY KEY,
    -- 专业名称。
    djx_MajorName13 VARCHAR(100) NOT NULL UNIQUE,
    -- 毕业所需总学分。
    djx_GraduationCredits13 NUMERIC(6, 2) NOT NULL CHECK (djx_GraduationCredits13 > 0)
);

-- 班级信息表，保存班级所属专业和入学年级。
CREATE TABLE Dengjx_Classes13 (
    -- 班级主键编号。
    djx_ClassId13 BIGSERIAL PRIMARY KEY,
    -- 班级名称。
    djx_ClassName13 VARCHAR(100) NOT NULL UNIQUE,
    -- 所属专业编号。
    djx_MajorId13 BIGINT NOT NULL REFERENCES Dengjx_Majors13 (djx_MajorId13),
    -- 入学年级。
    djx_GradeYear13 INTEGER NOT NULL
);

-- 教学楼表，保存校内可排课教学楼。
CREATE TABLE Dengjx_TeachingBuildings13 (
    -- 教学楼主键编号。
    djx_BuildingId13 BIGSERIAL PRIMARY KEY,
    -- 教学楼名称。
    djx_BuildingName13 VARCHAR(100) NOT NULL UNIQUE
);

-- 教室表，保存教学楼下的具体教室。
CREATE TABLE Dengjx_Classrooms13 (
    -- 教室主键编号。
    djx_ClassroomId13 BIGSERIAL PRIMARY KEY,
    -- 所属教学楼编号。
    djx_BuildingId13 BIGINT NOT NULL REFERENCES Dengjx_TeachingBuildings13 (djx_BuildingId13),
    -- 教室名称。
    djx_ClassroomName13 VARCHAR(100) NOT NULL,
    -- 教室容量。
    djx_Capacity13 INTEGER NOT NULL CHECK (djx_Capacity13 > 0),
    UNIQUE (
        djx_BuildingId13,
        djx_ClassroomName13
    )
);

-- 学生信息表，保存学生基本信息、班级和生源地区。
CREATE TABLE Dengjx_Students13 (
    -- 学生主键编号。
    djx_StudentId13 BIGSERIAL PRIMARY KEY,
    -- 学生学号。
    djx_Sno13 VARCHAR(32) NOT NULL UNIQUE,
    -- 学生姓名。
    djx_Sname13 VARCHAR(100) NOT NULL,
    -- 学生性别，取值为MALE或FEMALE。
    djx_Gender13 VARCHAR(10) NOT NULL CHECK (
        djx_Gender13 IN ('MALE', 'FEMALE')
    ),
    -- 学生年龄。
    djx_Age13 INTEGER NOT NULL CHECK (djx_Age13 BETWEEN 15 AND 35),
    -- 所属班级编号。
    djx_ClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13 (djx_ClassId13),
    -- 生源地区编号。
    djx_RegionId13 BIGINT NOT NULL REFERENCES Dengjx_Regions13 (djx_RegionId13),
    -- 入学日期。
    djx_AdmissionDate13 DATE NOT NULL,
    -- 已修学分缓存，由成绩触发器按成绩和课程学分自动维护。
    djx_TotalCredits13 NUMERIC(8, 2) NOT NULL DEFAULT 0 CHECK (djx_TotalCredits13 >= 0)
);

-- 教师信息表，保存教师基本信息、职称和联系电话。
CREATE TABLE Dengjx_Teachers13 (
    -- 教师主键编号。
    djx_TeacherId13 BIGSERIAL PRIMARY KEY,
    -- 教师工号。
    djx_Tno13 VARCHAR(32) NOT NULL UNIQUE,
    -- 教师姓名。
    djx_Tname13 VARCHAR(100) NOT NULL,
    -- 教师性别，取值为MALE或FEMALE。
    djx_Gender13 VARCHAR(10) NOT NULL CHECK (
        djx_Gender13 IN ('MALE', 'FEMALE')
    ),
    -- 教师年龄。
    djx_Age13 INTEGER NOT NULL CHECK (djx_Age13 BETWEEN 20 AND 75),
    -- 教师职称。
    djx_Title13 VARCHAR(100) NOT NULL,
    -- 教师联系电话。
    djx_Phone13 VARCHAR(30) NOT NULL
);

-- 课程基础信息表，保存课程编码、课程名称、学时、考核方式和学分。
CREATE TABLE Dengjx_Courses13 (
    -- 课程主键编号。
    djx_CourseId13 BIGSERIAL PRIMARY KEY,
    -- 课程编码。
    djx_CourseCode13 VARCHAR(32) NOT NULL UNIQUE,
    -- 课程名称。
    djx_CourseName13 VARCHAR(120) NOT NULL,
    -- 课程总学时。
    djx_Hours13 INTEGER NOT NULL CHECK (djx_Hours13 > 0),
    -- 考核方式，EXAM表示考试，CHECK表示考查。
    djx_AssessmentType13 VARCHAR(10) NOT NULL CHECK (
        djx_AssessmentType13 IN ('EXAM', 'CHECK')
    ),
    -- 课程学分。
    djx_Credit13 NUMERIC(4, 1) NOT NULL CHECK (djx_Credit13 > 0)
);

-- 期末考试表，按课程、学年和学期统一维护考试时间。
CREATE TABLE Dengjx_FinalExams13 (
    -- 期末考试主键编号。
    djx_ExamId13 BIGSERIAL PRIMARY KEY,
    -- 课程编号。
    djx_CourseId13 BIGINT NOT NULL REFERENCES Dengjx_Courses13 (djx_CourseId13),
    -- 学年。
    djx_AcademicYear13 VARCHAR(20) NOT NULL,
    -- 学期，取值为1或2。
    djx_Semester13 INTEGER NOT NULL CHECK (djx_Semester13 IN (1, 2)),
    -- 考试开始时间。
    djx_ExamTime13 TIMESTAMP NOT NULL,
    UNIQUE (
        djx_CourseId13,
        djx_AcademicYear13,
        djx_Semester13
    )
);

-- 专业课程计划表，保存专业与课程的培养方案关系。
CREATE TABLE Dengjx_MajorCourses13 (
    -- 专业课程计划主键编号。
    djx_MajorCourseId13 BIGSERIAL PRIMARY KEY,
    -- 所属专业编号。
    djx_MajorId13 BIGINT NOT NULL REFERENCES Dengjx_Majors13 (djx_MajorId13),
    -- 课程编号。
    djx_CourseId13 BIGINT NOT NULL REFERENCES Dengjx_Courses13 (djx_CourseId13),
    -- 课程类型，REQUIRED表示必修，ELECTIVE表示选修。
    djx_CourseType13 VARCHAR(20) NOT NULL CHECK (
        djx_CourseType13 IN ('REQUIRED', 'ELECTIVE')
    ),
    -- 建议修读年级。
    djx_TargetGrade13 INTEGER NOT NULL CHECK (
        djx_TargetGrade13 BETWEEN 1 AND 4
    ),
    -- 建议修读学期，取值为1或2。
    djx_TargetSemester13 INTEGER NOT NULL CHECK (
        djx_TargetSemester13 IN (1, 2)
    ),
    UNIQUE (djx_MajorId13, djx_CourseId13)
);

-- 选课设置表，保存系统全局选课开关状态。
CREATE TABLE Dengjx_EnrollmentSettings13 (
    -- 选课设置主键，固定为1。
    djx_SettingId13 INTEGER PRIMARY KEY DEFAULT 1 CHECK (djx_SettingId13 = 1),
    -- 是否启用选课功能。
    djx_Enabled13 BOOLEAN NOT NULL DEFAULT TRUE,
    -- 选课设置最后更新时间。
    djx_UpdatedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 转专业申请设置表，保存系统全局转专业申请开关状态。
CREATE TABLE Dengjx_MajorTransferSettings13 (
    -- 转专业申请设置主键，固定为1。
    djx_SettingId13 INTEGER PRIMARY KEY DEFAULT 1 CHECK (djx_SettingId13 = 1),
    -- 是否启用学生转专业申请功能。
    djx_Enabled13 BOOLEAN NOT NULL DEFAULT TRUE,
    -- 转专业申请设置最后更新时间。
    djx_UpdatedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 教学任务表，保存专业课程计划、班级、教师、学年学期和选课容量配置。
CREATE TABLE Dengjx_TeachingAssignments13 (
    -- 教学任务主键编号。
    djx_AssignmentId13 BIGSERIAL PRIMARY KEY,
    -- 专业课程计划编号。
    djx_MajorCourseId13 BIGINT NOT NULL REFERENCES Dengjx_MajorCourses13 (djx_MajorCourseId13),
    -- 授课班级编号。
    djx_ClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13 (djx_ClassId13),
    -- 授课教师编号。
    djx_TeacherId13 BIGINT NOT NULL REFERENCES Dengjx_Teachers13 (djx_TeacherId13),
    -- 上课教室编号。
    djx_ClassroomId13 BIGINT NOT NULL REFERENCES Dengjx_Classrooms13 (djx_ClassroomId13),
    -- 学年。
    djx_AcademicYear13 VARCHAR(20) NOT NULL,
    -- 学期，取值为1或2。
    djx_Semester13 INTEGER NOT NULL CHECK (djx_Semester13 IN (1, 2)),
    -- 选课容量。
    djx_Capacity13 INTEGER NOT NULL CHECK (djx_Capacity13 > 0),
    -- 该教学任务是否开放选课。
    djx_EnrollmentOpen13 BOOLEAN NOT NULL DEFAULT FALSE,
    -- 第一个每周上课日，1到5表示周一到周五。
    djx_WeekdayOne13 INTEGER NULL CHECK (
        djx_WeekdayOne13 BETWEEN 1 AND 5
    ),
    -- 第一个每周上课开始时间。
    djx_StartTimeOne13 TIME NULL,
    -- 第一个每周上课结束时间。
    djx_EndTimeOne13 TIME NULL,
    -- 第二个每周上课日，1到5表示周一到周五。
    djx_WeekdayTwo13 INTEGER NULL CHECK (
        djx_WeekdayTwo13 BETWEEN 1 AND 5
    ),
    -- 第二个每周上课开始时间。
    djx_StartTimeTwo13 TIME NULL,
    -- 第二个每周上课结束时间。
    djx_EndTimeTwo13 TIME NULL,
    CONSTRAINT chk_djx_assignment_time_one13 CHECK (
        (
            djx_WeekdayOne13 IS NULL
            AND djx_StartTimeOne13 IS NULL
            AND djx_EndTimeOne13 IS NULL
        )
        OR (
            djx_WeekdayOne13 IS NOT NULL
            AND djx_StartTimeOne13 IS NOT NULL
            AND djx_EndTimeOne13 IS NOT NULL
            AND djx_StartTimeOne13 < djx_EndTimeOne13
        )
    ),
    CONSTRAINT chk_djx_assignment_time_two13 CHECK (
        (
            djx_WeekdayTwo13 IS NULL
            AND djx_StartTimeTwo13 IS NULL
            AND djx_EndTimeTwo13 IS NULL
        )
        OR (
            djx_WeekdayTwo13 IS NOT NULL
            AND djx_StartTimeTwo13 IS NOT NULL
            AND djx_EndTimeTwo13 IS NOT NULL
            AND djx_StartTimeTwo13 < djx_EndTimeTwo13
        )
    )
);

-- 转专业申请表，保存学生提交和管理员审核结果。
CREATE TABLE Dengjx_MajorTransferApplications13 (
    -- 转专业申请主键编号。
    djx_ApplicationId13 BIGSERIAL PRIMARY KEY,
    -- 申请学生编号。
    djx_StudentId13 BIGINT NOT NULL REFERENCES Dengjx_Students13 (djx_StudentId13),
    -- 原专业编号。
    djx_FromMajorId13 BIGINT NOT NULL REFERENCES Dengjx_Majors13 (djx_MajorId13),
    -- 原班级编号，用于审核通过后当前学期继续按原专业班级修读。
    djx_FromClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13 (djx_ClassId13),
    -- 目标专业编号。
    djx_TargetMajorId13 BIGINT NOT NULL REFERENCES Dengjx_Majors13 (djx_MajorId13),
    -- 审核通过后转入的班级，未通过或待审核时为空。
    djx_TargetClassId13 BIGINT NULL REFERENCES Dengjx_Classes13 (djx_ClassId13),
    -- 申请理由。
    djx_Reason13 VARCHAR(500) NULL,
    -- 审核状态，PENDING待审核，APPROVED通过，REJECTED驳回。
    djx_Status13 VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (
        djx_Status13 IN (
            'PENDING',
            'APPROVED',
            'REJECTED'
        )
    ),
    -- 审核意见。
    djx_ReviewComment13 VARCHAR(500) NULL,
    -- 审核通过后开始按目标专业生效的学年。
    djx_EffectiveAcademicYear13 VARCHAR(20) NULL,
    -- 审核通过后开始按目标专业生效的学期。
    djx_EffectiveSemester13 INTEGER NULL CHECK (
        djx_EffectiveSemester13 IN (1, 2)
    ),
    -- 申请提交时间。
    djx_AppliedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 审核时间。
    djx_ReviewedAt13 TIMESTAMP NULL
);

-- 选课记录表，保存学生选择教学任务的状态和时间。
CREATE TABLE Dengjx_Enrollments13 (
    -- 选课记录主键编号。
    djx_EnrollmentId13 BIGSERIAL PRIMARY KEY,
    -- 选课学生编号。
    djx_StudentId13 BIGINT NOT NULL REFERENCES Dengjx_Students13 (djx_StudentId13),
    -- 对应教学任务编号。
    djx_AssignmentId13 BIGINT NOT NULL REFERENCES Dengjx_TeachingAssignments13 (djx_AssignmentId13),
    -- 选课状态，SELECTED表示已选，DROPPED表示已退选，COMPLETED表示已完成。
    djx_Status13 VARCHAR(20) NOT NULL DEFAULT 'SELECTED' CHECK (
        djx_Status13 IN (
            'SELECTED',
            'DROPPED',
            'COMPLETED'
        )
    ),
    -- 选课时间。
    djx_SelectedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 退选时间，未退选时为空。
    djx_DroppedAt13 TIMESTAMP NULL,
    UNIQUE (
        djx_StudentId13,
        djx_AssignmentId13
    )
);

-- 教学评价表，保存学生对已完成选课记录的一次性最终评价。
CREATE TABLE Dengjx_TeachingEvaluations13 (
    -- 教学评价主键编号。
    djx_EvaluationId13 BIGSERIAL PRIMARY KEY,
    -- 对应选课记录编号，每条选课记录最多评价一次。
    djx_EnrollmentId13 BIGINT NOT NULL UNIQUE REFERENCES Dengjx_Enrollments13 (djx_EnrollmentId13),
    -- 评价等级，1到5表示从非常不满意到非常满意。
    djx_Rating13 INTEGER NOT NULL CHECK (djx_Rating13 BETWEEN 1 AND 5),
    -- 评价理由，可为空。
    djx_Comment13 VARCHAR(500) NULL,
    -- 评价提交时间。
    djx_EvaluatedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 成绩信息表，保存学生选课记录对应的课程成绩。
CREATE TABLE Dengjx_Grades13 (
    -- 成绩主键编号。
    djx_GradeId13 BIGSERIAL PRIMARY KEY,
    -- 对应选课记录编号。
    djx_EnrollmentId13 BIGINT NOT NULL UNIQUE REFERENCES Dengjx_Enrollments13 (djx_EnrollmentId13),
    -- 课程成绩，范围为0到100。
    djx_Score13 NUMERIC(5, 2) NOT NULL CHECK (djx_Score13 BETWEEN 0 AND 100),
    -- 成绩录入时间。
    djx_GradedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 系统用户表，保存管理员、教师和学生登录账号及角色绑定关系。
CREATE TABLE Dengjx_Users13 (
    -- 用户主键编号。
    djx_UserId13 BIGSERIAL PRIMARY KEY,
    -- 登录用户名。
    djx_Username13 VARCHAR(64) NOT NULL UNIQUE,
    -- 登录密码哈希值。
    djx_Password13 VARCHAR(100) NOT NULL,
    -- 用户角色，ADMIN表示管理员，TEACHER表示教师，STUDENT表示学生。
    djx_Role13 VARCHAR(20) NOT NULL CHECK (
        djx_Role13 IN ('ADMIN', 'TEACHER', 'STUDENT')
    ),
    -- 关联学生编号，学生账号使用。
    djx_StudentId13 BIGINT NULL REFERENCES Dengjx_Students13 (djx_StudentId13),
    -- 关联教师编号，教师账号使用。
    djx_TeacherId13 BIGINT NULL REFERENCES Dengjx_Teachers13 (djx_TeacherId13),
    -- 账号是否启用。
    djx_Enabled13 BOOLEAN NOT NULL DEFAULT TRUE,
    -- 登录令牌版本号，密码或账号关键字段变更后递增以使旧令牌失效。
    djx_TokenVersion13 INTEGER NOT NULL DEFAULT 0 CHECK (djx_TokenVersion13 >= 0),
    CONSTRAINT chk_djx_users_role_binding13 CHECK (
        (
            djx_Role13 = 'ADMIN'
            AND djx_StudentId13 IS NULL
            AND djx_TeacherId13 IS NULL
        )
        OR (
            djx_Role13 = 'STUDENT'
            AND djx_StudentId13 IS NOT NULL
            AND djx_TeacherId13 IS NULL
        )
        OR (
            djx_Role13 = 'TEACHER'
            AND djx_TeacherId13 IS NOT NULL
            AND djx_StudentId13 IS NULL
        )
    )
);

CREATE INDEX idx_djx_students_class13 ON Dengjx_Students13 (djx_ClassId13);

CREATE INDEX idx_djx_students_region13 ON Dengjx_Students13 (djx_RegionId13);

CREATE INDEX idx_djx_majorcourses_major13 ON Dengjx_MajorCourses13 (djx_MajorId13);

CREATE INDEX idx_djx_majorcourses_course13 ON Dengjx_MajorCourses13 (djx_CourseId13);

CREATE INDEX idx_djx_assignments_majorcourse13 ON Dengjx_TeachingAssignments13 (djx_MajorCourseId13);

CREATE INDEX idx_djx_assignments_teacher13 ON Dengjx_TeachingAssignments13 (djx_TeacherId13);

CREATE INDEX idx_djx_assignments_class13 ON Dengjx_TeachingAssignments13 (djx_ClassId13);

CREATE INDEX idx_djx_assignments_classroom13 ON Dengjx_TeachingAssignments13 (djx_ClassroomId13);

CREATE INDEX idx_djx_enrollments_student13 ON Dengjx_Enrollments13 (djx_StudentId13);

CREATE INDEX idx_djx_assignments_teacher_term13 ON Dengjx_TeachingAssignments13 (
    djx_TeacherId13,
    djx_AcademicYear13,
    djx_Semester13
);

CREATE INDEX idx_djx_assignments_class_term13 ON Dengjx_TeachingAssignments13 (
    djx_ClassId13,
    djx_AcademicYear13,
    djx_Semester13
);

CREATE INDEX idx_djx_assignments_classroom_term13 ON Dengjx_TeachingAssignments13 (
    djx_ClassroomId13,
    djx_AcademicYear13,
    djx_Semester13
);

CREATE INDEX idx_djx_enrollments_assignment_status13 ON Dengjx_Enrollments13 (
    djx_AssignmentId13,
    djx_Status13
);

CREATE INDEX idx_djx_enrollments_student_status13 ON Dengjx_Enrollments13 (djx_StudentId13, djx_Status13);

CREATE INDEX idx_djx_majortransfer_student13 ON Dengjx_MajorTransferApplications13 (djx_StudentId13);

CREATE INDEX idx_djx_finalexams_course13 ON Dengjx_FinalExams13 (djx_CourseId13);

CREATE INDEX idx_djx_evaluations_rating13 ON Dengjx_TeachingEvaluations13 (djx_Rating13);

CREATE INDEX idx_djx_grades_score13 ON Dengjx_Grades13 (djx_Score13);

CREATE UNIQUE INDEX uk_djx_users_student13 ON Dengjx_Users13 (djx_StudentId13)
WHERE
    djx_StudentId13 IS NOT NULL;

CREATE UNIQUE INDEX uk_djx_users_teacher13 ON Dengjx_Users13 (djx_TeacherId13)
WHERE
    djx_TeacherId13 IS NOT NULL;

DROP VIEW IF EXISTS V_Dengjx_StudentCreditSummary13;

DROP VIEW IF EXISTS V_Dengjx_RegionStudentCount13;

DROP VIEW IF EXISTS V_Dengjx_CourseAverage13;

DROP VIEW IF EXISTS V_Dengjx_ClassCourses13;

DROP VIEW IF EXISTS V_Dengjx_TeacherAssignments13;

DROP VIEW IF EXISTS V_Dengjx_StudentGrades13;

DROP VIEW IF EXISTS V_Dengjx_TeachingEvaluations13;

CREATE OR REPLACE VIEW V_Dengjx_StudentGrades13 AS
SELECT
    s.djx_StudentId13,
    s.djx_Sno13,
    s.djx_Sname13,
    cl.djx_ClassId13,
    cl.djx_ClassName13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    t.djx_TeacherId13,
    t.djx_Tno13,
    t.djx_Tname13,
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13,
    g.djx_Score13,
    c.djx_Credit13,
    g.djx_GradedAt13
FROM
    Dengjx_Grades13 g
    JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = g.djx_EnrollmentId13
    JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
WHERE
    e.djx_Status13 <> 'DROPPED';

CREATE OR REPLACE VIEW V_Dengjx_TeachingEvaluations13 AS
SELECT
    ev.djx_EvaluationId13,
    ev.djx_Rating13,
    ev.djx_Comment13,
    ev.djx_EvaluatedAt13,
    e.djx_EnrollmentId13,
    s.djx_StudentId13,
    s.djx_Sno13,
    s.djx_Sname13,
    a.djx_AssignmentId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassName13,
    t.djx_TeacherId13,
    t.djx_Tno13,
    t.djx_Tname13,
    a.djx_AcademicYear13,
    a.djx_Semester13
FROM
    Dengjx_TeachingEvaluations13 ev
    JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = ev.djx_EnrollmentId13
    JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13;

CREATE OR REPLACE VIEW V_Dengjx_TeacherAssignments13 AS
SELECT
    t.djx_TeacherId13,
    t.djx_Tno13,
    t.djx_Tname13,
    a.djx_AssignmentId13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassId13,
    cl.djx_ClassName13,
    cr.djx_ClassroomId13,
    tb.djx_BuildingName13,
    cr.djx_ClassroomName13,
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13,
    a.djx_Capacity13,
    a.djx_EnrollmentOpen13,
    a.djx_WeekdayOne13,
    a.djx_StartTimeOne13,
    a.djx_EndTimeOne13,
    a.djx_WeekdayTwo13,
    a.djx_StartTimeTwo13,
    a.djx_EndTimeTwo13,
    COUNT(
        CASE
            WHEN e.djx_Status13 IN ('SELECTED', 'COMPLETED') THEN e.djx_EnrollmentId13
        END
    ) AS djx_SelectedCount13
FROM
    Dengjx_TeachingAssignments13 a
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_Classrooms13 cr ON cr.djx_ClassroomId13 = a.djx_ClassroomId13
    JOIN Dengjx_TeachingBuildings13 tb ON tb.djx_BuildingId13 = cr.djx_BuildingId13
    LEFT JOIN Dengjx_Enrollments13 e ON e.djx_AssignmentId13 = a.djx_AssignmentId13
GROUP BY
    t.djx_TeacherId13,
    t.djx_Tno13,
    t.djx_Tname13,
    a.djx_AssignmentId13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassId13,
    cl.djx_ClassName13,
    cr.djx_ClassroomId13,
    tb.djx_BuildingName13,
    cr.djx_ClassroomName13,
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13,
    a.djx_Capacity13,
    a.djx_EnrollmentOpen13,
    a.djx_WeekdayOne13,
    a.djx_StartTimeOne13,
    a.djx_EndTimeOne13,
    a.djx_WeekdayTwo13,
    a.djx_StartTimeTwo13,
    a.djx_EndTimeTwo13;

CREATE OR REPLACE VIEW V_Dengjx_ClassCourses13 AS
SELECT
    cl.djx_ClassId13,
    cl.djx_ClassName13,
    m.djx_MajorId13,
    m.djx_MajorName13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    t.djx_TeacherId13,
    t.djx_Tno13,
    t.djx_Tname13,
    a.djx_AssignmentId13,
    cr.djx_ClassroomId13,
    tb.djx_BuildingName13,
    cr.djx_ClassroomName13,
    a.djx_AcademicYear13,
    a.djx_Semester13,
    a.djx_WeekdayOne13,
    a.djx_StartTimeOne13,
    a.djx_EndTimeOne13,
    a.djx_WeekdayTwo13,
    a.djx_StartTimeTwo13,
    a.djx_EndTimeTwo13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13
FROM
    Dengjx_TeachingAssignments13 a
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_Majors13 m ON m.djx_MajorId13 = cl.djx_MajorId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
    JOIN Dengjx_Classrooms13 cr ON cr.djx_ClassroomId13 = a.djx_ClassroomId13
    JOIN Dengjx_TeachingBuildings13 tb ON tb.djx_BuildingId13 = cr.djx_BuildingId13;

CREATE OR REPLACE VIEW V_Dengjx_CourseAverage13 AS
SELECT
    a.djx_AssignmentId13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassName13,
    t.djx_Tname13,
    cr.djx_ClassroomId13,
    tb.djx_BuildingName13,
    cr.djx_ClassroomName13,
    a.djx_AcademicYear13,
    a.djx_Semester13,
    ROUND(AVG(g.djx_Score13), 2) AS djx_AverageScore13,
    MAX(g.djx_Score13) AS djx_MaxScore13,
    MIN(g.djx_Score13) AS djx_MinScore13,
    SUM(
        CASE
            WHEN g.djx_Score13 >= 60 THEN 1
            ELSE 0
        END
    ) AS djx_PassCount13,
    COUNT(g.djx_GradeId13) AS djx_StudentCount13
FROM
    Dengjx_TeachingAssignments13 a
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
    JOIN Dengjx_Classrooms13 cr ON cr.djx_ClassroomId13 = a.djx_ClassroomId13
    JOIN Dengjx_TeachingBuildings13 tb ON tb.djx_BuildingId13 = cr.djx_BuildingId13
    LEFT JOIN Dengjx_Enrollments13 e ON e.djx_AssignmentId13 = a.djx_AssignmentId13
    AND e.djx_Status13 <> 'DROPPED'
    LEFT JOIN Dengjx_Grades13 g ON g.djx_EnrollmentId13 = e.djx_EnrollmentId13
GROUP BY
    a.djx_AssignmentId13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassName13,
    t.djx_Tname13,
    cr.djx_ClassroomId13,
    tb.djx_BuildingName13,
    cr.djx_ClassroomName13,
    a.djx_AcademicYear13,
    a.djx_Semester13;

CREATE OR REPLACE VIEW V_Dengjx_RegionStudentCount13 AS
SELECT r.djx_RegionId13, r.djx_RegionName13, COUNT(s.djx_StudentId13) AS djx_StudentCount13
FROM
    Dengjx_Regions13 r
    LEFT JOIN Dengjx_Students13 s ON s.djx_RegionId13 = r.djx_RegionId13
GROUP BY
    r.djx_RegionId13,
    r.djx_RegionName13;

CREATE OR REPLACE VIEW V_Dengjx_StudentCreditSummary13 AS
SELECT
    s.djx_StudentId13,
    s.djx_Sno13,
    s.djx_Sname13,
    cl.djx_ClassName13,
    m.djx_MajorName13,
    m.djx_GraduationCredits13,
    COUNT(g.djx_GradeId13) AS djx_CompletedCourses13,
    SUM(
        CASE
            WHEN g.djx_Score13 >= 60 THEN 1
            ELSE 0
        END
    ) AS djx_PassedCourses13,
    s.djx_TotalCredits13,
    COALESCE(
        SUM(
            CASE
                WHEN g.djx_Score13 >= 60 THEN c.djx_Credit13
                ELSE 0
            END
        ),
        0
    ) AS djx_CalculatedCredits13
FROM
    Dengjx_Students13 s
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
    JOIN Dengjx_Majors13 m ON m.djx_MajorId13 = cl.djx_MajorId13
    LEFT JOIN Dengjx_Enrollments13 e ON e.djx_StudentId13 = s.djx_StudentId13
    AND e.djx_Status13 <> 'DROPPED'
    LEFT JOIN Dengjx_Grades13 g ON g.djx_EnrollmentId13 = e.djx_EnrollmentId13
    LEFT JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
    LEFT JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    LEFT JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
GROUP BY
    s.djx_StudentId13,
    s.djx_Sno13,
    s.djx_Sname13,
    s.djx_TotalCredits13,
    cl.djx_ClassName13,
    m.djx_MajorName13,
    m.djx_GraduationCredits13;

DROP TRIGGER IF EXISTS Dengjx_GradesAfterInsertUpdate13 ON Dengjx_Grades13;

DROP TRIGGER IF EXISTS Dengjx_GradesAfterDelete13 ON Dengjx_Grades13;

DROP FUNCTION IF EXISTS Dengjx_GradesAfterInsertUpdateFn13 ();

DROP FUNCTION IF EXISTS Dengjx_GradesAfterDeleteFn13 ();

DROP FUNCTION IF EXISTS Dengjx_RecalculateStudentCredits13 (BIGINT);

CREATE OR REPLACE FUNCTION Dengjx_RecalculateStudentCredits13(student_id BIGINT)
RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Dengjx_Students13
    SET djx_TotalCredits13 = COALESCE((
        SELECT SUM(c.djx_Credit13)
        FROM Dengjx_Grades13 g
        JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = g.djx_EnrollmentId13
        JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
        JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
        JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
        WHERE e.djx_StudentId13 = student_id
          AND e.djx_Status13 <> 'DROPPED'
          AND g.djx_Score13 >= 60
    ), 0)
    WHERE djx_StudentId13 = student_id;
END;
$$;

CREATE OR REPLACE FUNCTION Dengjx_GradesAfterInsertUpdateFn13()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    new_student_id BIGINT;
    old_student_id BIGINT;
BEGIN
    SELECT e.djx_StudentId13
    INTO new_student_id
    FROM Dengjx_Enrollments13 e
    WHERE e.djx_EnrollmentId13 = NEW.djx_EnrollmentId13;

    IF new_student_id IS NOT NULL THEN
        PERFORM Dengjx_RecalculateStudentCredits13(new_student_id);
    END IF;

    IF TG_OP = 'UPDATE' AND OLD.djx_EnrollmentId13 <> NEW.djx_EnrollmentId13 THEN
        SELECT e.djx_StudentId13
        INTO old_student_id
        FROM Dengjx_Enrollments13 e
        WHERE e.djx_EnrollmentId13 = OLD.djx_EnrollmentId13;

        IF old_student_id IS NOT NULL AND old_student_id <> new_student_id THEN
            PERFORM Dengjx_RecalculateStudentCredits13(old_student_id);
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION Dengjx_GradesAfterDeleteFn13()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    old_student_id BIGINT;
BEGIN
    SELECT e.djx_StudentId13
    INTO old_student_id
    FROM Dengjx_Enrollments13 e
    WHERE e.djx_EnrollmentId13 = OLD.djx_EnrollmentId13;

    IF old_student_id IS NOT NULL THEN
        PERFORM Dengjx_RecalculateStudentCredits13(old_student_id);
    END IF;

    RETURN OLD;
END;
$$;

CREATE TRIGGER Dengjx_GradesAfterInsertUpdate13
AFTER INSERT OR UPDATE ON Dengjx_Grades13
FOR EACH ROW
EXECUTE PROCEDURE Dengjx_GradesAfterInsertUpdateFn13();

CREATE TRIGGER Dengjx_GradesAfterDelete13
AFTER DELETE ON Dengjx_Grades13
FOR EACH ROW
EXECUTE PROCEDURE Dengjx_GradesAfterDeleteFn13();

CREATE OR REPLACE PROCEDURE Dengjx_GetStudentYearScores13(
    IN student_id BIGINT,
    IN academic_year VARCHAR,
    OUT result_cursor REFCURSOR
)
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_cursor FOR
        SELECT
            c.djx_CourseCode13,
            c.djx_CourseName13,
            a.djx_Semester13,
            g.djx_Score13,
            c.djx_Credit13,
            t.djx_Tno13,
            t.djx_Tname13
        FROM Dengjx_Grades13 g
        JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = g.djx_EnrollmentId13
        JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
        JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
        JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
        JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13
        WHERE e.djx_StudentId13 = student_id
          AND a.djx_AcademicYear13 = academic_year
          AND e.djx_Status13 <> 'DROPPED'
        ORDER BY a.djx_Semester13, c.djx_CourseCode13;
END;
$$;

CREATE OR REPLACE PROCEDURE Dengjx_GetCourseScoreRank13(
    IN assignment_id BIGINT,
    OUT result_cursor REFCURSOR
)
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN result_cursor FOR
        SELECT
            s.djx_StudentId13,
            s.djx_Sno13,
            s.djx_Sname13,
            cl.djx_ClassId13,
            cl.djx_ClassName13,
            g.djx_Score13,
            RANK() OVER (ORDER BY g.djx_Score13 DESC) AS djx_ScoreRank13,
            ROUND(AVG(g.djx_Score13) OVER (), 2) AS djx_CourseAverage13
        FROM Dengjx_Grades13 g
        JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = g.djx_EnrollmentId13
        JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
        JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
        WHERE e.djx_AssignmentId13 = assignment_id
          AND e.djx_Status13 <> 'DROPPED'
        ORDER BY g.djx_Score13 DESC, s.djx_Sno13;
END;
$$;

DELETE FROM Dengjx_Users13;

DELETE FROM Dengjx_TeachingEvaluations13;

DELETE FROM Dengjx_Grades13;

DELETE FROM Dengjx_Enrollments13;

DELETE FROM Dengjx_MajorTransferApplications13;

DELETE FROM Dengjx_MajorTransferSettings13;

DELETE FROM Dengjx_TeachingAssignments13;

DELETE FROM Dengjx_FinalExams13;

DELETE FROM Dengjx_EnrollmentSettings13;

DELETE FROM Dengjx_MajorCourses13;

DELETE FROM Dengjx_Courses13;

DELETE FROM Dengjx_Teachers13;

DELETE FROM Dengjx_Students13;

DELETE FROM Dengjx_Classes13;

DELETE FROM Dengjx_Classrooms13;

DELETE FROM Dengjx_TeachingBuildings13;

DELETE FROM Dengjx_Majors13;

DELETE FROM Dengjx_Regions13;

INSERT INTO
    Dengjx_Regions13 (djx_RegionName13)
VALUES ('湖北省'),
    ('湖南省'),
    ('广东省'),
    ('河南省'),
    ('四川省'),
    ('浙江省'),
    ('江苏省'),
    ('山东省'),
    ('福建省'),
    ('安徽省');

INSERT INTO
    Dengjx_Majors13 (
        djx_MajorName13,
        djx_GraduationCredits13
    )
VALUES ('软件工程', 160.00),
    ('计算机科学与技术', 160.00),
    ('数据科学与大数据技术', 160.00);

INSERT INTO
    Dengjx_EnrollmentSettings13 (
        djx_SettingId13,
        djx_Enabled13
    )
VALUES (1, TRUE);

INSERT INTO
    Dengjx_MajorTransferSettings13 (
        djx_SettingId13,
        djx_Enabled13
    )
VALUES (1, TRUE);

INSERT INTO
    Dengjx_Classes13 (
        djx_ClassName13,
        djx_MajorId13,
        djx_GradeYear13
    )
SELECT v.class_name, m.djx_MajorId13, 2025
FROM (
        VALUES ('软件工程2501', '软件工程'), ('软件工程2502', '软件工程'), ('计科2501', '计算机科学与技术'), ('计科2502', '计算机科学与技术'), ('大数据2501', '数据科学与大数据技术'), ('大数据2502', '数据科学与大数据技术')
    ) AS v (class_name, major_name)
    JOIN Dengjx_Majors13 m ON m.djx_MajorName13 = v.major_name;

INSERT INTO
    Dengjx_TeachingBuildings13 (djx_BuildingName13)
VALUES ('健行楼'),
    ('广知楼'),
    ('博易楼'),
    ('仁和楼'),
    ('法学楼');

INSERT INTO
    Dengjx_Classrooms13 (
        djx_BuildingId13,
        djx_ClassroomName13,
        djx_Capacity13
    )
SELECT b.djx_BuildingId13, v.classroom_name, v.capacity
FROM (
        VALUES ('健行楼', 'B201', 80),
            ('健行楼', 'B305', 80),
            ('广知楼', 'A101', 90),
            ('广知楼', 'C203', 90),
            ('博易楼', 'C109', 70),
            ('博易楼', 'B208', 70),
            ('仁和楼', '105', 60),
            ('仁和楼', '203', 60),
            ('法学楼', 'B101', 80),
            ('法学楼', 'B306', 80)
    ) AS v (
        building_name,
        classroom_name,
        capacity
    )
    JOIN Dengjx_TeachingBuildings13 b ON b.djx_BuildingName13 = v.building_name;

INSERT INTO
    Dengjx_Students13 (
        djx_Sno13,
        djx_Sname13,
        djx_Gender13,
        djx_Age13,
        djx_ClassId13,
        djx_RegionId13,
        djx_AdmissionDate13
    )
SELECT v.sno, v.sname, v.gender, v.age, cl.djx_ClassId13, r.djx_RegionId13, DATE '2025-09-01'
FROM (
        VALUES (
                '20250001',
                '刘晨',
                'MALE',
                18,
                '软件工程2501',
                '湖北省'
            ),
            (
                '20250002',
                '李雪',
                'FEMALE',
                18,
                '软件工程2501',
                '湖南省'
            ),
            (
                '20250003',
                '王浩',
                'MALE',
                19,
                '软件工程2501',
                '广东省'
            ),
            (
                '20250004',
                '赵敏',
                'FEMALE',
                18,
                '软件工程2501',
                '河南省'
            ),
            (
                '20250005',
                '陈杰',
                'MALE',
                19,
                '软件工程2501',
                '四川省'
            ),
            (
                '20250006',
                '周怡',
                'FEMALE',
                18,
                '软件工程2501',
                '浙江省'
            ),
            (
                '20250007',
                '邓超',
                'MALE',
                18,
                '软件工程2501',
                '江苏省'
            ),
            (
                '20250008',
                '林悦',
                'FEMALE',
                19,
                '软件工程2501',
                '山东省'
            ),
            (
                '20250009',
                '马骏',
                'MALE',
                18,
                '软件工程2502',
                '福建省'
            ),
            (
                '20250010',
                '高雅',
                'FEMALE',
                18,
                '软件工程2502',
                '安徽省'
            ),
            (
                '20250011',
                '罗成',
                'MALE',
                19,
                '软件工程2502',
                '湖北省'
            ),
            (
                '20250012',
                '许宁',
                'FEMALE',
                18,
                '软件工程2502',
                '浙江省'
            ),
            (
                '20250013',
                '程远',
                'MALE',
                18,
                '软件工程2502',
                '江苏省'
            ),
            (
                '20250014',
                '姚瑶',
                'FEMALE',
                19,
                '软件工程2502',
                '山东省'
            ),
            (
                '20250015',
                '蔡明',
                'MALE',
                18,
                '软件工程2502',
                '广东省'
            ),
            (
                '20250016',
                '宋佳',
                'FEMALE',
                18,
                '软件工程2502',
                '河南省'
            ),
            (
                '20250017',
                '孙磊',
                'MALE',
                18,
                '计科2501',
                '湖北省'
            ),
            (
                '20250018',
                '吴倩',
                'FEMALE',
                18,
                '计科2501',
                '湖南省'
            ),
            (
                '20250019',
                '郑宇',
                'MALE',
                19,
                '计科2501',
                '广东省'
            ),
            (
                '20250020',
                '何琳',
                'FEMALE',
                18,
                '计科2501',
                '河南省'
            ),
            (
                '20250021',
                '郭强',
                'MALE',
                18,
                '计科2501',
                '四川省'
            ),
            (
                '20250022',
                '唐静',
                'FEMALE',
                19,
                '计科2501',
                '浙江省'
            ),
            (
                '20250023',
                '袁博',
                'MALE',
                18,
                '计科2501',
                '江苏省'
            ),
            (
                '20250024',
                '韩梅',
                'FEMALE',
                18,
                '计科2501',
                '山东省'
            ),
            (
                '20250025',
                '沈航',
                'MALE',
                19,
                '计科2502',
                '福建省'
            ),
            (
                '20250026',
                '叶琳',
                'FEMALE',
                18,
                '计科2502',
                '安徽省'
            ),
            (
                '20250027',
                '许晨',
                'MALE',
                18,
                '计科2502',
                '湖北省'
            ),
            (
                '20250028',
                '秦月',
                'FEMALE',
                19,
                '计科2502',
                '浙江省'
            ),
            (
                '20250029',
                '邵峰',
                'MALE',
                18,
                '计科2502',
                '江苏省'
            ),
            (
                '20250030',
                '白璐',
                'FEMALE',
                18,
                '计科2502',
                '山东省'
            ),
            (
                '20250031',
                '尹航',
                'MALE',
                19,
                '计科2502',
                '广东省'
            ),
            (
                '20250032',
                '贺敏',
                'FEMALE',
                18,
                '计科2502',
                '河南省'
            ),
            (
                '20250033',
                '顾然',
                'MALE',
                18,
                '大数据2501',
                '湖北省'
            ),
            (
                '20250034',
                '蒋怡',
                'FEMALE',
                18,
                '大数据2501',
                '湖南省'
            ),
            (
                '20250035',
                '傅强',
                'MALE',
                19,
                '大数据2501',
                '广东省'
            ),
            (
                '20250036',
                '潘悦',
                'FEMALE',
                18,
                '大数据2501',
                '河南省'
            ),
            (
                '20250037',
                '余洋',
                'MALE',
                18,
                '大数据2501',
                '四川省'
            ),
            (
                '20250038',
                '戴宁',
                'FEMALE',
                19,
                '大数据2501',
                '浙江省'
            ),
            (
                '20250039',
                '孟涛',
                'MALE',
                18,
                '大数据2501',
                '江苏省'
            ),
            (
                '20250040',
                '卢佳',
                'FEMALE',
                18,
                '大数据2501',
                '山东省'
            ),
            (
                '20250041',
                '曾越',
                'MALE',
                19,
                '大数据2502',
                '福建省'
            ),
            (
                '20250042',
                '夏雨',
                'FEMALE',
                18,
                '大数据2502',
                '安徽省'
            ),
            (
                '20250043',
                '范哲',
                'MALE',
                18,
                '大数据2502',
                '湖北省'
            ),
            (
                '20250044',
                '陆瑶',
                'FEMALE',
                19,
                '大数据2502',
                '浙江省'
            ),
            (
                '20250045',
                '田浩',
                'MALE',
                18,
                '大数据2502',
                '江苏省'
            ),
            (
                '20250046',
                '钟敏',
                'FEMALE',
                18,
                '大数据2502',
                '山东省'
            ),
            (
                '20250047',
                '汪博',
                'MALE',
                19,
                '大数据2502',
                '广东省'
            ),
            (
                '20250048',
                '施琪',
                'FEMALE',
                18,
                '大数据2502',
                '河南省'
            )
    ) AS v (
        sno,
        sname,
        gender,
        age,
        class_name,
        region_name
    )
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassName13 = v.class_name
    JOIN Dengjx_Regions13 r ON r.djx_RegionName13 = v.region_name;

INSERT INTO
    Dengjx_Teachers13 (
        djx_Tno13,
        djx_Tname13,
        djx_Gender13,
        djx_Age13,
        djx_Title13,
        djx_Phone13
    )
VALUES (
        'T2023001',
        '张建国',
        'MALE',
        45,
        '教授',
        '13800000001'
    ),
    (
        'T2023002',
        '李芳',
        'FEMALE',
        39,
        '副教授',
        '13800000002'
    ),
    (
        'T2023003',
        '王磊',
        'MALE',
        36,
        '讲师',
        '13800000003'
    ),
    (
        'T2023004',
        '陈静',
        'FEMALE',
        42,
        '副教授',
        '13800000004'
    ),
    (
        'T2023005',
        '刘洋',
        'MALE',
        33,
        '讲师',
        '13800000005'
    ),
    (
        'T2023006',
        '赵文博',
        'MALE',
        38,
        '副教授',
        '13800000006'
    ),
    (
        'T2023007',
        '胡敏',
        'FEMALE',
        34,
        '讲师',
        '13800000007'
    ),
    (
        'T2023008',
        '马强',
        'MALE',
        41,
        '教授',
        '13800000008'
    );

INSERT INTO
    Dengjx_Courses13 (
        djx_CourseCode13,
        djx_CourseName13,
        djx_Hours13,
        djx_AssessmentType13,
        djx_Credit13
    )
VALUES (
        'CS101',
        '数据库原理',
        64,
        'EXAM',
        4.0
    ),
    (
        'CS102',
        'Java程序设计',
        64,
        'EXAM',
        4.0
    ),
    (
        'CS103',
        '数据结构',
        64,
        'EXAM',
        4.0
    ),
    (
        'CS104',
        '操作系统',
        64,
        'EXAM',
        4.0
    ),
    (
        'CS105',
        '计算机网络',
        48,
        'EXAM',
        3.0
    ),
    (
        'CS106',
        '软件工程',
        48,
        'CHECK',
        3.0
    ),
    (
        'CS107',
        'Web前端开发',
        48,
        'CHECK',
        2.5
    ),
    (
        'CS108',
        '人工智能导论',
        32,
        'CHECK',
        2.0
    ),
    (
        'CS109',
        'Python数据分析',
        48,
        'CHECK',
        3.0
    ),
    (
        'CS110',
        '信息安全基础',
        48,
        'EXAM',
        3.0
    ),
    (
        'CS111',
        '云计算导论',
        32,
        'CHECK',
        2.0
    ),
    (
        'CS112',
        '移动应用开发',
        48,
        'CHECK',
        2.5
    ),
    (
        'CS113',
        '分布式系统',
        64,
        'EXAM',
        4.0
    ),
    (
        'CS114',
        '软件测试技术',
        48,
        'CHECK',
        3.0
    ),
    (
        'CS115',
        '毕业设计指导',
        32,
        'CHECK',
        2.0
    ),
    (
        'CS116',
        '云原生应用开发',
        48,
        'CHECK',
        3.0
    );

INSERT INTO
    Dengjx_MajorCourses13 (
        djx_MajorId13,
        djx_CourseId13,
        djx_CourseType13,
        djx_TargetGrade13,
        djx_TargetSemester13
    )
SELECT m.djx_MajorId13, c.djx_CourseId13, v.course_type, 1, 2
FROM (
        VALUES ('软件工程', 'CS102', 'REQUIRED'),
            ('软件工程', 'CS101', 'REQUIRED'),
            ('软件工程', 'CS103', 'REQUIRED'),
            ('软件工程', 'CS106', 'REQUIRED'),
            ('软件工程', 'CS107', 'ELECTIVE'),
            ('软件工程', 'CS112', 'ELECTIVE'),
            ('软件工程', 'CS111', 'ELECTIVE'),
            ('软件工程', 'CS114', 'ELECTIVE'),
            (
                '计算机科学与技术',
                'CS101',
                'REQUIRED'
            ),
            (
                '计算机科学与技术',
                'CS103',
                'REQUIRED'
            ),
            (
                '计算机科学与技术',
                'CS104',
                'REQUIRED'
            ),
            (
                '计算机科学与技术',
                'CS110',
                'REQUIRED'
            ),
            (
                '计算机科学与技术',
                'CS108',
                'ELECTIVE'
            ),
            (
                '计算机科学与技术',
                'CS111',
                'ELECTIVE'
            ),
            (
                '计算机科学与技术',
                'CS113',
                'ELECTIVE'
            ),
            (
                '计算机科学与技术',
                'CS116',
                'ELECTIVE'
            ),
            (
                '数据科学与大数据技术',
                'CS101',
                'REQUIRED'
            ),
            (
                '数据科学与大数据技术',
                'CS103',
                'REQUIRED'
            ),
            (
                '数据科学与大数据技术',
                'CS105',
                'REQUIRED'
            ),
            (
                '数据科学与大数据技术',
                'CS106',
                'REQUIRED'
            ),
            (
                '数据科学与大数据技术',
                'CS108',
                'ELECTIVE'
            ),
            (
                '数据科学与大数据技术',
                'CS109',
                'ELECTIVE'
            ),
            (
                '数据科学与大数据技术',
                'CS113',
                'ELECTIVE'
            ),
            (
                '数据科学与大数据技术',
                'CS116',
                'ELECTIVE'
            )
    ) AS v (
        major_name,
        course_code,
        course_type
    )
    JOIN Dengjx_Majors13 m ON m.djx_MajorName13 = v.major_name
    JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code;

INSERT INTO
    Dengjx_TeachingAssignments13 (
        djx_MajorCourseId13,
        djx_ClassId13,
        djx_TeacherId13,
        djx_ClassroomId13,
        djx_AcademicYear13,
        djx_Semester13,
        djx_Capacity13,
        djx_EnrollmentOpen13,
        djx_WeekdayOne13,
        djx_StartTimeOne13,
        djx_EndTimeOne13,
        djx_WeekdayTwo13,
        djx_StartTimeTwo13,
        djx_EndTimeTwo13
    )
SELECT mc.djx_MajorCourseId13, cl.djx_ClassId13, t.djx_TeacherId13, cr.djx_ClassroomId13, '2025-2026', 2, v.capacity, v.enrollment_open, v.weekday_one, v.start_time_one, v.end_time_one, v.weekday_two, v.start_time_two, v.end_time_two
FROM (
        VALUES (
                '软件工程2501',
                '软件工程',
                'CS102',
                'T2023001',
                60,
                FALSE,
                1,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS101',
                'T2023002',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS103',
                'T2023003',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS106',
                'T2023004',
                60,
                FALSE,
                2,
                TIME '09:55:00',
                TIME '11:35:00',
                4,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS107',
                'T2023005',
                40,
                TRUE,
                5,
                TIME '13:30:00',
                TIME '15:10:00',
                3,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS112',
                'T2023006',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS111',
                'T2023007',
                40,
                TRUE,
                5,
                TIME '09:55:00',
                TIME '11:35:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '软件工程2501',
                '软件工程',
                'CS114',
                'T2023008',
                40,
                TRUE,
                1,
                TIME '18:30:00',
                TIME '20:10:00',
                4,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS102',
                'T2023003',
                60,
                FALSE,
                1,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS101',
                'T2023004',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS103',
                'T2023005',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS106',
                'T2023006',
                60,
                FALSE,
                2,
                TIME '09:55:00',
                TIME '11:35:00',
                4,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS107',
                'T2023007',
                40,
                TRUE,
                5,
                TIME '13:30:00',
                TIME '15:10:00',
                3,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS112',
                'T2023008',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS111',
                'T2023001',
                40,
                TRUE,
                5,
                TIME '09:55:00',
                TIME '11:35:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '软件工程2502',
                '软件工程',
                'CS114',
                'T2023002',
                40,
                TRUE,
                1,
                TIME '18:30:00',
                TIME '20:10:00',
                4,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS101',
                'T2023003',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS103',
                'T2023004',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS104',
                'T2023005',
                60,
                FALSE,
                1,
                TIME '09:55:00',
                TIME '11:35:00',
                3,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS110',
                'T2023006',
                60,
                FALSE,
                2,
                TIME '13:30:00',
                TIME '15:10:00',
                4,
                TIME '13:30:00',
                TIME '15:10:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS108',
                'T2023007',
                40,
                TRUE,
                5,
                TIME '08:00:00',
                TIME '09:40:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS111',
                'T2023008',
                40,
                TRUE,
                5,
                TIME '09:55:00',
                TIME '11:35:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS113',
                'T2023001',
                40,
                TRUE,
                3,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '计科2501',
                '计算机科学与技术',
                'CS116',
                'T2023002',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS101',
                'T2023005',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS103',
                'T2023006',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS104',
                'T2023007',
                60,
                FALSE,
                1,
                TIME '09:55:00',
                TIME '11:35:00',
                3,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS110',
                'T2023008',
                60,
                FALSE,
                2,
                TIME '13:30:00',
                TIME '15:10:00',
                4,
                TIME '13:30:00',
                TIME '15:10:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS108',
                'T2023001',
                40,
                TRUE,
                5,
                TIME '08:00:00',
                TIME '09:40:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS111',
                'T2023002',
                40,
                TRUE,
                5,
                TIME '09:55:00',
                TIME '11:35:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS113',
                'T2023003',
                40,
                TRUE,
                3,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '计科2502',
                '计算机科学与技术',
                'CS116',
                'T2023004',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS101',
                'T2023006',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS103',
                'T2023007',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS105',
                'T2023007',
                60,
                FALSE,
                1,
                TIME '13:30:00',
                TIME '15:10:00',
                3,
                TIME '13:30:00',
                TIME '15:10:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS106',
                'T2023008',
                60,
                FALSE,
                2,
                TIME '09:55:00',
                TIME '11:35:00',
                4,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS108',
                'T2023002',
                40,
                TRUE,
                5,
                TIME '08:00:00',
                TIME '09:40:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS109',
                'T2023002',
                40,
                TRUE,
                2,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS113',
                'T2023004',
                40,
                TRUE,
                3,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '大数据2501',
                '数据科学与大数据技术',
                'CS116',
                'T2023005',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS101',
                'T2023007',
                60,
                FALSE,
                1,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '08:00:00',
                TIME '09:40:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS103',
                'T2023008',
                60,
                FALSE,
                2,
                TIME '08:00:00',
                TIME '09:40:00',
                3,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS105',
                'T2023001',
                60,
                FALSE,
                1,
                TIME '13:30:00',
                TIME '15:10:00',
                3,
                TIME '13:30:00',
                TIME '15:10:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS106',
                'T2023002',
                60,
                FALSE,
                2,
                TIME '09:55:00',
                TIME '11:35:00',
                4,
                TIME '09:55:00',
                TIME '11:35:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS108',
                'T2023003',
                40,
                TRUE,
                5,
                TIME '08:00:00',
                TIME '09:40:00',
                NULL,
                NULL,
                NULL
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS109',
                'T2023004',
                40,
                TRUE,
                2,
                TIME '15:25:00',
                TIME '17:05:00',
                4,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS113',
                'T2023005',
                40,
                TRUE,
                3,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '18:30:00',
                TIME '20:10:00'
            ),
            (
                '大数据2502',
                '数据科学与大数据技术',
                'CS116',
                'T2023007',
                40,
                TRUE,
                2,
                TIME '18:30:00',
                TIME '20:10:00',
                5,
                TIME '15:25:00',
                TIME '17:05:00'
            )
    ) AS v (
        class_name,
        major_name,
        course_code,
        teacher_no,
        capacity,
        enrollment_open,
        weekday_one,
        start_time_one,
        end_time_one,
        weekday_two,
        start_time_two,
        end_time_two
    )
    JOIN Dengjx_Majors13 m ON m.djx_MajorName13 = v.major_name
    JOIN Dengjx_Classes13 cl ON cl.djx_MajorId13 = m.djx_MajorId13
    AND cl.djx_ClassName13 = v.class_name
    JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorId13 = m.djx_MajorId13
    AND mc.djx_CourseId13 = c.djx_CourseId13
    JOIN Dengjx_Teachers13 t ON t.djx_Tno13 = v.teacher_no
    JOIN Dengjx_TeachingBuildings13 b ON b.djx_BuildingName13 = CASE
        WHEN v.course_code IN ('CS101', 'CS102', 'CS103') THEN '广知楼'
        WHEN v.course_code IN ('CS104', 'CS105', 'CS106') THEN '健行楼'
        WHEN v.course_code IN ('CS107', 'CS108', 'CS109') THEN '博易楼'
        WHEN v.course_code IN ('CS110', 'CS111', 'CS112') THEN '仁和楼'
        ELSE '法学楼'
    END
    JOIN Dengjx_Classrooms13 cr ON cr.djx_BuildingId13 = b.djx_BuildingId13
    AND cr.djx_ClassroomName13 = CASE
        WHEN v.course_code = 'CS101' THEN 'A101'
        WHEN v.course_code = 'CS102' THEN 'C203'
        WHEN v.course_code = 'CS103' THEN 'A101'
        WHEN v.course_code = 'CS104' THEN 'B201'
        WHEN v.course_code = 'CS105' THEN 'B305'
        WHEN v.course_code = 'CS106' THEN 'B201'
        WHEN v.course_code = 'CS107' THEN 'C109'
        WHEN v.course_code = 'CS108' THEN 'B208'
        WHEN v.course_code = 'CS109' THEN 'C109'
        WHEN v.course_code = 'CS110' THEN '105'
        WHEN v.course_code = 'CS111' THEN '203'
        WHEN v.course_code = 'CS112' THEN '105'
        WHEN v.course_code = 'CS113' THEN 'B101'
        WHEN v.course_code = 'CS114' THEN 'B306'
        WHEN v.course_code = 'CS116' THEN 'B101'
    END;

INSERT INTO
    Dengjx_FinalExams13 (
        djx_CourseId13,
        djx_AcademicYear13,
        djx_Semester13,
        djx_ExamTime13
    )
SELECT c.djx_CourseId13, '2025-2026', 2, v.exam_time
FROM (
        VALUES (
                'CS101', TIMESTAMP '2026-06-22 09:00:00'
            ), (
                'CS102', TIMESTAMP '2026-06-23 09:00:00'
            ), (
                'CS103', TIMESTAMP '2026-06-24 14:00:00'
            ), (
                'CS104', TIMESTAMP '2026-06-25 09:00:00'
            ), (
                'CS105', TIMESTAMP '2026-06-25 14:00:00'
            ), (
                'CS106', TIMESTAMP '2026-06-26 09:00:00'
            ), (
                'CS107', TIMESTAMP '2026-06-29 09:00:00'
            ), (
                'CS108', TIMESTAMP '2026-06-29 14:00:00'
            ), (
                'CS109', TIMESTAMP '2026-06-30 09:00:00'
            ), (
                'CS110', TIMESTAMP '2026-06-30 14:00:00'
            ), (
                'CS111', TIMESTAMP '2026-07-01 09:00:00'
            ), (
                'CS112', TIMESTAMP '2026-07-01 14:00:00'
            ), (
                'CS113', TIMESTAMP '2026-07-02 09:00:00'
            ), (
                'CS114', TIMESTAMP '2026-07-02 14:00:00'
            ), (
                'CS116', TIMESTAMP '2026-07-03 09:00:00'
            )
    ) AS v (course_code, exam_time)
    JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code;

INSERT INTO
    Dengjx_Enrollments13 (
        djx_StudentId13,
        djx_AssignmentId13,
        djx_Status13,
        djx_SelectedAt13,
        djx_DroppedAt13
    )
SELECT s.djx_StudentId13, a.djx_AssignmentId13, 'COMPLETED', TIMESTAMP '2026-02-24 08:30:00', NULL
FROM
    Dengjx_Students13 s
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_ClassId13 = cl.djx_ClassId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
WHERE
    mc.djx_CourseType13 = 'REQUIRED'
    AND a.djx_AcademicYear13 = '2025-2026'
    AND a.djx_Semester13 = 2;

INSERT INTO
    Dengjx_Enrollments13 (
        djx_StudentId13,
        djx_AssignmentId13,
        djx_Status13,
        djx_SelectedAt13,
        djx_DroppedAt13
    )
SELECT s.djx_StudentId13, a.djx_AssignmentId13, 'SELECTED', TIMESTAMP '2026-02-25 10:00:00', NULL
FROM
    Dengjx_Students13 s
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = s.djx_ClassId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_ClassId13 = cl.djx_ClassId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
WHERE
    mc.djx_CourseType13 = 'ELECTIVE'
    AND a.djx_AcademicYear13 = '2025-2026'
    AND a.djx_Semester13 = 2
    AND (
        (
            MOD(
                CAST(
                    SUBSTRING(
                        s.djx_Sno13
                        FROM 8 FOR 1
                    ) AS INTEGER
                ),
                2
            ) = 1
            AND c.djx_CourseCode13 IN ('CS107', 'CS108')
        )
        OR (
            MOD(
                CAST(
                    SUBSTRING(
                        s.djx_Sno13
                        FROM 8 FOR 1
                    ) AS INTEGER
                ),
                2
            ) = 0
            AND c.djx_CourseCode13 IN ('CS109', 'CS111', 'CS114')
        )
    );

INSERT INTO
    Dengjx_Grades13 (
        djx_EnrollmentId13,
        djx_Score13,
        djx_GradedAt13
    )
SELECT e.djx_EnrollmentId13, 62.00 + MOD(
        s.djx_StudentId13 + a.djx_AssignmentId13, 35
    ), TIMESTAMP '2026-06-20 14:00:00'
FROM
    Dengjx_Enrollments13 e
    JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
WHERE
    e.djx_Status13 = 'COMPLETED';

INSERT INTO
    Dengjx_TeachingEvaluations13 (
        djx_EnrollmentId13,
        djx_Rating13,
        djx_Comment13,
        djx_EvaluatedAt13
    )
SELECT
    e.djx_EnrollmentId13,
    CASE
        WHEN MOD(
            s.djx_StudentId13 + a.djx_AssignmentId13,
            3
        ) = 0 THEN 5
        WHEN MOD(
            s.djx_StudentId13 + a.djx_AssignmentId13,
            3
        ) = 1 THEN 4
        ELSE 3
    END,
    CASE
        WHEN MOD(
            s.djx_StudentId13 + a.djx_AssignmentId13,
            2
        ) = 0 THEN '课程讲解清晰'
        ELSE '课堂互动充分'
    END,
    TIMESTAMP '2026-06-25 10:00:00'
FROM
    Dengjx_Enrollments13 e
    JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
WHERE
    e.djx_Status13 = 'COMPLETED'
    AND s.djx_Sno13 IN (
        '20250001',
        '20250002',
        '20250017',
        '20250018',
        '20250033',
        '20250034'
    );

INSERT INTO
    Dengjx_Users13 (
        djx_Username13,
        djx_Password13,
        djx_Role13,
        djx_StudentId13,
        djx_TeacherId13,
        djx_Enabled13
    )
VALUES (
        'admin',
        '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.',
        'ADMIN',
        NULL,
        NULL,
        TRUE
    );

INSERT INTO
    Dengjx_Users13 (
        djx_Username13,
        djx_Password13,
        djx_Role13,
        djx_StudentId13,
        djx_TeacherId13,
        djx_Enabled13
    )
SELECT LOWER(t.djx_Tno13), '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.', 'TEACHER', NULL, t.djx_TeacherId13, TRUE
FROM Dengjx_Teachers13 t;

INSERT INTO
    Dengjx_Users13 (
        djx_Username13,
        djx_Password13,
        djx_Role13,
        djx_StudentId13,
        djx_TeacherId13,
        djx_Enabled13
    )
SELECT 's' || s.djx_Sno13, '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.', 'STUDENT', s.djx_StudentId13, NULL, TRUE
FROM Dengjx_Students13 s;