# 高校教务系统 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个满足数据库课程设计要求的高校教务系统，包含 openGauss 数据库脚本、Spring Boot 后端、Vue 管理端前端，以及管理员、教师、学生三类用户闭环。

**Architecture:** 项目采用前后端分离架构。`design/sql` 存放 openGauss 建库、建表、视图、触发器、存储过程和初始化数据脚本；`backend` 提供 Spring Boot REST API、JWT 登录和接口级权限；`frontend` 提供 Vue 3 + Element Plus 管理端页面。

**Tech Stack:** openGauss、Java 17、Spring Boot 3、MyBatis-Plus、Spring Security、JWT、Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router、Axios。

---

## File Structure

需要创建或修改的主要文件如下。

### Database

- Create: `design/sql/01_create_database.sql`
- Create: `design/sql/02_schema.sql`
- Create: `design/sql/03_views.sql`
- Create: `design/sql/04_triggers.sql`
- Create: `design/sql/05_procedures.sql`
- Create: `design/sql/06_seed_data.sql`
- Create: `design/sql/README.md`

### Backend

- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/dengjx/affairs/AcademicAffairsApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create package: `backend/src/main/java/com/dengjx/affairs/common`
- Create package: `backend/src/main/java/com/dengjx/affairs/security`
- Create package: `backend/src/main/java/com/dengjx/affairs/auth`
- Create package: `backend/src/main/java/com/dengjx/affairs/region`
- Create package: `backend/src/main/java/com/dengjx/affairs/major`
- Create package: `backend/src/main/java/com/dengjx/affairs/schoolclass`
- Create package: `backend/src/main/java/com/dengjx/affairs/student`
- Create package: `backend/src/main/java/com/dengjx/affairs/teacher`
- Create package: `backend/src/main/java/com/dengjx/affairs/course`
- Create package: `backend/src/main/java/com/dengjx/affairs/assignment`
- Create package: `backend/src/main/java/com/dengjx/affairs/enrollment`
- Create package: `backend/src/main/java/com/dengjx/affairs/grade`
- Create package: `backend/src/main/java/com/dengjx/affairs/statistics`
- Create: `backend/README.md`

### Frontend

- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.ts`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.ts`
- Create: `frontend/src/stores/auth.ts`
- Create: `frontend/src/api/http.ts`
- Create: `frontend/src/api/modules/*.ts`
- Create: `frontend/src/layouts/MainLayout.vue`
- Create: `frontend/src/views/login/LoginView.vue`
- Create admin views under: `frontend/src/views/admin`
- Create teacher views under: `frontend/src/views/teacher`
- Create student views under: `frontend/src/views/student`
- Create shared components under: `frontend/src/components`
- Create: `frontend/README.md`

### Documentation

- Modify: `docs/superpowers/specs/2026-07-02-高校教务系统-design.md` only when design changes are approved.
- Create: `design/docs/验收说明.md`
- Create: `design/docs/接口清单.md`

---

## Task 1: Database SQL Scripts

**Files:**

- Create: `design/sql/01_create_database.sql`
- Create: `design/sql/02_schema.sql`
- Create: `design/sql/03_views.sql`
- Create: `design/sql/04_triggers.sql`
- Create: `design/sql/05_procedures.sql`
- Create: `design/sql/06_seed_data.sql`
- Create: `design/sql/README.md`

- [ ] **Step 1: Create database script**

Write `design/sql/01_create_database.sql`:

```sql
DROP DATABASE IF EXISTS DengjxMIS13;
CREATE DATABASE DengjxMIS13 DBCOMPATIBILITY = 'PG';
```

- [ ] **Step 2: Create schema script with all base tables**

Write `design/sql/02_schema.sql` with these tables and constraints:

