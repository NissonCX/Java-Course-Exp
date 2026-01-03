# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Student Grade Management System** (学生成绩管理系统) built with Spring Boot 3.x for the backend and vanilla JavaScript for the frontend. The system supports role-based access for students and teachers (and a SUPER_ADMIN capability), with JWT authentication and AI-powered advisory features using Alibaba's Qwen (通义千问) through LangChain4j.

**Key Technologies:**
- Spring Boot **3.2.1** (JDK 21)
- Spring MVC + Spring Security
- MyBatis / MyBatis-Plus starter (see `pom.xml`)
- MySQL database (`stu_grade_sys`)
- JWT for stateless authentication
- LangChain4j with DashScope (Alibaba Qwen) for AI features
- Vanilla JavaScript frontend (no framework)

> Note: Redis is mentioned in some docs/notes, but this repository currently does not include Redis dependencies/config as a required runtime component.

## Database Setup

**Database:** MySQL on `localhost:3306`
- Database name: `stu_grade_sys`
- Username: `root`
- Password: `12345678`

**Required Services:**
```bash
# Start MySQL (macOS)
mysql.server start
```

**Database Schema:** The system uses 8 main tables:
- `user` - User authentication (username, password, role)
- `student` - Student profiles
- `teacher` - Teacher profiles
- `course` - Course catalog
- `teaching_class` - Teaching class instances (links courses, teachers, semesters)
- `enrollment` - Student course enrollments
- `score` - Student grades (usual, midterm, experiment, final, total, grade_point)
- `score_weight` - Configurable score weightings per teaching class

## Build & Run Commands

This is a Maven project (see `pom.xml`).

**Run Application:**
- Main class: `com.cqu.exp04.Exp04Application`
- Default port: `8080`

**Testing:**
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=Exp04ApplicationTests
```

**Access Application:**
- Frontend: `http://localhost:8080/index.html`
- Student Dashboard: `http://localhost:8080/student.html`
- Teacher Dashboard: `http://localhost:8080/teacher.html`
- API Base: `http://localhost:8080/api`

## Architecture

### Package Structure

```
com.cqu.exp04/
├── entity/          # Domain models (User, Student, Teacher, Course, Score, etc.)
├── dto/             # Request DTOs (LoginRequest, StudentRegisterRequest, ScoreInputRequest, etc.)
├── vo/              # Response VOs (Result, LoginResponse, StudentScoreVO, ClassScoreStatisticsVO)
├── mapper/          # MyBatis mapper interfaces
├── service/         # Business logic layer
│   └── impl/        # Service implementations
├── controller/      # REST API controllers
├── config/          # Spring configurations (SecurityConfig, LangChain4jConfig)
├── security/        # JWT utilities (JwtUtil, JwtAuthenticationFilter)
└── exception/       # Global exception handler
```

### Authentication Flow

1. **User Registration:** Students/teachers register via `/api/auth/register/student` or `/api/auth/register/teacher`
2. **Login:** POST to `/api/auth/login` returns JWT token and user details
3. **Authorization:** JWT token in `Authorization: Bearer <token>` header
4. **Role Extraction:** `JwtAuthenticationFilter` extracts user info and sets `roleId` + `userRole` in request attributes
5. **Role-Based Access:** Controllers check `request.getAttribute("roleId")` for user identity

### Frontend Integration Contract (Important)

The frontend uses `src/main/resources/static/js/api.js` with:
- `BASE_URL: '/api'`
- requests like `/api/auth/login`, `/api/student/scores`, `/api/teacher/classes`, etc.

If you change API paths/ports during refactors, you MUST keep this contract working (preferred) or update the frontend accordingly.

## Microservices Refactor Playbook (for Claude Code)

This section contains **ready-to-copy prompts** for Claude Code to refactor this monolith into microservices with minimal risk.

### Goals

- Refactor the monolith into a microservices architecture while preserving functionality:
  - Auth (login/register/JWT)
  - Student/Teacher/Admin APIs
  - MyBatis persistence and existing database
  - Static frontend pages remain accessible
  - AI features remain available (optional per environment)

