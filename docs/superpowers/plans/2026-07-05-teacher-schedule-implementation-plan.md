# Teacher Schedule Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a teacher schedule page that mirrors the student schedule grid for the current logged-in teacher's assignments.

**Architecture:** Reuse the existing backend schedule slot rules in `AssignmentScheduleRules` and expose a new read-only `GET /api/teacher/schedule` endpoint from `StatisticsController`. Add a Vue teacher page at `/teacher/schedule`, then register it in router and teacher menu.

**Tech Stack:** Spring Boot, MyBatis Plus, JdbcTemplate, Vue 3, Element Plus, node:test, JUnit 5.

---

### Task 1: Backend Teacher Schedule Contract

**Files:**
- Modify: `backend/src/test/java/com/dengjx/affairs/AcademicFeatureContractTests.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/StatisticsService.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/StatisticsServiceImpl.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/controller/StatisticsController.java`

- [x] Add a failing JUnit test that calls `StatisticsServiceImpl.teacherSchedule(100L)` with a fixed teacher id and one 48-hour assignment row.
- [x] Run `mvn "-Dtest=AcademicFeatureContractTests#teacherScheduleSplitsAssignmentsByHalfTerm" test` from `backend`; expected RED is a compile failure or missing method failure.
- [x] Add `Map<String, Object> teacherSchedule(Long userId)` to `StatisticsService`.
- [x] Implement `StatisticsServiceImpl.teacherSchedule` by querying the teacher's assignments, converting each row to `Assignment`, splitting slots with `AssignmentScheduleRules.effectiveSlots`, and returning `firstHalf` / `secondHalf`.
- [x] Add `GET /api/teacher/schedule` to `StatisticsController`.
- [ ] Re-run the same Maven test; expected PASS. Blocked by sandbox access denial for `C:\Users\Lenovo\.jdks\openjdk-24.0.2\conf\security\java.security`.

### Task 2: Frontend Teacher Schedule Page

**Files:**
- Modify: `frontend/test/menuGroups.test.ts`
- Create: `frontend/src/views/teacher/TeacherScheduleView.vue`
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/layouts/menuGroups.ts`

- [x] Update the teacher menu test to expect `我的课表` under `教学管理`.
- [x] Run `node --test frontend/test/menuGroups.test.ts`; expected RED because the menu item does not exist yet.
- [x] Create `TeacherScheduleView.vue` based on `MyScheduleView.vue`, calling `teacherGet('/schedule')` and showing course, class, course type, and classroom.
- [x] Register `/teacher/schedule` in `frontend/src/router/index.ts`.
- [x] Add `{ index: '/teacher/schedule', label: '我的课表', icon: Calendar }` to the teacher teaching menu.
- [x] Re-run `node --test frontend/test/menuGroups.test.ts`; expected PASS.

### Task 3: Documentation And Verification

**Files:**
- Modify: `design/docs/接口清单.md`
- Modify: `角色功能说明.md`

- [x] Document `GET /api/teacher/schedule` in the Teacher APIs table.
- [x] Update the teacher role/menu description to include `我的课表`.
- [ ] Run the focused backend Maven test and frontend menu test. Frontend passed; backend was blocked by sandbox JDK file access.
- [x] If time allows, run `npm run build` from `frontend` to catch Vue type errors.
