# 学生成绩管理系统 (Student Grade Management System)

这是一个基于 Spring Boot 3.x 和原生 JavaScript 开发的学生成绩管理系统。系统支持学生和教师两种角色的访问控制，集成了 JWT 认证，并利用 LangChain4j 接入阿里云通义千问 (Qwen) 大模型，提供智能学业咨询功能。

## 📌 项目概述

本项目是一个典型的 **“前后端不分离但接口化”** 的教学/课程实验项目：

- 后端提供 REST 风格接口（统一返回 `Result<T>` 结构），并负责鉴权、权限控制、业务逻辑与数据持久化。
- 前端页面以静态资源形式由 Spring Boot 直接托管（`src/main/resources/static`），使用原生 JavaScript 调用后端 API。
- 登录后通过 JWT 在前端保存 token，后续请求在 `Authorization: Bearer <token>` 中携带。

### 在线页面入口（本地启动后）

- 首页/登录：`http://localhost:8080/index.html`
- 学生端：`http://localhost:8080/student.html`
- 教师端：`http://localhost:8080/teacher.html`
- 登录测试页：`http://localhost:8080/test-login.html`

## ✨ 主要功能

- **账号体系与角色访问控制**
  - **学生**：注册/登录、维护个人信息、选课/退课、查询成绩、AI 学业咨询（支持流式输出）。
  - **教师**：注册/登录、维护个人信息、查看教学班、查看班级学生与成绩、录入/批量录入成绩、查看统计分析、AI 教学分析（支持流式输出）。
- **成绩管理**
  - 支持平时/期中/实验/期末等多维成绩字段。
  - 支持成绩统计与分析视图（面向教师）。
- **AI 智能咨询（可选启用）**
  - 学生：学习建议、薄弱项分析、课程规划等。
  - 教师：班级成绩概况分析、改进建议等。

## 🛠 技术栈

### 后端
- **核心框架**：Spring Boot 3.2.1
- **安全框架**：Spring Security + JWT
- **ORM**：MyBatis 3.0.3（XML Mapper）
- **数据库**：MySQL 8.0+
- **AI 集成**：LangChain4j（DashScope / Qwen）
- **其他**：Validation、Lombok、JJWT

> 说明：`pom.xml` 中包含 `spring-boot-starter-data-redis` 依赖且 `application.yaml` 有 Redis 配置，但根据当前代码检索，项目业务逻辑未实际使用 Redis（因此 **不需要** 启动 Redis 才能运行主要功能）。

### 前端
- HTML / CSS / JavaScript（原生，无框架）
- 主要脚本位于：`src/main/resources/static/js/`

## ⚙️ 环境要求

- **JDK**：Java 21
- **Maven**：3.6+
- **MySQL**：8.0+

## 🚀 快速开始

### 1) 数据库准备

1. 创建数据库：`stu_grade_sys`
2. 导入 SQL 初始化脚本
   - 若仓库中没有提供 `.sql` 文件，可根据 `src/main/java/com/cqu/exp04/entity` 与 mapper XML（`src/main/resources/mapper`）中的字段自行建表。
3. 确保 MySQL 服务运行在 `localhost:3306`

#### 数据库表（逻辑模型）

项目围绕以下核心表进行组织（命名以实际数据库为准）：

- `user`：登录账号（用户名/密码/角色等）
- `student`：学生档案
- `teacher`：教师档案
- `course`：课程信息
- `teaching_class`：教学班（课程 + 教师 + 学期等）
- `enrollment`：选课记录（学生-教学班）
- `score`：成绩明细（平时/期中/实验/期末/总评/绩点等）
- `score_weight`：成绩各项权重配置（按教学班配置）

### 2) 配置修改

主要配置在 `src/main/resources/application.yaml`：

- 数据库：`spring.datasource.*`
- JWT：`jwt.secret`、`jwt.expiration`
- AI（可选）：`langchain4j.dashscope.chat-model.api-key`

示例（按需修改）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stu_grade_sys
    username: root
    password: your_password

# AI 配置（可选）
langchain4j:
  dashscope:
    chat-model:
      api-key: ${DASHSCOPE_API_KEY:your_api_key_here}
