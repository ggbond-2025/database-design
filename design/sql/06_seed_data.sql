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
        VALUES ('软件工程2501', '软件工程'),
            ('软件工程2502', '软件工程'),
            ('计科2501', '计算机科学与技术'),
            ('计科2502', '计算机科学与技术'),
            ('大数据2501', '数据科学与大数据技术'),
            ('大数据2502', '数据科学与大数据技术')
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
SELECT
    b.djx_BuildingId13,
    v.classroom_name,
    v.capacity
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
    ) AS v (building_name, classroom_name, capacity)
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
        VALUES ('20250001', '刘晨', 'MALE', 18, '软件工程2501', '湖北省'),
            ('20250002', '李雪', 'FEMALE', 18, '软件工程2501', '湖南省'),
            ('20250003', '王浩', 'MALE', 19, '软件工程2501', '广东省'),
            ('20250004', '赵敏', 'FEMALE', 18, '软件工程2501', '河南省'),
            ('20250005', '陈杰', 'MALE', 19, '软件工程2501', '四川省'),
            ('20250006', '周怡', 'FEMALE', 18, '软件工程2501', '浙江省'),
            ('20250007', '邓超', 'MALE', 18, '软件工程2501', '江苏省'),
            ('20250008', '林悦', 'FEMALE', 19, '软件工程2501', '山东省'),
            ('20250009', '马骏', 'MALE', 18, '软件工程2502', '福建省'),
            ('20250010', '高雅', 'FEMALE', 18, '软件工程2502', '安徽省'),
            ('20250011', '罗成', 'MALE', 19, '软件工程2502', '湖北省'),
            ('20250012', '许宁', 'FEMALE', 18, '软件工程2502', '浙江省'),
            ('20250013', '程远', 'MALE', 18, '软件工程2502', '江苏省'),
            ('20250014', '姚瑶', 'FEMALE', 19, '软件工程2502', '山东省'),
            ('20250015', '蔡明', 'MALE', 18, '软件工程2502', '广东省'),
            ('20250016', '宋佳', 'FEMALE', 18, '软件工程2502', '河南省'),
            ('20250017', '孙磊', 'MALE', 18, '计科2501', '湖北省'),
            ('20250018', '吴倩', 'FEMALE', 18, '计科2501', '湖南省'),
            ('20250019', '郑宇', 'MALE', 19, '计科2501', '广东省'),
            ('20250020', '何琳', 'FEMALE', 18, '计科2501', '河南省'),
            ('20250021', '郭强', 'MALE', 18, '计科2501', '四川省'),
            ('20250022', '唐静', 'FEMALE', 19, '计科2501', '浙江省'),
            ('20250023', '袁博', 'MALE', 18, '计科2501', '江苏省'),
            ('20250024', '韩梅', 'FEMALE', 18, '计科2501', '山东省'),
            ('20250025', '沈航', 'MALE', 19, '计科2502', '福建省'),
            ('20250026', '叶琳', 'FEMALE', 18, '计科2502', '安徽省'),
            ('20250027', '许晨', 'MALE', 18, '计科2502', '湖北省'),
            ('20250028', '秦月', 'FEMALE', 19, '计科2502', '浙江省'),
            ('20250029', '邵峰', 'MALE', 18, '计科2502', '江苏省'),
            ('20250030', '白璐', 'FEMALE', 18, '计科2502', '山东省'),
            ('20250031', '尹航', 'MALE', 19, '计科2502', '广东省'),
            ('20250032', '贺敏', 'FEMALE', 18, '计科2502', '河南省'),
            ('20250033', '顾然', 'MALE', 18, '大数据2501', '湖北省'),
            ('20250034', '蒋怡', 'FEMALE', 18, '大数据2501', '湖南省'),
            ('20250035', '傅强', 'MALE', 19, '大数据2501', '广东省'),
            ('20250036', '潘悦', 'FEMALE', 18, '大数据2501', '河南省'),
            ('20250037', '余洋', 'MALE', 18, '大数据2501', '四川省'),
            ('20250038', '戴宁', 'FEMALE', 19, '大数据2501', '浙江省'),
            ('20250039', '孟涛', 'MALE', 18, '大数据2501', '江苏省'),
            ('20250040', '卢佳', 'FEMALE', 18, '大数据2501', '山东省'),
            ('20250041', '曾越', 'MALE', 19, '大数据2502', '福建省'),
            ('20250042', '夏雨', 'FEMALE', 18, '大数据2502', '安徽省'),
            ('20250043', '范哲', 'MALE', 18, '大数据2502', '湖北省'),
            ('20250044', '陆瑶', 'FEMALE', 19, '大数据2502', '浙江省'),
            ('20250045', '田浩', 'MALE', 18, '大数据2502', '江苏省'),
            ('20250046', '钟敏', 'FEMALE', 18, '大数据2502', '山东省'),
            ('20250047', '汪博', 'MALE', 19, '大数据2502', '广东省'),
            ('20250048', '施琪', 'FEMALE', 18, '大数据2502', '河南省')
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
            ('计算机科学与技术', 'CS101', 'REQUIRED'),
            ('计算机科学与技术', 'CS103', 'REQUIRED'),
            ('计算机科学与技术', 'CS104', 'REQUIRED'),
            ('计算机科学与技术', 'CS110', 'REQUIRED'),
            ('计算机科学与技术', 'CS108', 'ELECTIVE'),
            ('计算机科学与技术', 'CS111', 'ELECTIVE'),
            ('计算机科学与技术', 'CS113', 'ELECTIVE'),
            ('计算机科学与技术', 'CS116', 'ELECTIVE'),
            ('数据科学与大数据技术', 'CS101', 'REQUIRED'),
            ('数据科学与大数据技术', 'CS103', 'REQUIRED'),
            ('数据科学与大数据技术', 'CS105', 'REQUIRED'),
            ('数据科学与大数据技术', 'CS106', 'REQUIRED'),
            ('数据科学与大数据技术', 'CS108', 'ELECTIVE'),
            ('数据科学与大数据技术', 'CS109', 'ELECTIVE'),
            ('数据科学与大数据技术', 'CS113', 'ELECTIVE'),
            ('数据科学与大数据技术', 'CS116', 'ELECTIVE')
    ) AS v (major_name, course_code, course_type)
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
SELECT
    mc.djx_MajorCourseId13,
    cl.djx_ClassId13,
    t.djx_TeacherId13,
    cr.djx_ClassroomId13,
    '2025-2026',
    2,
    v.capacity,
    v.enrollment_open,
    v.weekday_one,
    v.start_time_one,
    v.end_time_one,
    v.weekday_two,
    v.start_time_two,
    v.end_time_two
