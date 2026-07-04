# SQL 执行顺序

1. 使用 openGauss 管理账号执行 `01_create_database.sql`。
2. 连接数据库 `DengjxMIS13`。
3. 依次执行 `02_schema.sql`、`03_views.sql`、`04_triggers.sql`、`05_procedures.sql`、`06_seed_data.sql`。
4. 执行完成后检查表、视图、触发器、存储过程是否存在。

说明：

- `Dengjx_Students13.djx_TotalCredits13` 是课程设计验收用的已修学分缓存字段，由 `04_triggers.sql` 中的成绩触发器自动维护。
- `V_Dengjx_StudentCreditSummary13` 同时返回缓存学分和实时计算学分，便于核对触发器维护结果。
- `Dengjx_TeachingAssignments13` 保存每个开课安排的两个周内上课时间段，星期取值为1到5，对应周一到周五；后端按课程学时解释前半学期和后半学期是否生效。
- `Dengjx_TeachingBuildings13` 和 `Dengjx_Classrooms13` 保存教学楼与教室；`Dengjx_TeachingAssignments13.djx_ClassroomId13` 关联上课地点，学生课表和期末考试默认地点都从这里读取。
- `Dengjx_MajorTransferApplications13` 保存转专业申请和审核结果；审核通过后只更新学生当前班级，不删除选课、成绩和已完成课程。
- `Dengjx_FinalExams13` 按课程、学年、学期唯一保存期末考试时间；同一课程所有教学班共享考试时间，地点按各教学班上课教室展示。
- `Dengjx_TeachingEvaluations13` 保存学生对已完成选课记录的一次性最终评价，`djx_EnrollmentId13` 唯一，评价等级为1到5，理由可为空。
- 当前初始化数据统一使用五个固定上课时间段：`08:00-09:40`、`09:55-11:35`、`13:30-15:10`、`15:25-17:05`、`18:30-20:10`。
- `06_seed_data.sql` 中的用户密码字段为演示用 BCrypt 哈希，实际部署时应通过后端账号管理功能重置密码。
- `06_seed_data.sql` 已集中为软件工程、计算机科学与技术、数据科学与大数据技术三个专业生成 2025 级大一下样例数据，学生入学日期统一为 `2025-09-01`，课程统一位于 `2025-2026` 学年第 2 学期，每个专业至少配置 8 门专业课程，并为部分已完成课程生成样例教学评价、浙工大教学楼/教室和期末考试时间。