```

> 建议：不要把真实 API Key 写死在配置文件中，优先通过环境变量 `DASHSCOPE_API_KEY` 提供。

### 3) 启动项目

在项目根目录执行：

```bash
mvn spring-boot:run
```

或在 IntelliJ IDEA 中运行 `com.cqu.exp04.Exp04Application`。

### 4) 使用方式（典型流程）

1. 访问 `index.html` 登录/注册
2. 登录成功后前端保存 token
3. 进入学生端/教师端页面进行业务操作

## 🔐 认证与权限（JWT + Spring Security）

- 登录接口：`POST /api/auth/login`，成功后返回 `LoginResponse`（包含 token 与角色信息）。
- 后续请求：在请求头追加：`Authorization: Bearer <token>`。
- 后端过滤器会从 token 中解析角色/身份，并将 `roleId`（学生ID或教师ID）写入 `HttpServletRequest` 属性，Controller 里通常通过：
  - `Long roleId = (Long) request.getAttribute("roleId");`
  来获取当前登录用户对应的业务主键。

## 🤖 AI 功能说明（LangChain4j + 通义千问）

AI 功能属于“增强项”，不影响基础成绩管理功能。

- 学生咨询：
  - 普通：`POST /api/student/ai/consult`
  - 流式（SSE）：`GET /api/student/ai/consult/stream?message=...`
- 教师咨询：
  - 普通：`POST /api/teacher/ai/consult`（需要 `teachingClassId`）
  - 流式（SSE）：`GET /api/teacher/ai/consult/stream?teachingClassId=...&message=...`

> 流式接口返回 `text/event-stream`，适合在前端做“边生成边展示”的效果。

## 🔌 API 概览

以 Controller 的 `@RequestMapping` 为准：

### 认证（`/api/auth`）
- `POST /login`：登录
- `POST /register/student`：学生注册
- `POST /register/teacher`：教师注册
- `GET /test`：测试接口

### 学生（`/api/student`）
- `GET /profile`：查看个人信息
- `PUT /profile`：更新个人信息
- `GET /scores`：我的成绩（含统计信息）
- `GET /enrollments`：我的选课
- `POST /enroll`：选课（body: `{ "teachingClassId": 1 }`）
- `DELETE /enroll/{enrollmentId}`：退课
- `POST /ai/consult`：AI 咨询
- `GET /ai/consult/stream`：AI 流式咨询

### 教师（`/api/teacher`）
- `GET /profile`：查看个人信息
- `PUT /profile`：更新个人信息
- `GET /classes`：我的教学班列表
- `GET /class/{classId}/students`：教学班学生列表
- `GET /class/{classId}/scores`：教学班成绩列表
- `POST /score/input`：录入/更新单个学生成绩
- `POST /score/batch`：批量录入
- `GET /class/{classId}/statistics`：班级统计
- `POST /ai/consult`：AI 教学分析
- `GET /ai/consult/stream`：AI 流式教学分析

### 公共课程（`/api/course`）
- `GET /list`：课程列表
- `GET /{id}`：课程详情
- `GET /classes?semester=...`：可选教学班
- `GET /{courseId}/classes`：指定课程的教学班
- `GET /class/{classId}`：教学班详情

## 📂 项目结构

```
src/main/java/com/cqu/exp04/
├── config/          # 配置类（Security、LangChain4j 等）
├── controller/      # 控制器层（REST API 入口）
├── dto/             # 请求 DTO（LoginRequest、ScoreInputRequest 等）
├── entity/          # 实体类（与数据库表关联）
├── exception/       # 全局异常处理
├── mapper/          # MyBatis Mapper 接口
├── security/        # JWT 工具与过滤器
├── service/         # 业务逻辑接口与实现
└── vo/              # 返回 VO（Result、统计视图等）

src/main/resources/
├── mapper/          # MyBatis XML
├── static/          # 前端静态资源（页面 + js/css）
└── application.yaml # 配置文件
```

## 🧩 常见问题（FAQ）

### 1) 我需要启动 Redis 吗？

不需要。

- 当前项目虽然引入了 Redis 相关依赖与配置，但从代码层面检索不到实际使用点。
- 若后续要做缓存/会话/AI 对话记忆等功能，再考虑引入并启用 Redis。

### 2) AI 功能用不了/报鉴权错误？

- 检查是否正确配置 `DASHSCOPE_API_KEY`（环境变量或 `application.yaml`）。
- 检查网络环境是否可访问 DashScope 服务。

## 🧭 开发说明与可改进点

- MyBatis：优先确保 `mapper` 接口与 `resources/mapper/*Mapper.xml` 一一对应，避免“接口声明了方法但 XML 未实现”。
- 安全：生产环境建议将 `jwt.secret` 换成更安全的值，并通过环境变量/密钥管理系统注入。
- AI：目前咨询是无状态的（不保留历史对话）。如果要做“连续对话”，可以在服务层加会话存储（内存/数据库/Redis）。

## 📄 许可证

本项目仅供学习交流使用。
