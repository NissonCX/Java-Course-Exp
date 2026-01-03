# Java 企业级应用开发课程实验（Exp01–Exp04）

本仓库收录重庆大学计算机学院《Java 企业级应用开发》课程实验作业，项目从 **命令行 Java 应用** 逐步演进到 **RBAC 权限系统**、**现代前端工程化项目**，以及集成 **JWT + MyBatis + AI（LangChain4j/Qwen）** 的完整 Web 系统，最终演进为 **微服务架构系统**。

> 建议阅读顺序：Exp01 → Exp02 → Exp03 → Exp04 → Exp_Final（难度与工程复杂度逐步上升）。

---

## 仓库结构

- `Exp01/`：命令行学生成绩管理系统（Java / Maven）
- `Exp02/`：RBAC 权限管理系统（Java / Maven / MySQL）
- `Exp03/`：学生成绩管理系统前端（Vue3 / TS / Vite / Element Plus）
- `Exp04/`：集成 AI 的学生成绩管理系统（Spring Boot 3 / JWT / MyBatis / MySQL / 原生 JS）
- `Exp_Final/`：基于微服务架构的学生成绩管理系统（Spring Cloud Alibaba / Nacos / Gateway / 多服务模块）

---

## 环境要求（按实验取用）

- **JDK**：21 或更高
- **Maven**：3.6+（各实验为独立 Maven 工程）
- **Node.js**：18+（用于 Exp03）
- **MySQL**：8.0+（用于 Exp02、Exp04、Exp_Final）
- **Nacos**：2.2+（用于 Exp_Final 微服务架构）
- **Redis**：可选（Exp04 `pom.xml`/配置中存在 Redis 依赖，但是否强制取决于当前实现；以各实验 `README.md` 的说明为准）
- **通义千问 API Key**：可选（仅 Exp04 和 Exp_Final 的 AI 功能需要；建议使用环境变量而非写死配置）

---

## 快速开始（总览）

### 1) 克隆仓库

```bash
git clone <your-repo-url>
cd Java-Course-Exp-main
```

### 2) 选择一个实验运行

每个实验都是独立工程：

- Java 实验（Exp01/Exp02/Exp04）：在对应目录执行 `mvn clean package` 或 `mvn spring-boot:run`。
- 前端实验（Exp03）：在 `Exp03/` 执行 `npm install`、`npm run dev`。
- 微服务实验（Exp_Final）：需启动 Nacos 服务注册中心，然后按顺序启动各微服务模块。

---

## 实验一（Exp01）：命令行学生成绩管理系统

**定位**：Java 基础与面向对象、集合与 Stream、MVC 分层的综合练习。

**技术栈**：Java 21、Maven、Lombok（如启用）。

**亮点功能**：
- 自动生成模拟数据（学生/教师/课程/教学班/成绩）
- 班级成绩管理与排序
- 按学号或姓名查询成绩
- 成绩统计分析、学生排名

**运行方式**（以实验内 README 为准）：

```bash
cd Exp01
mvn clean package
# 运行主类（具体 main class 见 Exp01/README.md）
```

文档：`Exp01/README.md`

---

## 实验二（Exp02）：RBAC 权限管理系统

**定位**：基于角色的访问控制（RBAC）模型实现，包含用户/角色/权限的多对多关系与典型安全机制。

**技术栈**：Java 21、Maven、MySQL（JDBC/Spring Boot JDBC 视实现而定）、Lombok。

**亮点功能**：
- 完整 RBAC 模型（用户-角色、角色-权限）
- 细粒度权限控制（可细化到资源/操作）
- 登录认证与尝试次数限制
- 操作审计日志
- 防止自我删除等安全保护

**说明**：Exp02 包含较完整的设计文档（含架构图/流程图/类关系图）。

文档：`Exp02/SystemDesign.md`

---

## 实验三（Exp03）：前端学生成绩管理系统（Vue3 + TS）

**定位**：现代前端工程化实践（路由、状态管理、组件化 UI、TypeScript 类型约束）。

**技术栈**：Vue 3、TypeScript、Vite、Element Plus、Pinia、Vue Router、Day.js。

**运行方式**：

```bash
cd Exp03
npm install
npm run dev
```

> 启动地址与端口以 Vite 输出为准（实验 README 中曾提到 `http://localhost:3000`，如不一致请以终端输出为准）。

文档：`Exp03/README.md`

---

## 实验四（Exp04）：集成 AI 的学生成绩管理系统（Spring Boot 3）

**定位**：综合项目（后端 REST API + 静态前端页面 + JWT 认证 + MyBatis 持久化 + AI 顾问）。

**技术栈**：
- 后端：Spring Boot 3.2.x、Spring Security、JWT、MyBatis、MySQL、Validation、Lombok
- 前端：原生 JavaScript + HTML/CSS（由 Spring Boot 静态资源托管）
- AI：LangChain4j + 通义千问（Qwen / DashScope，可选启用）

**典型页面入口**（本地启动后）：
- 首页/登录：`http://localhost:8080/index.html`
- 学生端：`http://localhost:8080/student.html`
- 教师端：`http://localhost:8080/teacher.html`

