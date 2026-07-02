DELETE FROM Dengjx_Users13;
DELETE FROM Dengjx_Grades13;
DELETE FROM Dengjx_Enrollments13;
DELETE FROM Dengjx_TeachingAssignments13;
DELETE FROM Dengjx_Courses13;
DELETE FROM Dengjx_Teachers13;
DELETE FROM Dengjx_Students13;
DELETE FROM Dengjx_Classes13;
DELETE FROM Dengjx_Majors13;
DELETE FROM Dengjx_Regions13;

INSERT INTO Dengjx_Regions13 (djx_RegionName13) VALUES
('湖北省'),
('湖南省'),
('广东省'),
('河南省'),
('四川省'),
('浙江省');

INSERT INTO Dengjx_Majors13 (djx_MajorName13) VALUES
('软件工程'),
('计算机科学与技术'),
('数据科学与大数据技术');

INSERT INTO Dengjx_Classes13 (djx_ClassName13, djx_MajorId13, djx_GradeYear13)
SELECT v.class_name, m.djx_MajorId13, v.grade_year
FROM (VALUES
    ('软件工程2301', '软件工程', 2023),
    ('软件工程2302', '软件工程', 2023),
    ('计科2301', '计算机科学与技术', 2023),
    ('大数据2301', '数据科学与大数据技术', 2023)
) AS v(class_name, major_name, grade_year)
JOIN Dengjx_Majors13 m ON m.djx_MajorName13 = v.major_name;

INSERT INTO Dengjx_Students13 (
    djx_Sno13,
    djx_Sname13,
    djx_Gender13,
    djx_Age13,
    djx_ClassId13,
    djx_RegionId13
)
SELECT v.sno, v.sname, v.gender, v.age, cl.djx_ClassId13, r.djx_RegionId13
FROM (VALUES
    ('20230001', '刘晨', 'MALE', 20, '软件工程2301', '湖北省'),
    ('20230002', '李雪', 'FEMALE', 19, '软件工程2301', '湖南省'),
    ('20230003', '王浩', 'MALE', 20, '软件工程2301', '广东省'),
    ('20230004', '赵敏', 'FEMALE', 19, '软件工程2302', '河南省'),
    ('20230005', '陈杰', 'MALE', 21, '软件工程2302', '四川省'),
    ('20230006', '周怡', 'FEMALE', 20, '软件工程2302', '浙江省'),
    ('20230007', '孙磊', 'MALE', 20, '计科2301', '湖北省'),
    ('20230008', '吴倩', 'FEMALE', 19, '计科2301', '湖南省'),
    ('20230009', '郑宇', 'MALE', 21, '计科2301', '广东省'),
    ('20230010', '何琳', 'FEMALE', 20, '大数据2301', '河南省'),
    ('20230011', '郭强', 'MALE', 20, '大数据2301', '四川省'),
    ('20230012', '唐静', 'FEMALE', 19, '大数据2301', '浙江省')
) AS v(sno, sname, gender, age, class_name, region_name)
JOIN Dengjx_Classes13 cl ON cl.djx_ClassName13 = v.class_name
JOIN Dengjx_Regions13 r ON r.djx_RegionName13 = v.region_name;

INSERT INTO Dengjx_Teachers13 (
    djx_Tno13,
    djx_Tname13,
    djx_Gender13,
    djx_Age13,
    djx_Title13,
    djx_Phone13
) VALUES
('T2023001', '张建国', 'MALE', 45, '教授', '13800000001'),
('T2023002', '李芳', 'FEMALE', 39, '副教授', '13800000002'),
('T2023003', '王磊', 'MALE', 36, '讲师', '13800000003'),
('T2023004', '陈静', 'FEMALE', 42, '副教授', '13800000004'),
('T2023005', '刘洋', 'MALE', 33, '讲师', '13800000005');

INSERT INTO Dengjx_Courses13 (
    djx_CourseCode13,
    djx_CourseName13,
    djx_Hours13,
    djx_AssessmentType13,
    djx_Credit13
) VALUES
('CS101', '数据库原理', 64, 'EXAM', 4.0),
('CS102', 'Java程序设计', 64, 'EXAM', 4.0),
('CS103', '数据结构', 64, 'EXAM', 4.0),
('CS104', '操作系统', 64, 'EXAM', 4.0),
('CS105', '计算机网络', 48, 'EXAM', 3.0),
('CS106', '软件工程', 48, 'CHECK', 3.0),
('CS107', 'Web前端开发', 48, 'CHECK', 2.5),
('CS108', '人工智能导论', 32, 'CHECK', 2.0);

