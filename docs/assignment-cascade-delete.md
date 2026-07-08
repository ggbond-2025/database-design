# 开课安排级联删除说明

## 问题原因

`Dengjx_TeachingAssignments13` 是开课安排主表，`Dengjx_Enrollments13` 通过 `djx_AssignmentId13` 引用它。成绩表 `Dengjx_Grades13` 和教学评价表 `Dengjx_TeachingEvaluations13` 又通过 `djx_EnrollmentId13` 引用选课记录。

如果直接删除开课安排，数据库外键会阻止删除，导致管理员端删除失败。

## 处理方式

后端在 `AssignmentServiceImpl.delete` 中按依赖关系做 Service 层级联删除，并使用事务保证一致性：

1. 查询该开课安排下的选课记录 ID。
2. 删除对应教学评价。
3. 删除对应成绩。
4. 删除对应选课记录。
5. 删除开课安排。

## 选择理由

当前项目已有 `StudentServiceImpl.delete` 的 Service 层级联删除模式。继续沿用该模式，比直接修改数据库外键为 `ON DELETE CASCADE` 更符合现有代码风格，也能让业务删除范围在代码中显式可见。

## 验证

- `mvn "-Dtest=BusinessRuleTests,TransactionBoundaryTests" test`
