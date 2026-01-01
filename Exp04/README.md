# 学生成绩管理系统 (Student Grade Management System)

这是一个基于 Spring Boot 和原生前端技术构建的学生成绩管理系统。系统集成了 AI 辅助功能，为学生提供学业咨询，为教师提供教学分析。

## ⚠️ 关于 Redis 的说明
本项目**不需要** Redis。虽然之前的配置中可能包含 Redis 依赖，但实际业务逻辑中并未依赖 Redis 进行缓存或会话管理（使用 JWT 进行无状态认证）。目前已移除相关依赖和配置，直接启动即可。

## 🛠 技术栈

- **后端**: Spring Boot 3.2.1, MyBatis 3.0.3, Spring Security, JWT
- **数据库**: MySQL 8.0
- **AI 集成**: LangChain4j (接入阿里云通义千问 Qwen-Max)
- **前端**: HTML5, CSS3, Vanilla JavaScript (原生 JS)

## ✨ 功能特性

### 👨‍🎓 学生端
1.  **我的成绩**: 查看各科成绩明细、平均分、绩点等统计信息。
2.  **选课中心**: 查看已选课程，支持退课操作。
3.  **选课大厅**: 浏览可选课程并进行选课。
4.  **AI 学业顾问**: 基于个人成绩数据的 AI 智能咨询，提供学习建议。
5.  **个人信息**: 查看和修改个人联系方式。

### 👩‍🏫 教师端
1.  **我的教学班**: 查看所教授的班级列表及选课人数。
2.  **成绩管理**: 在线录入和修改学生成绩（平时、期中、实验、期末），支持批量保存。
3.  **教学分析**: 查看班级成绩统计（平均分、及格率等）。
4.  **AI 教学助手**: 针对特定班级的成绩分布进行 AI 智能分析，辅助教学改进。
5.  **个人信息**: 查看和修改个人联系方式。

## 🚀 快速开始

### 1. 环境准备
- JDK 21
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
1. 创建数据库 `stu_grade_sys`。
2. 导入 `sql/schema.sql` (如果有) 或允许 Hibernate/MyBatis 自动初始化（本项目使用 MyBatis，需确保表结构存在）。
3. 修改 `src/main/resources/application.yaml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/stu_grade_sys?...
       username: your_username
       password: your_password
   ```

### 3. AI 配置
本项目使用阿里云通义千问模型。请在 `application.yaml` 中配置您的 API Key：
```yaml
langchain4j:
  dashscope:
    chat-model:
      api-key: your_api_key_here
```
或者设置环境变量 `DASHSCOPE_API_KEY`。

### 4. 启动项目
运行 `Exp04Application.java` 中的 `main` 方法启动 Spring Boot 应用。

### 5. 访问系统
浏览器访问: `http://localhost:8080`

## 🧪 测试账号

- **学生**: `2023100001` / `123456`
- **教师**: `T1001` / `123456`

