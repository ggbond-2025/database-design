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