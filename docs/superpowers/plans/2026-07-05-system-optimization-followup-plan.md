# System Optimization Follow-up Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Optimize the academic affairs system except for configuration security and environment isolation.

**Architecture:** Keep the current Spring Boot + MyBatis-Plus backend and Vue/Vite frontend structure. Apply targeted fixes around transactional consistency, route/session state, database query support, and repository hygiene without changing unrelated business flows.

**Tech Stack:** Java 17, Spring Boot 3.3.5, MyBatis-Plus, OpenGauss SQL, Vue 3, Pinia, Vue Router, Vite.

---

### Scope

- [ ] Add backend tests before behavior changes.
- [ ] Prevent enrollment capacity overbooking by locking the assignment row during enrollment creation.
- [ ] Validate teacher, class, and classroom schedule conflicts for assignment create/update.
- [ ] Avoid leaking internal exception messages in 500 API responses.
- [ ] Add token version support so password changes and account updates can invalidate old tokens.
- [ ] Refresh frontend auth state through `/api/auth/me` instead of trusting only local storage role data.
- [ ] Pin frontend dependency versions from `package-lock.json`.
- [ ] Add database indexes for common enrollment, assignment, grade, and evaluation queries.
- [ ] Ignore local IDE files and stop tracking existing `backend/.idea` files.

### 2026-07-05 Follow-up Completed

- [x] Split frontend route pages into lazy-loaded chunks while keeping route paths and role guards unchanged.
- [x] Replace full Element Plus plugin registration with explicit component registration for components used in the project.
- [x] Keep Element Plus Chinese locale through the root `el-config-provider`.
- [x] Extract duplicated student/teacher schedule grid building into `ScheduleGridBuilder`.
- [x] Add warn logs when token version lookup or increment falls back because the database schema is not synchronized.

### Verification

- Backend: `mvn test` from `backend`.
- Frontend: `npm run build` from `frontend`.
- Targeted frontend tests are not available as an npm script in the current `package.json`; build is the fastest available check.
- 2026-07-05 follow-up: `mvn test` passed with 80 tests, and `npm run build` passed. Frontend main JS changed from about 1,147.62 kB before route lazy loading to about 500.13 kB after route lazy loading and Element Plus explicit registration; Vite still reports the chunk-size warning because the largest minified chunk is just over 500 kB.
