# SQL 执行顺序

1. 使用 openGauss 管理账号执行 `01_create_database.sql`。
2. 连接数据库 `DengjxMIS13`。
3. 依次执行 `02_schema.sql`、`03_views.sql`、`04_triggers.sql`、`05_procedures.sql`、`06_seed_data.sql`。
4. 执行完成后检查表、视图、触发器、存储过程是否存在。

说明：

- `Dengjx_Students13.djx_TotalCredits13` 是课程设计验收用的已修学分缓存字段，由 `04_triggers.sql` 中的成绩触发器自动维护。
- `V_Dengjx_StudentCreditSummary13` 同时返回缓存学分和实时计算学分，便于核对触发器维护结果。
- `06_seed_data.sql` 中的用户密码字段为演示用 BCrypt 哈希，实际部署时应通过后端账号管理功能重置密码。
