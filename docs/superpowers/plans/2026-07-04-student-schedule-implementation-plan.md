# Student Schedule Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add teaching assignment class times, prevent student schedule conflicts, limit student enrollment/course views to the current term, and add a current-term schedule page split by first and second half of the semester.

**Architecture:** Store up to two weekly time slots on each teaching assignment. Backend derives slot effectiveness from course hours: 64 hours uses both slots all term, 48 hours uses slot one all term and slot two only in the first half, 32 hours uses slot one only in the first half. Student enrollment conflict checks and schedule queries use the same backend schedule expansion logic.

**Tech Stack:** Spring Boot 3, Java 17, MyBatis-Plus, JdbcTemplate SQL, Vue 3, Element Plus, TypeScript.

---

### Task 1: Backend Tests For Schedule Rules

**Files:**
- Modify: `backend/src/test/java/com/dengjx/affairs/BusinessRuleTests.java`
- Create: `backend/src/test/java/com/dengjx/affairs/ScheduleRuleTests.java`

- [ ] **Step 1: Write failing unit tests**

Add tests that verify:
- `Assignment` maps weekday/start/end fields.
- `EnrollmentServiceImpl.mine()` filters to the student's current grade and semester.
- Enrolling in a course fails when an active selected course overlaps the same half-semester time segment.
- Enrolling does not fail when the overlap only exists in a half where one course is inactive.
- `schedule(userId)` returns first-half and second-half weekday rows.

- [ ] **Step 2: Run tests and verify RED**

Run:

```powershell
cd backend
mvn "-Dtest=BusinessRuleTests,ScheduleRuleTests,EntityMappingTests" test
```

Expected: tests fail because schedule fields, conflict checks, and `schedule` API do not exist yet.

### Task 2: Database And Entity Model

**Files:**
- Modify: `design/sql/02_schema.sql`
- Modify: `design/sql/03_views.sql`
- Modify: `design/sql/06_seed_data.sql`
- Modify: `backend/src/main/java/com/dengjx/affairs/entity/Assignment.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/dto/AssignmentRequest.java`

- [ ] **Step 1: Add assignment time columns**

Add nullable columns to `Dengjx_TeachingAssignments13`:
- `djx_WeekdayOne13`, `djx_StartTimeOne13`, `djx_EndTimeOne13`
- `djx_WeekdayTwo13`, `djx_StartTimeTwo13`, `djx_EndTimeTwo13`

Weekday values are 1-5. Time values use SQL `TIME`.

- [ ] **Step 2: Map fields in Java**

Add matching fields to `Assignment` using lowercase `@TableField` names. Add fields to `AssignmentRequest` with `LocalTime` for start/end values.

### Task 3: Assignment Validation

**Files:**
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/AssignmentServiceImpl.java`

- [ ] **Step 1: Validate weekly time slots**

Validate:
- slot one must have weekday/start/end together.
- slot two must have weekday/start/end together.
- weekday must be 1-5.
- start time must be before end time.
- 48-hour and 64-hour courses need slot two for complete schedule display.
- 32-hour courses must have slot one.

- [ ] **Step 2: Apply fields**

Persist both slots from `AssignmentRequest` into `Assignment`.

### Task 4: Student Enrollment Filtering And Conflict Check

**Files:**
- Modify: `backend/src/main/java/com/dengjx/affairs/service/EnrollmentService.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/EnrollmentServiceImpl.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/controller/EnrollmentController.java`

- [ ] **Step 1: Filter current term**

Change `/api/student/enrollments/mine` to return only records where the assignment matches the current student's derived target grade and semester.

- [ ] **Step 2: Add conflict detection**

Before creating/restoring an enrollment, compare the selected assignment's effective slots with all active current-term selected/completed enrollments for the student. Throw `BusinessException("该时间段已有课程，不能重复选课")` when effective segments overlap.

- [ ] **Step 3: Add schedule query**

Add `GET /api/student/schedule` returning:
- `firstHalf`: Monday-Friday grouped schedule rows.
- `secondHalf`: Monday-Friday grouped schedule rows.

Only active `SELECTED` and `COMPLETED` enrollments are included.

### Task 5: Frontend Student Views

**Files:**
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/layouts/MainLayout.vue`
- Modify: `frontend/src/views/student/AvailableCoursesView.vue`
- Modify: `frontend/src/views/student/MyEnrollmentsView.vue`
- Modify: `frontend/src/views/student/MyCoursesView.vue`
- Create: `frontend/src/views/student/MyScheduleView.vue`
- Modify: `frontend/src/utils/formatters.ts`

- [ ] **Step 1: Show class time**

Add schedule time display columns to available courses, my enrollments, and my courses.

- [ ] **Step 2: Add route and menu**

Add `/student/schedule` with label `我的课表`.

- [ ] **Step 3: Build schedule page**

Use Element Plus tabs for `前半学期（第1-8周）` and `后半学期（第9周以后）`, with Monday-Friday columns and no weekend columns.

### Task 6: Frontend Admin Assignment Form

**Files:**
- Modify: `frontend/src/components/crudTypes.ts`
- Modify: `frontend/src/components/CrudPage.vue`
- Modify: `frontend/src/views/admin/adminConfigs.ts`

- [ ] **Step 1: Add time field type**

Support `time` fields in generic CRUD forms using `el-time-picker` and `HH:mm:ss` value format.

- [ ] **Step 2: Add assignment fields**

Expose weekday and time slot fields in the assignment CRUD config.

### Task 7: Docs And Verification

**Files:**
- Modify: `design/docs/接口清单.md`
- Modify: `design/sql/README.md`
- Modify: `README.md` or `角色功能说明.md` if they describe student menu behavior.

- [ ] **Step 1: Update docs**

Document assignment time fields, student schedule API, and current-term behavior for student enrollment/course views.

- [ ] **Step 2: Run backend verification**

Run:

```powershell
cd backend
mvn test
```

- [ ] **Step 3: Run frontend verification**

Run:

```powershell
cd frontend
npm run build
```

