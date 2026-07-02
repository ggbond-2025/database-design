DROP VIEW IF EXISTS V_Dengjx_StudentCreditSummary13;
DROP VIEW IF EXISTS V_Dengjx_RegionStudentCount13;
DROP VIEW IF EXISTS V_Dengjx_CourseAverage13;
DROP VIEW IF EXISTS V_Dengjx_ClassCourses13;
DROP VIEW IF EXISTS V_Dengjx_TeacherAssignments13;
DROP VIEW IF EXISTS V_Dengjx_StudentGrades13;

DROP TABLE IF EXISTS Dengjx_Users13;
DROP TABLE IF EXISTS Dengjx_Grades13;
DROP TABLE IF EXISTS Dengjx_Enrollments13;
DROP TABLE IF EXISTS Dengjx_TeachingAssignments13;
DROP TABLE IF EXISTS Dengjx_Courses13;
DROP TABLE IF EXISTS Dengjx_Teachers13;
DROP TABLE IF EXISTS Dengjx_Students13;
DROP TABLE IF EXISTS Dengjx_Classes13;
DROP TABLE IF EXISTS Dengjx_Majors13;
DROP TABLE IF EXISTS Dengjx_Regions13;

CREATE TABLE Dengjx_Regions13 (
    djx_RegionId13 BIGSERIAL PRIMARY KEY,
    djx_RegionName13 VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Dengjx_Majors13 (
    djx_MajorId13 BIGSERIAL PRIMARY KEY,
    djx_MajorName13 VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Dengjx_Classes13 (
    djx_ClassId13 BIGSERIAL PRIMARY KEY,
    djx_ClassName13 VARCHAR(100) NOT NULL UNIQUE,
    djx_MajorId13 BIGINT NOT NULL REFERENCES Dengjx_Majors13(djx_MajorId13),
    djx_GradeYear13 INTEGER NOT NULL
);

CREATE TABLE Dengjx_Students13 (
    djx_StudentId13 BIGSERIAL PRIMARY KEY,
    djx_Sno13 VARCHAR(32) NOT NULL UNIQUE,
    djx_Sname13 VARCHAR(100) NOT NULL,
    djx_Gender13 VARCHAR(10) NOT NULL CHECK (djx_Gender13 IN ('MALE', 'FEMALE')),
    djx_Age13 INTEGER NOT NULL CHECK (djx_Age13 BETWEEN 15 AND 35),
    djx_ClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13(djx_ClassId13),
    djx_RegionId13 BIGINT NOT NULL REFERENCES Dengjx_Regions13(djx_RegionId13),
    djx_TotalCredits13 NUMERIC(6, 2) NOT NULL DEFAULT 0 CHECK (djx_TotalCredits13 >= 0)
);

CREATE TABLE Dengjx_Teachers13 (
    djx_TeacherId13 BIGSERIAL PRIMARY KEY,
    djx_Tno13 VARCHAR(32) NOT NULL UNIQUE,
    djx_Tname13 VARCHAR(100) NOT NULL,
    djx_Gender13 VARCHAR(10) NOT NULL CHECK (djx_Gender13 IN ('MALE', 'FEMALE')),
    djx_Age13 INTEGER NOT NULL CHECK (djx_Age13 BETWEEN 20 AND 75),
    djx_Title13 VARCHAR(100) NOT NULL,
    djx_Phone13 VARCHAR(30) NOT NULL
);

CREATE TABLE Dengjx_Courses13 (
    djx_CourseId13 BIGSERIAL PRIMARY KEY,
    djx_CourseCode13 VARCHAR(32) NOT NULL UNIQUE,
    djx_CourseName13 VARCHAR(120) NOT NULL,
    djx_Hours13 INTEGER NOT NULL CHECK (djx_Hours13 > 0),
    djx_AssessmentType13 VARCHAR(10) NOT NULL CHECK (djx_AssessmentType13 IN ('EXAM', 'CHECK')),
    djx_Credit13 NUMERIC(4, 1) NOT NULL CHECK (djx_Credit13 > 0)
);

CREATE TABLE Dengjx_TeachingAssignments13 (
    djx_AssignmentId13 BIGSERIAL PRIMARY KEY,
    djx_CourseId13 BIGINT NOT NULL REFERENCES Dengjx_Courses13(djx_CourseId13),
    djx_ClassId13 BIGINT NOT NULL REFERENCES Dengjx_Classes13(djx_ClassId13),
    djx_TeacherId13 BIGINT NOT NULL REFERENCES Dengjx_Teachers13(djx_TeacherId13),
    djx_AcademicYear13 VARCHAR(20) NOT NULL,
    djx_Semester13 INTEGER NOT NULL CHECK (djx_Semester13 IN (1, 2)),
    djx_CourseType13 VARCHAR(20) NOT NULL CHECK (djx_CourseType13 IN ('REQUIRED', 'ELECTIVE')),
    djx_Capacity13 INTEGER NOT NULL CHECK (djx_Capacity13 > 0),
    djx_EnrollmentOpen13 BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Dengjx_Enrollments13 (
    djx_EnrollmentId13 BIGSERIAL PRIMARY KEY,
    djx_StudentId13 BIGINT NOT NULL REFERENCES Dengjx_Students13(djx_StudentId13),
    djx_AssignmentId13 BIGINT NOT NULL REFERENCES Dengjx_TeachingAssignments13(djx_AssignmentId13),
    djx_Status13 VARCHAR(20) NOT NULL DEFAULT 'SELECTED' CHECK (djx_Status13 IN ('SELECTED', 'DROPPED', 'COMPLETED')),
    djx_SelectedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    djx_DroppedAt13 TIMESTAMP NULL,
    UNIQUE (djx_StudentId13, djx_AssignmentId13)
);

CREATE TABLE Dengjx_Grades13 (
    djx_GradeId13 BIGSERIAL PRIMARY KEY,
    djx_EnrollmentId13 BIGINT NOT NULL UNIQUE REFERENCES Dengjx_Enrollments13(djx_EnrollmentId13),
    djx_Score13 NUMERIC(5, 2) NOT NULL CHECK (djx_Score13 BETWEEN 0 AND 100),
    djx_GradedAt13 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Dengjx_Users13 (
    djx_UserId13 BIGSERIAL PRIMARY KEY,
    djx_Username13 VARCHAR(64) NOT NULL UNIQUE,
    djx_Password13 VARCHAR(100) NOT NULL,
    djx_Role13 VARCHAR(20) NOT NULL CHECK (djx_Role13 IN ('ADMIN', 'TEACHER', 'STUDENT')),
    djx_StudentId13 BIGINT NULL REFERENCES Dengjx_Students13(djx_StudentId13),
    djx_TeacherId13 BIGINT NULL REFERENCES Dengjx_Teachers13(djx_TeacherId13),
    djx_Enabled13 BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_djx_students_class13 ON Dengjx_Students13(djx_ClassId13);
CREATE INDEX idx_djx_students_region13 ON Dengjx_Students13(djx_RegionId13);
CREATE INDEX idx_djx_assignments_teacher13 ON Dengjx_TeachingAssignments13(djx_TeacherId13);
CREATE INDEX idx_djx_assignments_class13 ON Dengjx_TeachingAssignments13(djx_ClassId13);
CREATE INDEX idx_djx_enrollments_student13 ON Dengjx_Enrollments13(djx_StudentId13);
CREATE INDEX idx_djx_grades_score13 ON Dengjx_Grades13(djx_Score13);