FROM (
        VALUES ('软件工程2501', '软件工程', 'CS102', 'T2023001', 60, FALSE, 1, TIME '08:00:00', TIME '09:40:00', 3, TIME '08:00:00', TIME '09:40:00'),
            ('软件工程2501', '软件工程', 'CS101', 'T2023002', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('软件工程2501', '软件工程', 'CS103', 'T2023003', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('软件工程2501', '软件工程', 'CS106', 'T2023004', 60, FALSE, 2, TIME '09:55:00', TIME '11:35:00', 4, TIME '09:55:00', TIME '11:35:00'),
            ('软件工程2501', '软件工程', 'CS107', 'T2023005', 40, TRUE, 5, TIME '13:30:00', TIME '15:10:00', 3, TIME '15:25:00', TIME '17:05:00'),
            ('软件工程2501', '软件工程', 'CS112', 'T2023006', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00'),
            ('软件工程2501', '软件工程', 'CS111', 'T2023007', 40, TRUE, 5, TIME '09:55:00', TIME '11:35:00', NULL, NULL, NULL),
            ('软件工程2501', '软件工程', 'CS114', 'T2023008', 40, TRUE, 1, TIME '18:30:00', TIME '20:10:00', 4, TIME '15:25:00', TIME '17:05:00'),
            ('软件工程2502', '软件工程', 'CS102', 'T2023003', 60, FALSE, 1, TIME '08:00:00', TIME '09:40:00', 3, TIME '08:00:00', TIME '09:40:00'),
            ('软件工程2502', '软件工程', 'CS101', 'T2023004', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('软件工程2502', '软件工程', 'CS103', 'T2023005', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('软件工程2502', '软件工程', 'CS106', 'T2023006', 60, FALSE, 2, TIME '09:55:00', TIME '11:35:00', 4, TIME '09:55:00', TIME '11:35:00'),
            ('软件工程2502', '软件工程', 'CS107', 'T2023007', 40, TRUE, 5, TIME '13:30:00', TIME '15:10:00', 3, TIME '15:25:00', TIME '17:05:00'),
            ('软件工程2502', '软件工程', 'CS112', 'T2023008', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00'),
            ('软件工程2502', '软件工程', 'CS111', 'T2023001', 40, TRUE, 5, TIME '09:55:00', TIME '11:35:00', NULL, NULL, NULL),
            ('软件工程2502', '软件工程', 'CS114', 'T2023002', 40, TRUE, 1, TIME '18:30:00', TIME '20:10:00', 4, TIME '15:25:00', TIME '17:05:00'),
            ('计科2501', '计算机科学与技术', 'CS101', 'T2023003', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('计科2501', '计算机科学与技术', 'CS103', 'T2023004', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('计科2501', '计算机科学与技术', 'CS104', 'T2023005', 60, FALSE, 1, TIME '09:55:00', TIME '11:35:00', 3, TIME '08:00:00', TIME '09:40:00'),
            ('计科2501', '计算机科学与技术', 'CS110', 'T2023006', 60, FALSE, 2, TIME '13:30:00', TIME '15:10:00', 4, TIME '13:30:00', TIME '15:10:00'),
            ('计科2501', '计算机科学与技术', 'CS108', 'T2023007', 40, TRUE, 5, TIME '08:00:00', TIME '09:40:00', NULL, NULL, NULL),
            ('计科2501', '计算机科学与技术', 'CS111', 'T2023008', 40, TRUE, 5, TIME '09:55:00', TIME '11:35:00', NULL, NULL, NULL),
            ('计科2501', '计算机科学与技术', 'CS113', 'T2023001', 40, TRUE, 3, TIME '18:30:00', TIME '20:10:00', 5, TIME '18:30:00', TIME '20:10:00'),
            ('计科2501', '计算机科学与技术', 'CS116', 'T2023002', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00'),
            ('计科2502', '计算机科学与技术', 'CS101', 'T2023005', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('计科2502', '计算机科学与技术', 'CS103', 'T2023006', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('计科2502', '计算机科学与技术', 'CS104', 'T2023007', 60, FALSE, 1, TIME '09:55:00', TIME '11:35:00', 3, TIME '08:00:00', TIME '09:40:00'),
            ('计科2502', '计算机科学与技术', 'CS110', 'T2023008', 60, FALSE, 2, TIME '13:30:00', TIME '15:10:00', 4, TIME '13:30:00', TIME '15:10:00'),
            ('计科2502', '计算机科学与技术', 'CS108', 'T2023001', 40, TRUE, 5, TIME '08:00:00', TIME '09:40:00', NULL, NULL, NULL),
            ('计科2502', '计算机科学与技术', 'CS111', 'T2023002', 40, TRUE, 5, TIME '09:55:00', TIME '11:35:00', NULL, NULL, NULL),
            ('计科2502', '计算机科学与技术', 'CS113', 'T2023003', 40, TRUE, 3, TIME '18:30:00', TIME '20:10:00', 5, TIME '18:30:00', TIME '20:10:00'),
            ('计科2502', '计算机科学与技术', 'CS116', 'T2023004', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS101', 'T2023006', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS103', 'T2023007', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS105', 'T2023007', 60, FALSE, 1, TIME '13:30:00', TIME '15:10:00', 3, TIME '13:30:00', TIME '15:10:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS106', 'T2023008', 60, FALSE, 2, TIME '09:55:00', TIME '11:35:00', 4, TIME '09:55:00', TIME '11:35:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS108', 'T2023002', 40, TRUE, 5, TIME '08:00:00', TIME '09:40:00', NULL, NULL, NULL),
            ('大数据2501', '数据科学与大数据技术', 'CS109', 'T2023002', 40, TRUE, 2, TIME '15:25:00', TIME '17:05:00', 4, TIME '18:30:00', TIME '20:10:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS113', 'T2023004', 40, TRUE, 3, TIME '18:30:00', TIME '20:10:00', 5, TIME '18:30:00', TIME '20:10:00'),
            ('大数据2501', '数据科学与大数据技术', 'CS116', 'T2023005', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS101', 'T2023007', 60, FALSE, 1, TIME '15:25:00', TIME '17:05:00', 4, TIME '08:00:00', TIME '09:40:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS103', 'T2023008', 60, FALSE, 2, TIME '08:00:00', TIME '09:40:00', 3, TIME '09:55:00', TIME '11:35:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS105', 'T2023001', 60, FALSE, 1, TIME '13:30:00', TIME '15:10:00', 3, TIME '13:30:00', TIME '15:10:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS106', 'T2023002', 60, FALSE, 2, TIME '09:55:00', TIME '11:35:00', 4, TIME '09:55:00', TIME '11:35:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS108', 'T2023003', 40, TRUE, 5, TIME '08:00:00', TIME '09:40:00', NULL, NULL, NULL),
            ('大数据2502', '数据科学与大数据技术', 'CS109', 'T2023004', 40, TRUE, 2, TIME '15:25:00', TIME '17:05:00', 4, TIME '18:30:00', TIME '20:10:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS113', 'T2023005', 40, TRUE, 3, TIME '18:30:00', TIME '20:10:00', 5, TIME '18:30:00', TIME '20:10:00'),
            ('大数据2502', '数据科学与大数据技术', 'CS116', 'T2023007', 40, TRUE, 2, TIME '18:30:00', TIME '20:10:00', 5, TIME '15:25:00', TIME '17:05:00')
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
SELECT
    c.djx_CourseId13,
    '2025-2026',
    2,
    v.exam_time
FROM (
        VALUES ('CS101', TIMESTAMP '2026-06-22 09:00:00'),
            ('CS102', TIMESTAMP '2026-06-23 09:00:00'),
            ('CS103', TIMESTAMP '2026-06-24 14:00:00'),
            ('CS104', TIMESTAMP '2026-06-25 09:00:00'),
            ('CS105', TIMESTAMP '2026-06-25 14:00:00'),
            ('CS106', TIMESTAMP '2026-06-26 09:00:00'),
            ('CS107', TIMESTAMP '2026-06-29 09:00:00'),
            ('CS108', TIMESTAMP '2026-06-29 14:00:00'),
            ('CS109', TIMESTAMP '2026-06-30 09:00:00'),
            ('CS110', TIMESTAMP '2026-06-30 14:00:00'),
            ('CS111', TIMESTAMP '2026-07-01 09:00:00'),
            ('CS112', TIMESTAMP '2026-07-01 14:00:00'),
            ('CS113', TIMESTAMP '2026-07-02 09:00:00'),
            ('CS114', TIMESTAMP '2026-07-02 14:00:00'),
            ('CS116', TIMESTAMP '2026-07-03 09:00:00')
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
SELECT
    s.djx_StudentId13,
    a.djx_AssignmentId13,
    'COMPLETED',
    TIMESTAMP '2026-02-24 08:30:00',
    NULL
FROM Dengjx_Students13 s
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
SELECT
    s.djx_StudentId13,
    a.djx_AssignmentId13,
    'SELECTED',
    TIMESTAMP '2026-02-25 10:00:00',
    NULL
FROM Dengjx_Students13 s
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
            MOD(CAST(SUBSTRING(s.djx_Sno13 FROM 8 FOR 1) AS INTEGER), 2) = 1
            AND c.djx_CourseCode13 IN ('CS107', 'CS108')
        )
        OR (
            MOD(CAST(SUBSTRING(s.djx_Sno13 FROM 8 FOR 1) AS INTEGER), 2) = 0
            AND c.djx_CourseCode13 IN ('CS109', 'CS111', 'CS114')
        )
    );

INSERT INTO
    Dengjx_Grades13 (
        djx_EnrollmentId13,
        djx_Score13,
        djx_GradedAt13
    )
SELECT
    e.djx_EnrollmentId13,
    62.00 + MOD(s.djx_StudentId13 + a.djx_AssignmentId13, 35),
    TIMESTAMP '2026-06-20 14:00:00'
FROM Dengjx_Enrollments13 e
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
        WHEN MOD(s.djx_StudentId13 + a.djx_AssignmentId13, 3) = 0 THEN 5
        WHEN MOD(s.djx_StudentId13 + a.djx_AssignmentId13, 3) = 1 THEN 4
        ELSE 3
    END,
    CASE
        WHEN MOD(s.djx_StudentId13 + a.djx_AssignmentId13, 2) = 0 THEN '课程讲解清晰'
        ELSE '课堂互动充分'
    END,
    TIMESTAMP '2026-06-25 10:00:00'
FROM Dengjx_Enrollments13 e
    JOIN Dengjx_Students13 s ON s.djx_StudentId13 = e.djx_StudentId13
    JOIN Dengjx_TeachingAssignments13 a ON a.djx_AssignmentId13 = e.djx_AssignmentId13
WHERE
    e.djx_Status13 = 'COMPLETED'
    AND s.djx_Sno13 IN ('20250001', '20250002', '20250017', '20250018', '20250033', '20250034');

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
