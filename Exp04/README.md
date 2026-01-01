# 学生成绩管理系统 (Student Grade Management System)

一个基于 Spring Boot 和原生前端技术构建的学生成绩管理系统，集成了 AI 辅助功能，为学生提供学业咨询，为教师提供教学分析。

## 📋 项目概述

本项目是一个完整的学生成绩管理系统，具有以下特点：
- 前后端分离架构，使用 Spring Boot 作为后端框架
- 集成 JWT 无状态认证机制
- 使用 MyBatis 作为持久层框架
- 集成 LangChain4j 与阿里云通义千问大模型，提供 AI 智能咨询功能

## 🛠 技术栈

### 后端技术
- **核心框架**: Spring Boot 3.2.1
- **Web 框架**: Spring MVC
- **持久层**: MyBatis 3.0.3
- **安全框架**: Spring Security
- **认证机制**: JWT (JSON Web Token)
- **AI 集成**: LangChain4j + 阿里云通义千问 (Qwen-Max)
- **数据库**: MySQL 8.0
- **构建工具**: Maven

### 前端技术
- **基础技术**: HTML5, CSS3, JavaScript (原生)
- **前端框架**: 无 (使用原生 JavaScript)

### 依赖版本
- **JDK**: 21
- **MySQL**: 8.0+
- **Maven**: 3.6+

## ✨ 核心功能

### 👨‍🎓 学生端功能
1. **成绩查询**: 查看个人各科成绩明细、平均分、绩点等统计信息
2. **课程管理**: 
   - 查看已选课程
   - 支持退课操作
   - 浏览可选课程并进行选课
3. **AI 学业顾问**: 基于个人成绩数据的 AI 智能咨询，提供个性化学习建议
4. **个人信息**: 查看和修改个人联系方式

### 👩‍🏫 教师端功能
1. **教学管理**: 查看所教授的班级列表及选课人数
2. **成绩管理**: 
   - 在线录入和修改学生成绩
   - 支持平时成绩、期中成绩、实验成绩、期末成绩等多维度评分
   - 批量保存成绩功能
3. **教学分析**: 查看班级成绩统计（平均分、及格率、优秀率等）
4. **AI 教学助手**: 针对特定班级的成绩分布进行 AI 智能分析，辅助教学改进决策
5. **个人信息**: 查看和修改个人联系方式

## 🚀 快速开始

### 环境准备
1. 安装 JDK 21
2. 安装 MySQL 8.0+
3. 安装 Maven 3.6+
4. 获取阿里云通义千问 API Key（可选，用于 AI 功能）

### 数据库配置
1. 创建数据库
   ```sql
   CREATE DATABASE stu_grade_sys CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 初始化表结构
   ```sql
   -- 请根据 mapper 文件中的 SQL 语句创建表结构
   -- 或运行项目时让 MyBatis 自动创建表结构
   ```

3. 修改数据库连接配置
   在 [application.yaml](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/resources/application.yaml) 中修改数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/stu_grade_sys?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
       username: your_username
       password: your_password
   ```

### AI 功能配置
本项目集成了阿里云通义千问大模型。请在 [application.yaml](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/resources/application.yaml) 中配置 API Key：
```yaml
langchain4j:
  dashscope:
    chat-model:
      api-key: your_dashscope_api_key_here
      model-name: qwen-max
```
或者设置环境变量 `DASHSCOPE_API_KEY`。

### 启动项目
1. 克隆项目
   ```bash
   git clone <repository-url>
   cd Exp04
   ```

2. 构建项目
   ```bash
   mvn clean install
   ```

3. 启动应用
   ```bash
   mvn spring-boot:run
   ```
   或者运行 [Exp04Application.java](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/java/com/cqu/exp04/Exp04Application.java) 中的 `main` 方法

4. 访问系统
   - 前端页面: `http://localhost:8080`
   - API 文档: `http://localhost:8080/swagger-ui.html` (如果配置了 Swagger)

## 👥 默认测试账号

- **学生账号**: 
  - 用户名: `2023100001`
  - 密码: `123456`

- **教师账号**: 
  - 用户名: `T1001`
  - 密码: `123456`

## 🔐 安全特性

- JWT 无状态认证，支持 Token 自动刷新
- Spring Security 提供全面的安全保护
- 密码使用 BCrypt 加密存储
- 防止常见的安全漏洞

## 🤖 AI 功能说明

本系统集成了 AI 功能，包括：
- **学生 AI 顾问**: 基于学生个人成绩数据提供学习建议和分析
- **教师 AI 助手**: 分析班级整体成绩分布，提供教学改进建议

AI 功能基于阿里云通义千问大模型，能够理解复杂的教育数据并提供有价值的洞察。

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/cqu/exp04/          # Java 源码
│   │   ├── config/                  # 配置类
│   │   ├── controller/              # 控制器层
│   │   ├── dto/                     # 数据传输对象
│   │   ├── entity/                  # 实体类
│   │   ├── exception/               # 异常处理
│   │   ├── mapper/                  # MyBatis 映射接口
│   │   ├── security/                # 安全相关
│   │   ├── service/                 # 业务逻辑层
│   │   ├── vo/                      # 视图对象
│   │   └── Exp04Application.java    # 应用启动类
│   └── resources/
│       ├── mapper/                  # MyBatis XML 映射文件
│       ├── static/                  # 静态资源 (HTML, CSS, JS)
│       └── application.yaml         # 应用配置文件
└── test/                            # 测试代码
```

## 🧪 测试

运行单元测试：
```bash
mvn test
```

运行集成测试：
```bash
mvn verify
```

## 🚧 项目限制与注意事项

- 本项目不使用 Redis，完全基于 JWT 进行无状态认证
- 所有数据持久化依赖 MySQL 数据库
- AI 功能需要有效的 API Key 才能正常工作
- 前端使用原生 JavaScript，未使用现代前端框架

## 📞 联系方式

如需技术支持或有任何问题，请联系项目维护者。
