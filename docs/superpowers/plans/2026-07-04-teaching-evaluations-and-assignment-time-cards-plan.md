# Teaching Evaluations And Assignment Time Cards Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add final teaching evaluations for completed courses and make admin assignment time fields adapt to course hours.

**Architecture:** Teaching evaluations are stored once per enrollment, so ownership, completion status, teacher visibility, and admin drill-down all follow the existing enrollment and assignment relationships. The admin assignment form reads course-hour metadata from major-course lookup options and conditionally displays or clears the second time slot.

**Tech Stack:** Spring Boot 3, Java 17, MyBatis-Plus, JdbcTemplate SQL, Vue 3, Element Plus, TypeScript.

---

### Task 1: Backend Evaluation Model And Rules

**Files:**
- Create: `backend/src/main/java/com/dengjx/affairs/entity/TeachingEvaluation.java`
- Create: `backend/src/main/java/com/dengjx/affairs/mapper/TeachingEvaluationMapper.java`
- Create: `backend/src/main/java/com/dengjx/affairs/dto/TeachingEvaluationRequest.java`
- Create: `backend/src/main/java/com/dengjx/affairs/service/TeachingEvaluationService.java`
- Create: `backend/src/main/java/com/dengjx/affairs/service/impl/TeachingEvaluationServiceImpl.java`
- Create: `backend/src/main/java/com/dengjx/affairs/controller/TeachingEvaluationController.java`
- Modify: `backend/src/test/java/com/dengjx/affairs/EntityMappingTests.java`
- Modify: `backend/src/test/java/com/dengjx/affairs/BusinessRuleTests.java`

- [ ] Write failing tests for entity mapping, rating range, completed-only submission, and final non-repeatable submission.
- [ ] Run `cd backend; mvn "-Dtest=BusinessRuleTests,EntityMappingTests" test` and confirm RED.
- [ ] Implement entity, mapper, DTO, service, controller.
- [ ] Re-run the same tests and confirm GREEN.

### Task 2: Database And SQL Contract

**Files:**
- Modify: `design/sql/02_schema.sql`
- Modify: `design/sql/03_views.sql`
- Modify: `design/sql/06_seed_data.sql`
- Modify: `backend/src/test/java/com/dengjx/affairs/SqlInitializationContractTests.java`

- [ ] Write failing SQL contract tests for `Dengjx_TeachingEvaluations13`, the unique enrollment constraint, and seed coverage.
- [ ] Add schema table, indexes, view support, and focused seed evaluations.
- [ ] Re-run SQL contract tests and confirm GREEN.

### Task 3: Frontend Evaluation Pages

**Files:**
- Create: `frontend/src/views/student/TeachingEvaluationsView.vue`
- Create: `frontend/src/views/teacher/TeacherEvaluationsView.vue`
- Create: `frontend/src/views/admin/TeachingEvaluationManagementView.vue`
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/layouts/menuGroups.ts`
- Modify: `frontend/src/utils/formatters.ts`
- Modify: `frontend/test/menuGroups.test.ts`

- [ ] Add menu tests for student, teacher, and admin evaluation routes.
- [ ] Add routes and menu entries.
- [ ] Implement student one-time evaluation submission, teacher assignment evaluation view, and admin teacher-to-assignment drill-down view.
- [ ] Run frontend tests and build.

### Task 4: Dynamic Assignment Time Cards

**Files:**
- Modify: `backend/src/main/java/com/dengjx/affairs/dto/LookupOption.java`
- Modify: `backend/src/main/java/com/dengjx/affairs/service/impl/LookupServiceImpl.java`
- Modify: `backend/src/test/java/com/dengjx/affairs/BusinessRuleTests.java`
- Modify: `frontend/src/components/crudTypes.ts`
- Modify: `frontend/src/components/CrudPage.vue`
- Modify: `frontend/src/views/admin/adminConfigs.ts`

- [ ] Add backend test for major-course lookup hour metadata.
- [ ] Return `hours` metadata from major-course lookup options.
- [ ] Add field visibility and clearing support to generic CRUD form.
- [ ] Configure assignment time groups so 32-hour courses hide slot two and 48/64-hour courses show slot two.
- [ ] Run backend targeted tests and frontend build.

### Task 5: Documentation And Final Verification

**Files:**
- Modify: `design/docs/接口清单.md`
- Modify: `design/sql/README.md`
- Modify: `角色功能说明.md`
- Modify: `README.md` if it mentions role features.

- [ ] Document evaluation APIs, evaluation table, role workflows, and dynamic time-slot behavior.
- [ ] Run `cd backend; mvn test`.
- [ ] Run `cd frontend; npm run build`.
