DROP VIEW IF EXISTS V_Dengjx_StudentCreditSummary13;

DROP VIEW IF EXISTS V_Dengjx_RegionStudentCount13;

DROP VIEW IF EXISTS V_Dengjx_CourseAverage13;

DROP VIEW IF EXISTS V_Dengjx_ClassCourses13;

DROP VIEW IF EXISTS V_Dengjx_TeacherAssignments13;

DROP VIEW IF EXISTS V_Dengjx_StudentGrades13;

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
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13,
    a.djx_Capacity13,
    a.djx_EnrollmentOpen13,
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
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13,
    a.djx_Capacity13,
    a.djx_EnrollmentOpen13;

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
    a.djx_AcademicYear13,
    a.djx_Semester13,
    mc.djx_CourseType13,
    mc.djx_TargetGrade13,
    mc.djx_TargetSemester13
FROM
    Dengjx_TeachingAssignments13 a
    JOIN Dengjx_Classes13 cl ON cl.djx_ClassId13 = a.djx_ClassId13
    JOIN Dengjx_Majors13 m ON m.djx_MajorId13 = cl.djx_MajorId13
    JOIN Dengjx_MajorCourses13 mc ON mc.djx_MajorCourseId13 = a.djx_MajorCourseId13
    JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = mc.djx_CourseId13
    JOIN Dengjx_Teachers13 t ON t.djx_TeacherId13 = a.djx_TeacherId13;

CREATE OR REPLACE VIEW V_Dengjx_CourseAverage13 AS
SELECT
    a.djx_AssignmentId13,
    c.djx_CourseId13,
    c.djx_CourseCode13,
    c.djx_CourseName13,
    cl.djx_ClassName13,
    t.djx_Tname13,
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