**运行方式**：

```bash
cd Exp04
mvn spring-boot:run
```

> Exp04 涉及数据库、JWT、以及 AI Key 等配置，详细步骤与接口说明请直接阅读实验内 README。

文档：`Exp04/README.md`

---

## 实验最终版（Exp_Final）：基于微服务架构的学生成绩管理系统

**定位**：采用微服务架构的完整学生成绩管理系统，集成了 AI 辅助功能，为学生提供学业咨询，为教师提供教学分析。

**技术栈**：
- **核心框架**：Spring Boot 3.2.1、Spring Cloud 2023.0.0
- **微服务框架**：Spring Cloud Alibaba
- **服务注册发现**：Nacos 2.2+
- **API 网关**：Spring Cloud Gateway
- **持久层**：MyBatis-Plus 3.5.7
- **安全框架**：Spring Security + JWT
- **AI 集成**：LangChain4j + 阿里云通义千问 (Qwen-Max)
- **数据库**：MySQL 8.0
- **前端技术**：原生 JavaScript + Chart.js + Marked.js

**微服务模块**：
- `gateway` (端口 8080)：API 网关服务，统一管理所有请求路由
- `auth-service` (端口 8081)：认证服务，处理用户认证和授权
- `core-service` (端口 8082)：核心服务，提供基础功能和静态资源
- `student-service` (端口 8083)：学生服务，处理学生相关业务逻辑
- `teacher-service` (端口 8084)：教师服务，处理教师相关业务逻辑
- `course-service` (端口 8085)：课程服务，管理课程信息
- `score-service` (端口 8086)：成绩服务，处理成绩相关业务
- `admin-service` (端口 8087)：管理员服务，处理管理员功能
- `ai-service` (端口 8088)：AI 服务，提供智能咨询服务

**亮点功能**：
- **微服务架构**：基于 Spring Cloud Alibaba 构建的完整微服务系统
- **服务注册与发现**：使用 Nacos 作为服务注册中心
- **API 网关**：使用 Spring Cloud Gateway 统一管理 API 路由
- **集成 JWT 无状态认证机制**
- **多维度成绩管理**：支持平时、期中、实验、期末成绩管理
- **AI 智能咨询**：集成 LangChain4j 与阿里云通义千问大模型，提供 AI 智能咨询功能
- **数据可视化**：提供成绩统计和分析的可视化功能
- **完整的 RBAC 权限系统**：支持学生、教师、管理员不同角色的权限控制

**运行方式**：

```bash
cd Exp_Final

# 1. 启动 Nacos 服务注册中心
docker run --name nacos-standalone -e MODE=standalone -p 8848:8848 -d nacos/nacos-server

# 2. 构建项目
mvn clean install

# 3. 按顺序启动微服务（参考 必看！项目启动顺序.md）
# 先启动基础服务
cd services/auth-service && mvn spring-boot:run &
cd services/course-service && mvn spring-boot:run &
cd services/score-service && mvn spring-boot:run &

# 再启动业务服务
cd services/student-service && mvn spring-boot:run &
cd services/teacher-service && mvn spring-boot:run &
cd services/admin-service && mvn spring-boot:run &
cd services/core-service && mvn spring-boot:run &
cd services/ai-service && mvn spring-boot:run &

# 最后启动网关服务
cd services/gateway && mvn spring-boot:run &
```

> Exp_Final 是完整的微服务架构项目，涉及多个服务模块，启动顺序很重要，详细说明请阅读 `Exp_Final/README.md` 及 `必看！项目启动顺序.md`。

文档：`Exp_Final/README.md`、`必看！项目启动顺序.md`

---

## 文档与报告

- 根目录 `README.md`：仓库总览（本文）
- 各实验目录内：运行说明与设计/报告文档
- `实验报告/`：课程实验报告（Word文档格式）

---

## 常见问题（FAQ）

### Q1：为什么我在根目录执行 Maven 命令失败？
本仓库是"多实验集合"，每个实验都是独立工程。请先 `cd Exp01` / `cd Exp02` / `cd Exp04` / `cd Exp_Final` 后再运行 Maven。

### Q2：Exp04 和 Exp_Final 一启动就报数据库连接错误？
Exp04 和 Exp_Final 依赖 MySQL。请检查：
- MySQL 是否启动
- `application.yaml` 的 `spring.datasource.*` 是否与本地一致
- 数据库与表结构是否已初始化

### Q3：Exp04 和 Exp_Final 的 AI 功能不可用？
AI 功能通常是"增强项"。请检查：
- 是否配置了 `DASHSCOPE_API_KEY`（建议使用环境变量）
- 网络是否可访问对应服务

### Q4：Exp_Final 微服务启动失败？
Exp_Final 是微服务项目，需要：
- 先启动 Nacos 服务注册中心
- 按照正确顺序启动各微服务模块
- 确保各服务端口未被占用

---

## 许可证与声明

- 许可证：见各实验目录的 `LICENSE`（如存在）以及根目录 `LICENSE`。
- 课程学习用途：仅供学习交流使用，请遵守学术诚信政策，勿直接抄袭作业。