```text
Dengjx_Regions13:
  djx_RegionId13 primary key
  djx_RegionName13 unique not null

Dengjx_Majors13:
  djx_MajorId13 primary key
  djx_MajorName13 unique not null

Dengjx_Classes13:
  djx_ClassId13 primary key
  djx_ClassName13 unique not null
  djx_MajorId13 references Dengjx_Majors13
  djx_GradeYear13 not null

Dengjx_Students13:
  djx_StudentId13 primary key
  djx_Sno13 unique not null
  djx_Sname13 not null
  djx_Gender13 check in ('MALE','FEMALE')
  djx_Age13 check between 15 and 35
  djx_ClassId13 references Dengjx_Classes13
  djx_RegionId13 references Dengjx_Regions13
  djx_TotalCredits13 default 0

Dengjx_Teachers13:
  djx_TeacherId13 primary key
  djx_Tno13 unique not null
  djx_Tname13 not null
  djx_Gender13 check in ('MALE','FEMALE')
  djx_Age13 check between 20 and 75
  djx_Title13 not null
  djx_Phone13 not null

Dengjx_Courses13:
  djx_CourseId13 primary key
  djx_CourseCode13 unique not null
  djx_CourseName13 not null
  djx_Hours13 check > 0
  djx_AssessmentType13 check in ('EXAM','CHECK')
  djx_Credit13 check > 0

Dengjx_TeachingAssignments13:
  djx_AssignmentId13 primary key
  djx_CourseId13 references Dengjx_Courses13
  djx_ClassId13 references Dengjx_Classes13
  djx_TeacherId13 references Dengjx_Teachers13
  djx_AcademicYear13 not null
  djx_Semester13 check in (1,2)
  djx_CourseType13 check in ('REQUIRED','ELECTIVE')
  djx_Capacity13 check > 0
  djx_EnrollmentOpen13 boolean default false

Dengjx_Enrollments13:
  djx_EnrollmentId13 primary key
  djx_StudentId13 references Dengjx_Students13
  djx_AssignmentId13 references Dengjx_TeachingAssignments13
  djx_Status13 check in ('SELECTED','DROPPED','COMPLETED')
  djx_SelectedAt13 not null
  djx_DroppedAt13 nullable
  unique(djx_StudentId13, djx_AssignmentId13)

Dengjx_Grades13:
  djx_GradeId13 primary key
  djx_EnrollmentId13 unique references Dengjx_Enrollments13
  djx_Score13 check between 0 and 100
  djx_GradedAt13 not null

Dengjx_Users13:
  djx_UserId13 primary key
  djx_Username13 unique not null
  djx_Password13 not null
  djx_Role13 check in ('ADMIN','TEACHER','STUDENT')
  djx_StudentId13 nullable references Dengjx_Students13
  djx_TeacherId13 nullable references Dengjx_Teachers13
  djx_Enabled13 boolean default true
```

- [ ] **Step 3: Add indexes**

Add indexes in `02_schema.sql`:

```sql
CREATE INDEX idx_djx_students_class13 ON Dengjx_Students13(djx_ClassId13);
CREATE INDEX idx_djx_students_region13 ON Dengjx_Students13(djx_RegionId13);
CREATE INDEX idx_djx_assignments_teacher13 ON Dengjx_TeachingAssignments13(djx_TeacherId13);
CREATE INDEX idx_djx_assignments_class13 ON Dengjx_TeachingAssignments13(djx_ClassId13);
CREATE INDEX idx_djx_enrollments_student13 ON Dengjx_Enrollments13(djx_StudentId13);
CREATE INDEX idx_djx_grades_score13 ON Dengjx_Grades13(djx_Score13);
```

- [ ] **Step 4: Create views script**

Write `design/sql/03_views.sql` with views:

```text
V_Dengjx_StudentGrades13:
  student, class, course, teacher, academic year, semester, score, credit

V_Dengjx_TeacherAssignments13:
  teacher, course, class, academic year, semester, course type, selected count

V_Dengjx_ClassCourses13:
  class, major, course, teacher, academic year, semester, course type

V_Dengjx_CourseAverage13:
  course, assignment, average score, max score, min score, pass count, student count

V_Dengjx_RegionStudentCount13:
  region, student count

V_Dengjx_StudentCreditSummary13:
  student, completed courses, passed courses, total credits
```

