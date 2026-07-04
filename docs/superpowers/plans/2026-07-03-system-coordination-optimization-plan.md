# 系统协同优化 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 优化高校教务系统中管理员、教师、学生三类功能之间的流程衔接，减少手输编号，形成更清晰的开课、选课、成绩、统计闭环。

**Architecture:** 保持现有 Spring Boot 分层和 Vue 页面结构不变。后端新增 lookup 查询选项接口，为前端下拉选择和跨页面跳转提供统一数据来源；前端在已有页面上增加选择器、快捷动作和状态提示；关键选课、成绩写操作补事务边界。

**Tech Stack:** Java 17、Spring Boot 3、MyBatis-Plus、JdbcTemplate、Vue 3、TypeScript、Element Plus、Pinia、Vue Router、Axios。

---

## File Structure

- Create: `backend/src/main/java/com/dengjx/affairs/dto/LookupOption.java`
- Create: `backend/src/main/java/com/dengjx/affairs/service/LookupService.java`
- Create: `backend/src/main/java/com/dengjx/affairs/service/impl/LookupServiceImpl.java`
- Create: `backend/src/main/java/com/dengjx/affairs/controller/LookupController.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/EnrollmentServiceImpl.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/GradeServiceImpl.java`
- Test: `backend/src/test/java/com/dengjx/affairs/LookupServiceTests.java`
- Modify: `frontend/src/api/modules/admin.ts`
- Modify: `frontend/src/components/crudTypes.ts`
- Modify: `frontend/src/components/CrudPage.vue`
- Modify: `frontend/src/views/admin/adminConfigs.ts`
- Modify: `frontend/src/views/admin/StatisticsView.vue`
- Modify: `frontend/src/views/teacher/TeacherAssignmentsView.vue`
- Modify: `frontend/src/views/teacher/TeacherEnrollmentListView.vue`
- Modify: `frontend/src/views/teacher/TeacherGradeEntryView.vue`
- Modify: `frontend/src/views/teacher/TeacherStatisticsView.vue`
- Modify: `frontend/src/views/student/AvailableCoursesView.vue`
- Modify: `frontend/src/views/student/MyEnrollmentsView.vue`
- Modify: `frontend/src/views/student/MyRankView.vue`
- Modify: `design/docs/接口清单.md`
- Modify: `design/docs/验收说明.md`

## Task 1: Backend Lookup API

- [ ] Add failing tests proving lookup options expose stable `label` and `value`.
- [ ] Add `LookupOption`, `LookupService`, `LookupServiceImpl`, and `LookupController`.
- [ ] Provide admin lookup endpoints for students, teachers, courses, classes, assignments, and active enrollments.
- [ ] Run focused backend tests.

## Task 2: Backend Transaction Boundaries

- [ ] Add `@Transactional` to enrollment create/drop and grade create/update/delete operations.
- [ ] Run business rule tests to verify existing behavior remains unchanged.

## Task 3: Admin Frontend Coordination

- [ ] Extend CRUD field config to support async option loading.
- [ ] Replace ID number inputs in class/student/assignment/enrollment/grade/user forms with lookup-backed selects.
- [ ] Replace manual student/assignment inputs in admin statistics with selectors.
- [ ] Run frontend type build.

## Task 4: Teacher Flow Coordination

- [ ] Add row actions from "我的任课" to "选课名单"、"成绩录入"、"课程统计".
- [ ] Let teacher list, grade entry, and statistics pages read `assignmentId` from route query and auto-load data.
- [ ] Show recorded/unrecorded grade counts after loading grade entry data.
- [ ] Run frontend type build.

## Task 5: Student Flow Coordination

- [ ] Add filters to available courses.
- [ ] Add post-enroll navigation to "我的选课".
- [ ] Add cross-links from my enrollments to courses, grades, credits, and rank.
- [ ] Limit rank choices to active courses and show clear empty-state guidance.
- [ ] Run frontend type build.

## Task 6: Documentation and Verification

- [ ] Update interface and acceptance documentation for lookup endpoints and coordinated flows.
- [ ] Run `mvn test` in `backend`.
- [ ] Run `npm run build` in `frontend`.