### Non-negotiable Constraints

1. **Always ship runnable code** after each phase (compiles + starts).
2. After each phase, run at least:
   - `mvn -DskipTests=false test` (preferred) or `mvn test`
3. Prefer **minimal, reversible steps** (avoid a big-bang rewrite).
4. Keep API compatibility with the existing frontend (`/api/**`) unless explicitly updated.
5. No unnecessary complexity: start with synchronous HTTP (Gateway + OpenFeign). Messaging (Kafka/RabbitMQ) is optional later.
6. Database strategy should be conservative at first:
   - Phase 1/2: allow a shared MySQL instance (+ shared schema)
   - Phase 3+: evolve toward per-service schemas/databases

### Recommended Target Architecture (phased)

- Phase 1 (minimum viable microservices):
  - `gateway` (Spring Cloud Gateway)
  - `auth-service` (login/register/JWT issuance)
  - `core-service` (everything else; existing monolith moved here as-is)

- Phase 2 (domain split):
  - `student-service`
  - `teacher-service`
  - `admin-service`
  - `course-service`
  - `score-service`
  - `ai-service` (optional)

### Routing Plan (keep frontend stable)

To avoid changing the frontend initially:
- Users access frontend at `http://localhost:8080`
- Gateway listens on `8080` and serves/forwards:
  - `/` and `/*.html`, `/css/**`, `/js/**`, `/favicon.ico` → static frontend (either from gateway itself or forwarded to a web/static service)
  - `/api/auth/**` → `auth-service`
  - `/api/**` → `core-service` (Phase 1)

In Phase 2, route by domain (still under `/api`):
- `/api/student/**` → `student-service`
- `/api/teacher/**` → `teacher-service`
- `/api/admin/**` → `admin-service`
- `/api/course/**` → `course-service`
- `/api/score/**` → `score-service`
- `/api/ai/**` → `ai-service` (or keep `/api/student/ai/**`, `/api/teacher/ai/**` for backward compatibility)

### Security Placement Decision (default recommendation)

Phase 1 (lowest risk):
- Gateway handles CORS and forwarding.
- Downstream services keep existing Spring Security + JWT validation (reuse current `JwtAuthenticationFilter` + `JwtUtil`).

Phase 2/3 (optimization):
- Move JWT validation to gateway (shared secret/public key) + pass user identity as headers.

---

## Claude Code Prompts (Copy/Paste)

### Prompt 0 — Repo scan + decomposition proposal (NO big edits yet)

Paste this to Claude Code:

> 你是一个资深 Java 微服务架构师 + Spring Cloud 工程化专家。请先不要大规模改代码。
>
> 目标：将当前仓库的单体 Spring Boot 项目重构为微服务（最终可运行），并保持现有原生前端能用。
>
> 请你做“扫描 + 方案”输出：
> 1) 扫描 `com.cqu.exp04.controller`、`service`、`mapper`、`entity`、`security`、`resources/static/js/api.js`。
> 2) 输出领域拆分建议表：student/teacher/admin/course/score/enrollment/user/auth/ai 的边界；每个服务包含哪些 Controller/Service/Mapper；哪些表可能归属哪个服务。
> 3) 输出 Phase 1 最小可跑拆分：gateway + auth-service + core-service（保留大部分业务在 core-service），并给出路由表（以 `/api` 为前缀）。
> 4) 给出 Maven 多模块目录结构建议（root pom + services/* + libs/common），并说明 DTO/VO 共享策略。
> 5) 列出风险点与规避策略：JWT、跨域、静态资源托管、LangChain4j Key（DASHSCOPE_API_KEY）、MyBatis mapper XML。
>
> 输出必须是 markdown，包含：服务清单与职责、接口路由规划、数据库策略、迁移顺序。

### Prompt 1 — Phase 1 implementation (Maven multi-module + gateway + auth-service)

Paste this to Claude Code:

> 开始实施 Phase 1。目标：把当前单体改造成 Maven 多模块，并拆出最小微服务骨架：`gateway` + `auth-service` + `core-service`。
>
> 约束：
> - 每一步都要能 `mvn test` 通过再继续。
> - 保持前端 `static/js/api.js` 的 `BASE_URL='/api'` 可用，用户从 `http://localhost:8080` 打开页面不应崩。
>
> 具体任务：
> 1) 把仓库改造成 Maven 多模块：root pom + `services/gateway` + `services/auth-service` + `services/core-service` + `libs/common`（需要时）。
> 2) `core-service`：承载现有业务（尽量少改代码），包含静态资源（Phase 1 可以由 core-service 提供静态资源）。
> 3) `auth-service`：迁移认证相关（`/api/auth/login`、`/api/auth/register/*`、JWT 签发）。
> 4) `gateway`：Spring Cloud Gateway
>    - `/api/auth/**` -> auth-service
>    - `/api/**` -> core-service
>    - 静态资源：优先通过 gateway 转发到 core-service，或直接让 gateway 托管（你自行选择，但必须保证原页面可访问）。
> 5) 安全策略（Phase 1 默认最稳）：gateway 不做复杂鉴权；core-service 保留现有 JWT 校验。
> 6) 为三个服务分别提供 `application.yml/yaml`，端口建议：gateway 8080、auth 8081、core 8082；数据库连接先共享 `stu_grade_sys`。
> 7) 运行验证：
>    - 给出启动顺序
>    - 通过网关调用 `/api/auth/login` 能返回 token
>    - 访问 `/index.html` 能加载页面
>
> 完成后输出：目录结构、如何启动、验证结果。

### Prompt 2 — Phase 2 implementation (domain split)

Paste this to Claude Code:

> 开始 Phase 2：把 `core-service` 按领域拆分为多个业务服务，并保持前端调用不崩。
>
> 推荐拆分（可调整但要解释）：student-service、teacher-service、admin-service、course-service、score-service、ai-service（可选）。
>
> 要求：
> 1) 网关路由清晰：`/api/student/**`、`/api/teacher/**`、`/api/admin/**`、`/api/course/**`、`/api/score/**`、`/api/ai/**`。
> 2) DTO/VO/Result 等共享类型放到 `libs/common`，避免循环依赖。
> 3) 服务间调用优先 OpenFeign（如确有需要）；否则只通过网关聚合也可以。
> 4) 数据库阶段仍共享一个 MySQL，但 mapper/表归属要在代码层清晰；如遇强耦合表，列出后续拆库方案。
> 5) JWT 校验统一策略：要么继续各服务校验，要么 gateway 统一校验（说明取舍）。
> 6) 每拆出一个服务都必须：编译、测试、最小接口验证通过，再继续拆下一个。
>
> 最终输出：启动方式、接口路由表、迁移清单。

### Prompt 3 — Phase 3 (optional hardening)

Paste this to Claude Code:

> Phase 3（可选增强）：在不破坏功能前提下补齐工程化能力。
> 可选项按优先级逐步引入：
> 1) OpenFeign + LoadBalancer（如尚未完成）
> 2) 统一配置管理（先集中式配置也可，说明理由）
> 3) 可观测性：Micrometer + traceId 日志
> 4) Resilience4j：对 AI 调用等外部依赖加熔断/限流
> 5) 事件驱动（仅当确有必要）：Kafka/RabbitMQ
>
> 输出：新增依赖与配置、如何验证、复杂度影响说明。

---

## Notes (AI / Secrets)

- `application.yaml` currently contains a default DashScope API key value in `${DASHSCOPE_API_KEY:...}`. Prefer using environment variables in real deployments.
- JWT secret is configured in `application.yaml` (`jwt.secret`). When splitting services, ensure all services that validate tokens share the same secret (Phase 1/2), or switch to asymmetric signing (Phase 3+).

## Known Issues & TODOs

1. Some mapper methods may be declared in Java interfaces but not implemented in XML.
2. AI requests are stateless (no conversation memory).
3. Performance hardening and observability are not implemented yet.
4. Architecture is currently monolithic; see Microservices Refactor Playbook above.