- [ ] **Step 5: Create trigger script**

Write `design/sql/04_triggers.sql`:

```text
Function Dengjx_RecalculateStudentCredits13(student_id bigint):
  Sum credits for the student's grades where score >= 60.
  Update Dengjx_Students13.djx_TotalCredits13.

Trigger Dengjx_GradesAfterInsertUpdate13:
  After insert or update on Dengjx_Grades13.
  Recalculate the corresponding student's total credits.

Trigger Dengjx_GradesAfterDelete13:
  After delete on Dengjx_Grades13.
  Recalculate the corresponding student's total credits.
```

- [ ] **Step 6: Create procedure script**

Write `design/sql/05_procedures.sql`:

```text
Procedure Dengjx_GetStudentYearScores13(student_id bigint, academic_year varchar):
  Return courses, semester, score, credit, teacher for the selected student and academic year.

Procedure Dengjx_GetCourseScoreRank13(assignment_id bigint):
  Return student, class, score, rank, course average for the selected assignment.
```

- [ ] **Step 7: Create seed data script**

Write `design/sql/06_seed_data.sql` with:

```text
Regions: at least 5
Majors: at least 3
Classes: at least 4
Students: at least 12
Teachers: at least 5
Courses: at least 8
Teaching assignments: required and elective examples
Enrollments: selected and completed examples
Grades: passing and failing examples
Users: admin, teacher accounts, student accounts
```

Use BCrypt hashes for seeded user passwords in backend-compatible format.

- [ ] **Step 8: Document database execution order**

Write `design/sql/README.md`:

```markdown
# SQL 执行顺序

1. 使用 openGauss 管理账号执行 `01_create_database.sql`。
2. 连接数据库 `DengjxMIS13`。
3. 依次执行 `02_schema.sql`、`03_views.sql`、`04_triggers.sql`、`05_procedures.sql`、`06_seed_data.sql`。
4. 执行完成后检查表、视图、触发器、存储过程是否存在。
```

- [ ] **Step 9: Verify SQL files exist**

Run:

```powershell
Get-ChildItem design\sql
```

Expected: list all six SQL files and `README.md`.

- [ ] **Step 10: Commit database scripts**

Run:

```powershell
git add design/sql
git commit -m "feat: add opengauss database scripts"
```

Expected: commit succeeds.

---

## Task 2: Backend Project Scaffold

**Files:**

- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/dengjx/affairs/AcademicAffairsApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/test/java/com/dengjx/affairs/AcademicAffairsApplicationTests.java`
- Create: `backend/README.md`

- [ ] **Step 1: Create Maven project structure**

Run:

```powershell
New-Item -ItemType Directory -Force backend\src\main\java\com\dengjx\affairs
New-Item -ItemType Directory -Force backend\src\main\resources
New-Item -ItemType Directory -Force backend\src\test\java\com\dengjx\affairs
```

Expected: directories exist.

- [ ] **Step 2: Create `pom.xml`**

Use Spring Boot 3 dependencies:

```xml
<dependencies>
  <dependency>spring-boot-starter-web</dependency>
  <dependency>spring-boot-starter-validation</dependency>
  <dependency>spring-boot-starter-security</dependency>
  <dependency>mybatis-plus-spring-boot3-starter</dependency>
  <dependency>opengauss-jdbc</dependency>
  <dependency>jjwt-api</dependency>
  <dependency>lombok</dependency>
  <dependency>spring-boot-starter-test</dependency>
