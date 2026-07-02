DROP TRIGGER IF EXISTS Dengjx_GradesAfterInsertUpdate13 ON Dengjx_Grades13;
DROP TRIGGER IF EXISTS Dengjx_GradesAfterDelete13 ON Dengjx_Grades13;
DROP FUNCTION IF EXISTS Dengjx_GradesAfterInsertUpdateFn13();
DROP FUNCTION IF EXISTS Dengjx_GradesAfterDeleteFn13();
DROP FUNCTION IF EXISTS Dengjx_RecalculateStudentCredits13(BIGINT);

CREATE OR REPLACE FUNCTION Dengjx_RecalculateStudentCredits13(student_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE Dengjx_Students13 s
    SET djx_TotalCredits13 = COALESCE((
        SELECT SUM(c.djx_Credit13)
        FROM Dengjx_Grades13 g
        JOIN Dengjx_Enrollments13 e ON e.djx_EnrollmentId13 = g.djx_EnrollmentId13
        JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
        JOIN Dengjx_Courses13 c ON c.djx_CourseId13 = a.djx_CourseId13
        WHERE e.djx_StudentId13 = student_id
          AND e.djx_Status13 <> 'DROPPED'
          AND g.djx_Score13 >= 60
    ), 0)
    WHERE s.djx_StudentId13 = student_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Dengjx_GradesAfterInsertUpdateFn13()
RETURNS TRIGGER AS $$
DECLARE
    new_student_id BIGINT;
    old_student_id BIGINT;
BEGIN
    SELECT e.djx_StudentId13
    INTO new_student_id
    FROM Dengjx_Enrollments13 e
    WHERE e.djx_EnrollmentId13 = NEW.djx_EnrollmentId13;

    IF TG_OP = 'UPDATE' THEN
        SELECT e.djx_StudentId13
        INTO old_student_id
        FROM Dengjx_Enrollments13 e
        WHERE e.djx_EnrollmentId13 = OLD.djx_EnrollmentId13;

        IF old_student_id IS NOT NULL THEN
            PERFORM Dengjx_RecalculateStudentCredits13(old_student_id);
        END IF;
    END IF;

    IF new_student_id IS NOT NULL THEN
        PERFORM Dengjx_RecalculateStudentCredits13(new_student_id);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Dengjx_GradesAfterDeleteFn13()
RETURNS TRIGGER AS $$
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
$$ LANGUAGE plpgsql;

CREATE TRIGGER Dengjx_GradesAfterInsertUpdate13
AFTER INSERT OR UPDATE ON Dengjx_Grades13
FOR EACH ROW
EXECUTE PROCEDURE Dengjx_GradesAfterInsertUpdateFn13();

CREATE TRIGGER Dengjx_GradesAfterDelete13
AFTER DELETE ON Dengjx_Grades13
FOR EACH ROW
EXECUTE PROCEDURE Dengjx_GradesAfterDeleteFn13();