INSERT INTO Dengjx_TeachingAssignments13 (
    djx_CourseId13,
    djx_ClassId13,
    djx_TeacherId13,
    djx_AcademicYear13,
    djx_Semester13,
    djx_CourseType13,
    djx_Capacity13,
    djx_EnrollmentOpen13
)
SELECT c.djx_CourseId13,
       cl.djx_ClassId13,
       t.djx_TeacherId13,
       v.academic_year,
       v.semester,
       v.course_type,
       v.capacity,
       v.enrollment_open
FROM (VALUES
    ('CS101', '软件工程2301', 'T2023001', '2023-2024', 1, 'REQUIRED', 45, FALSE),
    ('CS101', '软件工程2302', 'T2023001', '2023-2024', 1, 'REQUIRED', 45, FALSE),
    ('CS102', '软件工程2301', 'T2023002', '2023-2024', 2, 'REQUIRED', 45, FALSE),
    ('CS103', '计科2301', 'T2023003', '2023-2024', 1, 'REQUIRED', 45, FALSE),
    ('CS104', '计科2301', 'T2023004', '2023-2024', 2, 'REQUIRED', 45, FALSE),
    ('CS105', '大数据2301', 'T2023005', '2023-2024', 1, 'REQUIRED', 45, FALSE),
    ('CS106', '大数据2301', 'T2023002', '2023-2024', 2, 'REQUIRED', 45, FALSE),
    ('CS107', '软件工程2301', 'T2023003', '2023-2024', 2, 'ELECTIVE', 30, TRUE),
    ('CS107', '软件工程2302', 'T2023003', '2023-2024', 2, 'ELECTIVE', 30, TRUE),
    ('CS108', '计科2301', 'T2023004', '2023-2024', 2, 'ELECTIVE', 30, TRUE)
) AS v(course_code, class_name, teacher_no, academic_year, semester, course_type, capacity, enrollment_open)
JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code
JOIN Dengjx_Classes13 cl ON cl.djx_ClassName13 = v.class_name
JOIN Dengjx_Teachers13 t ON t.djx_Tno13 = v.teacher_no;

INSERT INTO Dengjx_Enrollments13 (
    djx_StudentId13,
    djx_AssignmentId13,
    djx_Status13,
    djx_SelectedAt13,
    djx_DroppedAt13
)
SELECT s.djx_StudentId13,
       a.djx_AssignmentId13,
       v.status,
       CAST(v.selected_at AS TIMESTAMP),
       CASE WHEN v.dropped_at IS NULL THEN NULL ELSE CAST(v.dropped_at AS TIMESTAMP) END