</dependencies>
```

Use explicit versions for MyBatis-Plus, openGauss JDBC, and JJWT in `<properties>` or dependency entries.

- [ ] **Step 3: Create application entrypoint**

Write `AcademicAffairsApplication.java`:

```java
package com.dengjx.affairs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AcademicAffairsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcademicAffairsApplication.class, args);
    }
}
```

- [ ] **Step 4: Create environment-based config**

Write `application.yml` using environment variables:

```yaml
spring:
  datasource:
    driver-class-name: org.opengauss.Driver
    url: ${DB_URL:jdbc:opengauss://localhost:5432/DengjxMIS13}
    username: ${DB_USERNAME:}
    password: ${DB_PASSWORD:}
  jackson:
    time-zone: Asia/Shanghai

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false

app:
  jwt:
    secret: ${JWT_SECRET:dev-only-change-this-secret}
    expiration-minutes: ${JWT_EXPIRATION_MINUTES:120}
```

- [ ] **Step 5: Create smoke test**

Write `AcademicAffairsApplicationTests.java`:

```java
package com.dengjx.affairs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AcademicAffairsApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 6: Verify Maven build**

Run:

```powershell
cd backend
mvn test
```

Expected: build succeeds or fails only because Maven/JDK is missing. If missing, record the local tool requirement in `backend/README.md`.

- [ ] **Step 7: Commit backend scaffold**

Run:

```powershell
git add backend
git commit -m "feat: scaffold spring boot backend"
```

Expected: commit succeeds.

---

## Task 3: Backend Common Layer and Security

**Files:**

- Create: `backend/src/main/java/com/dengjx/affairs/common/ApiResponse.java`
- Create: `backend/src/main/java/com/dengjx/affairs/common/PageResult.java`
- Create: `backend/src/main/java/com/dengjx/affairs/common/BusinessException.java`
- Create: `backend/src/main/java/com/dengjx/affairs/common/GlobalExceptionHandler.java`
- Create: `backend/src/main/java/com/dengjx/affairs/security/JwtService.java`
- Create: `backend/src/main/java/com/dengjx/affairs/security/JwtAuthenticationFilter.java`
- Create: `backend/src/main/java/com/dengjx/affairs/security/SecurityConfig.java`
- Create: `backend/src/main/java/com/dengjx/affairs/auth/AuthController.java`
- Create: `backend/src/main/java/com/dengjx/affairs/auth/AuthService.java`
- Create: `backend/src/main/java/com/dengjx/affairs/auth/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/dengjx/affairs/auth/dto/LoginResponse.java`

- [ ] **Step 1: Implement response wrapper**

`ApiResponse<T>` fields:

```text
success: boolean
message: String
data: T
```

Static factories:

```text
ok(T data)
fail(String message)
```

- [ ] **Step 2: Implement business exception and global handler**

Handle:

```text
BusinessException -> HTTP 400
MethodArgumentNotValidException -> HTTP 400
AccessDeniedException -> HTTP 403
Exception -> HTTP 500
```

Return `ApiResponse.fail(message)`.

- [ ] **Step 3: Implement JWT service**

`JwtService` methods:

```text
generateToken(userId, username, role)
parseToken(token)
getUserId(token)
getRole(token)
isValid(token)
```

Use `app.jwt.secret` and `app.jwt.expiration-minutes`.

- [ ] **Step 4: Implement security config**

Rules:

```text
POST /api/auth/login -> permitAll
/api/admin/** -> ADMIN
/api/teacher/** -> TEACHER or ADMIN
/api/student/** -> STUDENT or ADMIN
all other /api/** -> authenticated
```

- [ ] **Step 5: Implement login endpoint**

Endpoint:

```text
POST /api/auth/login
```

Request:

```json
{"username":"admin","password":"123456"}
```

Response:

```json
{"token":"jwt-token","role":"ADMIN","displayName":"管理员"}
```

- [ ] **Step 6: Verify security build**

Run:

```powershell
cd backend
mvn test
```

Expected: test passes.

- [ ] **Step 7: Commit common and security layer**

Run:

```powershell
git add backend
git commit -m "feat: add backend common security and auth"
```

Expected: commit succeeds.

---

## Task 4: Backend Entities and CRUD APIs

**Files:**

- Create entity, mapper, service, controller, DTO files under:
  - `backend/src/main/java/com/dengjx/affairs/region`
  - `backend/src/main/java/com/dengjx/affairs/major`
  - `backend/src/main/java/com/dengjx/affairs/schoolclass`
  - `backend/src/main/java/com/dengjx/affairs/student`
  - `backend/src/main/java/com/dengjx/affairs/teacher`
  - `backend/src/main/java/com/dengjx/affairs/course`
  - `backend/src/main/java/com/dengjx/affairs/assignment`

- [ ] **Step 1: Implement entities mapped to personalized table and field names**

Each entity must use explicit mapping:

```java
@TableName("Dengjx_Students13")
public class Student {
    @TableId("djx_StudentId13")
    private Long studentId;

    @TableField("djx_Sno13")
    private String sno;
}
```

Apply the same pattern to all tables. Do not rely on naming conversion.

- [ ] **Step 2: Implement admin CRUD endpoints**

Endpoint groups:

```text
/api/admin/regions
/api/admin/majors
/api/admin/classes
/api/admin/students
/api/admin/teachers
/api/admin/courses
/api/admin/assignments
```

Each group supports:

```text
GET list with query
GET detail by id
POST create
PUT update
DELETE delete
```

- [ ] **Step 3: Add service validations**

Validate:

```text
student age between 15 and 35
teacher age between 20 and 75
course credit > 0
course hours > 0
assignment semester is 1 or 2
assignment capacity > 0
```

- [ ] **Step 4: Verify backend build**

Run:

```powershell
cd backend
mvn test
```

Expected: test passes.

- [ ] **Step 5: Commit CRUD APIs**

Run:

```powershell
git add backend
git commit -m "feat: add academic base data APIs"
```

Expected: commit succeeds.

---

## Task 5: Backend Enrollment, Grade, and Statistics APIs

**Files:**

- Create package: `backend/src/main/java/com/dengjx/affairs/enrollment`
- Create package: `backend/src/main/java/com/dengjx/affairs/grade`
- Create package: `backend/src/main/java/com/dengjx/affairs/statistics`

- [ ] **Step 1: Implement enrollment service**

Rules:

```text
Student can enroll only when assignment enrollment is open.
Student cannot exceed assignment capacity.
Student cannot duplicate active enrollment.
Student cannot drop a course after grade exists.
Admin can create, drop, and restore enrollment records.
```

Endpoints:

```text
GET /api/student/enrollments/available
POST /api/student/enrollments/{assignmentId}
DELETE /api/student/enrollments/{assignmentId}
GET /api/student/enrollments/mine
GET /api/teacher/enrollments/assignments/{assignmentId}/students
GET /api/admin/enrollments
POST /api/admin/enrollments
DELETE /api/admin/enrollments/{id}
```

- [ ] **Step 2: Implement grade service**

Rules:

```text
Score must be 0-100.
Grade must reference an existing selected or completed enrollment.
Teacher can grade only own assignment.
Admin can manage all grades.
Student can read only own grades.
```

Endpoints:

```text
GET /api/student/grades/mine
GET /api/teacher/grades/assignments/{assignmentId}
POST /api/teacher/grades
PUT /api/teacher/grades/{id}
GET /api/admin/grades
POST /api/admin/grades
PUT /api/admin/grades/{id}
DELETE /api/admin/grades/{id}
```

- [ ] **Step 3: Implement statistics service**

Use views and stored procedures:

```text
GET /api/admin/statistics/regions
GET /api/admin/statistics/course-averages
GET /api/admin/statistics/student-year-scores
GET /api/admin/statistics/course-rank
GET /api/teacher/statistics/course-averages
GET /api/teacher/statistics/course-rank
GET /api/student/statistics/credits
GET /api/student/statistics/year-scores
GET /api/student/statistics/rank
```

- [ ] **Step 4: Verify business APIs**

Run:

```powershell
cd backend
mvn test
```

Expected: test passes.

- [ ] **Step 5: Commit business APIs**

Run:

```powershell
git add backend
git commit -m "feat: add enrollment grade and statistics APIs"
```

Expected: commit succeeds.

---

## Task 6: Frontend Scaffold and Auth Flow

**Files:**

- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.ts`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.ts`
- Create: `frontend/src/stores/auth.ts`
- Create: `frontend/src/api/http.ts`
- Create: `frontend/src/views/login/LoginView.vue`
- Create: `frontend/src/layouts/MainLayout.vue`

- [ ] **Step 1: Create Vite Vue TypeScript project files**

Use dependencies:

```json
{
  "dependencies": {
    "@element-plus/icons-vue": "latest",
    "axios": "latest",
    "element-plus": "latest",
    "pinia": "latest",
    "vue": "latest",
    "vue-router": "latest"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "latest",
    "typescript": "latest",
    "vite": "latest",
    "vue-tsc": "latest"
  }
}
```

- [ ] **Step 2: Implement HTTP client**

`frontend/src/api/http.ts`:

```text
Base URL: /api
Request interceptor: attach Authorization bearer token
Response interceptor: unwrap ApiResponse data or show error
401 handler: clear auth store and redirect login
```

- [ ] **Step 3: Implement auth store**

`frontend/src/stores/auth.ts` state:

```text
token
role
displayName
isAuthenticated
login()
logout()
```

- [ ] **Step 4: Implement router**

Routes:

```text
/login
/admin/dashboard
/teacher/dashboard
/student/dashboard
```

Navigation guard:

```text
No token -> /login
Wrong role -> role dashboard
```

- [ ] **Step 5: Implement login view and layout**

Login fields:

```text
username
password
submit button
error message
```

Layout:

```text
left menu
top user dropdown
main router-view
```

- [ ] **Step 6: Verify frontend build**

Run:

```powershell
cd frontend
npm install
npm run build
```

Expected: build succeeds.

- [ ] **Step 7: Commit frontend scaffold**

Run:

```powershell
git add frontend
git commit -m "feat: scaffold vue frontend auth flow"
```

Expected: commit succeeds.

---

## Task 7: Frontend Admin Pages

**Files:**

- Create views under: `frontend/src/views/admin`
- Create API modules under: `frontend/src/api/modules`
- Create shared components under: `frontend/src/components`

- [ ] **Step 1: Implement shared table page pattern**

Create reusable components:

```text
DataToolbar: keyword search and action buttons
ConfirmDeleteButton: delete confirmation
PageContainer: consistent title and content spacing
```

- [ ] **Step 2: Implement admin base data pages**

Pages:

```text
AdminDashboardView.vue
RegionManagementView.vue
MajorManagementView.vue
ClassManagementView.vue
StudentManagementView.vue
TeacherManagementView.vue
CourseManagementView.vue
AssignmentManagementView.vue
UserManagementView.vue
```

Each management page includes:

```text
query form
table
pagination
create dialog
edit dialog
delete confirmation
```

- [ ] **Step 3: Implement admin enrollment and grade pages**

Pages:

```text
EnrollmentManagementView.vue
GradeManagementView.vue
```

Enrollment page must show:

```text
student
course
teacher
academic year
semester
status
selected time
```

Grade page must show:

```text
student
course
teacher
score
graded time
```

- [ ] **Step 4: Implement admin statistics pages**

Pages:

```text
StatisticsView.vue
```

Sections:

```text
region student count
course average score
student year scores
course rank
student credit summary
teacher assignments
class courses
```

- [ ] **Step 5: Verify frontend build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build succeeds.

- [ ] **Step 6: Commit admin frontend**

Run:

```powershell
git add frontend
git commit -m "feat: add admin frontend pages"
```

Expected: commit succeeds.

---

## Task 8: Frontend Teacher and Student Pages

**Files:**

- Create views under: `frontend/src/views/teacher`
- Create views under: `frontend/src/views/student`

- [ ] **Step 1: Implement teacher pages**

Pages:

```text
TeacherDashboardView.vue
TeacherAssignmentsView.vue
TeacherEnrollmentListView.vue
TeacherGradeEntryView.vue
TeacherStatisticsView.vue
TeacherProfileView.vue
```

Teacher flow:

```text
view assignments -> open student list -> enter or edit scores -> view average and rank
```

- [ ] **Step 2: Implement student pages**

Pages:

```text
StudentDashboardView.vue
AvailableCoursesView.vue
MyEnrollmentsView.vue
MyCoursesView.vue
MyGradesView.vue
MyCreditsView.vue
MyRankView.vue
StudentProfileView.vue
```

Student flow:

```text
view available courses -> enroll -> view my enrollments -> view grades -> view credits and rank
```

- [ ] **Step 3: Verify role menus**

Menu rules:

```text
ADMIN sees admin pages only.
TEACHER sees teacher pages only.
STUDENT sees student pages only.
```

- [ ] **Step 4: Verify frontend build**

Run:

```powershell
cd frontend
npm run build
```

Expected: build succeeds.

- [ ] **Step 5: Commit teacher and student frontend**

Run:

```powershell
git add frontend
git commit -m "feat: add teacher and student frontend pages"
```

Expected: commit succeeds.

---

## Task 9: Integration and Documentation

**Files:**

- Create: `design/docs/验收说明.md`
- Create: `design/docs/接口清单.md`
- Modify: `backend/README.md`
- Modify: `frontend/README.md`

- [ ] **Step 1: Write backend run instructions**

`backend/README.md` must include:

```markdown
# 后端运行说明

1. 设置环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`JWT_SECRET`。
2. 执行 `mvn spring-boot:run`。
3. 访问 `http://localhost:8080/api/auth/login` 验证登录接口。
```

Do not include actual database password values.

- [ ] **Step 2: Write frontend run instructions**

`frontend/README.md` must include:

```markdown
# 前端运行说明

1. 执行 `npm install`。
2. 执行 `npm run dev`。
3. 打开 Vite 输出的本地地址。
```

- [ ] **Step 3: Write interface list**

`design/docs/接口清单.md` must list:

```text
auth APIs
admin APIs
teacher APIs
student APIs
statistics APIs
```

For each endpoint include method, path, role, and purpose.

- [ ] **Step 4: Write acceptance guide**

`design/docs/验收说明.md` must include:

```text
SQL execution order
admin closed-loop verification
teacher closed-loop verification
student closed-loop verification
trigger verification
procedure verification
view verification
```

- [ ] **Step 5: Verify final build**

Run:

```powershell
cd backend
mvn test
cd ..\frontend
npm run build
```

Expected: backend tests pass and frontend build succeeds. If local JDK, Maven, Node, or database connection is unavailable, record the exact unavailable dependency and the command output summary.

- [ ] **Step 6: Commit integration docs**

Run:

```powershell
git add backend frontend design/docs
git commit -m "docs: add run and acceptance documentation"
```

Expected: commit succeeds.

---

## Self-Review

### Spec Coverage

- Database tables, constraints, views, triggers, procedures: covered by Task 1.
- Spring Boot backend and JWT permissions: covered by Tasks 2 and 3.
- Base data APIs: covered by Task 4.
- Enrollment, grade, and statistics APIs: covered by Task 5.
- Vue frontend auth and layout: covered by Task 6.
- Admin pages: covered by Task 7.
- Teacher and student pages: covered by Task 8.
- Run and acceptance docs: covered by Task 9.

### Type and Naming Consistency

- Database name: `DengjxMIS13`.
- Table prefix: `Dengjx_`.
- Field prefix: `djx_`.
- Backend base package: `com.dengjx.affairs`.
- Role values: `ADMIN`、`TEACHER`、`STUDENT`.
- Course type values: `REQUIRED`、`ELECTIVE`.
- Enrollment status values: `SELECTED`、`DROPPED`、`COMPLETED`.

### Verification Strategy

- Database verification uses openGauss execution and object existence checks.
- Backend verification uses `mvn test`.
- Frontend verification uses `npm run build`.
- Integration verification uses role-based closed-loop manual checks documented in `design/docs/验收说明.md`.
