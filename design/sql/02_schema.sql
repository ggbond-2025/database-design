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

DROP TABLE IF EXISTS Dengjx_TeachingAssignments13;

DROP TABLE IF EXISTS Dengjx_EnrollmentSettings13;

DROP TABLE IF EXISTS Dengjx_MajorCourses13;

DROP TABLE IF EXISTS Dengjx_Courses13;

DROP TABLE IF EXISTS Dengjx_Teachers13;

DROP TABLE IF EXISTS Dengjx_Students13;

DROP TABLE IF EXISTS Dengjx_Classes13;

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
    -- 学年。
    djx_AcademicYear13 VARCHAR(20) NOT NULL,
    -- 学期，取值为1或2。
    djx_Semester13 INTEGER NOT NULL CHECK (djx_Semester13 IN (1, 2)),
    -- 选课容量。
    djx_Capacity13 INTEGER NOT NULL CHECK (djx_Capacity13 > 0),
    -- 该教学任务是否开放选课。
    djx_EnrollmentOpen13 BOOLEAN NOT NULL DEFAULT FALSE,
    -- 第一个每周上课日，1到5表示周一到周五。
    djx_WeekdayOne13 INTEGER NULL CHECK (djx_WeekdayOne13 BETWEEN 1 AND 5),
    -- 第一个每周上课开始时间。
    djx_StartTimeOne13 TIME NULL,
    -- 第一个每周上课结束时间。
    djx_EndTimeOne13 TIME NULL,
    -- 第二个每周上课日，1到5表示周一到周五。
    djx_WeekdayTwo13 INTEGER NULL CHECK (djx_WeekdayTwo13 BETWEEN 1 AND 5),
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

CREATE INDEX idx_djx_enrollments_student13 ON Dengjx_Enrollments13 (djx_StudentId13);

CREATE INDEX idx_djx_evaluations_rating13 ON Dengjx_TeachingEvaluations13 (djx_Rating13);

CREATE INDEX idx_djx_grades_score13 ON Dengjx_Grades13 (djx_Score13);

CREATE UNIQUE INDEX uk_djx_users_student13 ON Dengjx_Users13 (djx_StudentId13)
WHERE
    djx_StudentId13 IS NOT NULL;

CREATE UNIQUE INDEX uk_djx_users_teacher13 ON Dengjx_Users13 (djx_TeacherId13)
WHERE
    djx_TeacherId13 IS NOT NULL;
