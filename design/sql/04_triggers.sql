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