FROM (VALUES
    ('20230001', 'CS101', '软件工程2301', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:10:00', NULL),
    ('20230002', 'CS101', '软件工程2301', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:12:00', NULL),
    ('20230003', 'CS101', '软件工程2301', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:15:00', NULL),
    ('20230004', 'CS101', '软件工程2302', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:20:00', NULL),
    ('20230005', 'CS101', '软件工程2302', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:22:00', NULL),
    ('20230006', 'CS101', '软件工程2302', '2023-2024', 1, 'COMPLETED', '2023-09-05 08:25:00', NULL),
    ('20230007', 'CS103', '计科2301', '2023-2024', 1, 'COMPLETED', '2023-09-06 09:00:00', NULL),
    ('20230008', 'CS103', '计科2301', '2023-2024', 1, 'COMPLETED', '2023-09-06 09:02:00', NULL),
    ('20230009', 'CS103', '计科2301', '2023-2024', 1, 'COMPLETED', '2023-09-06 09:04:00', NULL),
    ('20230010', 'CS105', '大数据2301', '2023-2024', 1, 'COMPLETED', '2023-09-07 10:00:00', NULL),
    ('20230011', 'CS105', '大数据2301', '2023-2024', 1, 'COMPLETED', '2023-09-07 10:05:00', NULL),
    ('20230012', 'CS105', '大数据2301', '2023-2024', 1, 'COMPLETED', '2023-09-07 10:08:00', NULL),
    ('20230001', 'CS107', '软件工程2301', '2023-2024', 2, 'SELECTED', '2024-02-20 08:30:00', NULL),
    ('20230002', 'CS107', '软件工程2301', '2023-2024', 2, 'SELECTED', '2024-02-20 08:35:00', NULL),
    ('20230003', 'CS107', '软件工程2301', '2023-2024', 2, 'DROPPED', '2024-02-20 08:40:00', '2024-03-01 09:00:00'),
    ('20230007', 'CS108', '计科2301', '2023-2024', 2, 'SELECTED', '2024-02-21 09:10:00', NULL),
    ('20230008', 'CS108', '计科2301', '2023-2024', 2, 'SELECTED', '2024-02-21 09:15:00', NULL)
) AS v(sno, course_code, class_name, academic_year, semester, status, selected_at, dropped_at)
JOIN Dengjx_Students13 s ON s.djx_Sno13 = v.sno
JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code
JOIN Dengjx_Classes13 cl ON cl.djx_ClassName13 = v.class_name
JOIN Dengjx_TeachingAssignments13 a ON a.djx_CourseId13 = c.djx_CourseId13
    AND a.djx_ClassId13 = cl.djx_ClassId13
    AND a.djx_AcademicYear13 = v.academic_year
    AND a.djx_Semester13 = v.semester;

INSERT INTO Dengjx_Grades13 (
    djx_EnrollmentId13,
    djx_Score13,
    djx_GradedAt13
)
SELECT e.djx_EnrollmentId13,
       v.score,
       CAST(v.graded_at AS TIMESTAMP)
FROM (VALUES
    ('20230001', 'CS101', '软件工程2301', '2023-2024', 1, 85.00, '2024-01-10 14:00:00'),
    ('20230002', 'CS101', '软件工程2301', '2023-2024', 1, 72.00, '2024-01-10 14:05:00'),
    ('20230003', 'CS101', '软件工程2301', '2023-2024', 1, 58.00, '2024-01-10 14:10:00'),
    ('20230004', 'CS101', '软件工程2302', '2023-2024', 1, 91.00, '2024-01-10 14:15:00'),
    ('20230005', 'CS101', '软件工程2302', '2023-2024', 1, 67.00, '2024-01-10 14:20:00'),
    ('20230006', 'CS101', '软件工程2302', '2023-2024', 1, 76.00, '2024-01-10 14:25:00'),
    ('20230007', 'CS103', '计科2301', '2023-2024', 1, 88.00, '2024-01-11 15:00:00'),
    ('20230008', 'CS103', '计科2301', '2023-2024', 1, 94.00, '2024-01-11 15:05:00'),
    ('20230009', 'CS103', '计科2301', '2023-2024', 1, 52.00, '2024-01-11 15:10:00'),
    ('20230010', 'CS105', '大数据2301', '2023-2024', 1, 81.00, '2024-01-12 16:00:00'),
    ('20230011', 'CS105', '大数据2301', '2023-2024', 1, 63.00, '2024-01-12 16:05:00'),
    ('20230012', 'CS105', '大数据2301', '2023-2024', 1, 47.00, '2024-01-12 16:10:00')
) AS v(sno, course_code, class_name, academic_year, semester, score, graded_at)
JOIN Dengjx_Students13 s ON s.djx_Sno13 = v.sno
JOIN Dengjx_Courses13 c ON c.djx_CourseCode13 = v.course_code
JOIN Dengjx_Classes13 cl ON cl.djx_ClassName13 = v.class_name
JOIN Dengjx_TeachingAssignments13 a ON a.djx_CourseId13 = c.djx_CourseId13
    AND a.djx_ClassId13 = cl.djx_ClassId13
    AND a.djx_AcademicYear13 = v.academic_year
    AND a.djx_Semester13 = v.semester
JOIN Dengjx_Enrollments13 e ON e.djx_StudentId13 = s.djx_StudentId13
    AND e.djx_AssignmentId13 = a.djx_AssignmentId13;

INSERT INTO Dengjx_Users13 (
    djx_Username13,
    djx_Password13,
    djx_Role13,
    djx_StudentId13,
    djx_TeacherId13,
    djx_Enabled13
) VALUES
('admin', '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.', 'ADMIN', NULL, NULL, TRUE);

INSERT INTO Dengjx_Users13 (
    djx_Username13,
    djx_Password13,
    djx_Role13,
    djx_StudentId13,
    djx_TeacherId13,
    djx_Enabled13
)
SELECT LOWER(t.djx_Tno13),
       '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.',
       'TEACHER',
       NULL,
       t.djx_TeacherId13,
       TRUE
FROM Dengjx_Teachers13 t;

INSERT INTO Dengjx_Users13 (
    djx_Username13,
    djx_Password13,
    djx_Role13,
    djx_StudentId13,
    djx_TeacherId13,
    djx_Enabled13
)
SELECT 's' || s.djx_Sno13,
       '$2b$10$TMD1kp3HsH/jVG1f0sMtsucCelyXzEHe/VElLNRP3udIwGTMgXud.',
       'STUDENT',
       s.djx_StudentId13,
       NULL,
       TRUE
FROM Dengjx_Students13 s;
