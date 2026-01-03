# 学生成绩管理系统 (Student Grade Management System)

一个基于 Spring Boot 和微服务架构构建的学生成绩管理系统，集成了 AI 辅助功能，为学生提供学业咨询，为教师提供教学分析。

## 📋 项目概述

本项目是一个采用微服务架构的学生成绩管理系统，具有以下特点：
- **微服务架构**: 基于 Spring Cloud Alibaba 构建，包含多个独立的服务模块
- **前后端分离架构**: 使用 Spring Boot 作为后端框架
- **服务注册与发现**: 使用 Nacos 作为服务注册中心
- **API 网关**: 使用 Spring Cloud Gateway 统一管理 API 路由
- **集成 JWT 无状态认证机制**
- **使用 MyBatis-Plus 作为持久层框架**
- **集成 LangChain4j 与阿里云通义千问大模型，提供 AI 智能咨询功能**
- **支持多维度成绩管理（平时、期中、实验、期末）**
- **提供可视化数据统计和分析功能**

## 🛠 技术栈

### 后端技术
- **核心框架**: Spring Boot 3.2.1
- **微服务框架**: Spring Cloud 2023.0.0
- **服务注册发现**: Nacos 2.2+
- **API 网关**: Spring Cloud Gateway
- **Web 框架**: Spring MVC
- **持久层**: MyBatis-Plus 3.5.7
- **安全框架**: Spring Security
- **认证机制**: JWT (JSON Web Token)
- **AI 集成**: LangChain4j + 阿里云通义千问 (Qwen-Max)
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **数据序列化**: JSON

### 前端技术
- **基础技术**: HTML5, CSS3, JavaScript (原生)
- **前端框架**: 无 (使用原生 JavaScript)
- **数据可视化**: Chart.js
- **Markdown 解析**: Marked.js
- **HTTP 客户端**: Fetch API

### 依赖版本
- **JDK**: 21
- **MySQL**: 8.0+
- **Nacos**: 2.2+
- **Maven**: 3.6+

## 🏗️ 微服务架构

### 服务模块
本项目采用微服务架构，包含以下服务模块：

1. **gateway** (端口 8080): API 网关服务，统一管理所有请求路由
2. **auth-service** (端口 8081): 认证服务，处理用户认证和授权
3. **core-service** (端口 8082): 核心服务，提供基础功能和静态资源
4. **student-service** (端口 8083): 学生服务，处理学生相关业务逻辑
5. **teacher-service** (端口 8084): 教师服务，处理教师相关业务逻辑
6. **course-service** (端口 8085): 课程服务，管理课程信息
7. **score-service** (端口 8086): 成绩服务，处理成绩相关业务
8. **admin-service** (端口 8087): 管理员服务，处理管理员功能
9. **ai-service** (端口 8088): AI 服务，提供智能咨询服务

### 公共模块
- **libs/common**: 公共实体类、DTO、VO 等共享组件
- **libs/security-common**: 公共安全配置和工具类

## ✨ 核心功能

### 👨‍🎓 学生端功能
1. **成绩查询**:
   - 查看个人各科成绩明细、平均分、绩点等统计信息
   - 支持成绩可视化图表展示（柱状图、雷达图、环形图）
   - 提供成绩等级分布分析

2. **课程管理**:
   - 查看已选课程列表
   - 支持按学期查询可选课程
   - 支持选课和退课操作（基于教学班状态）
   - 实时显示课程容量和选课状态

3. **AI 学业顾问**:
   - 基于个人成绩数据的 AI 智能咨询
   - 提供个性化学习建议和知识点诊断
   - 支持流式响应，实现打字机效果
   - 结合具体课程知识点（如Java、数据结构、数据库等）进行分析

4. **个人信息管理**:
   - 查看和修改个人联系方式
   - 支持邮箱和电话信息更新

### 👩‍🏫 教师端功能
1. **教学管理**:
   - 查看所教授的班级列表及选课人数
   - 支持修改教学班状态（未开课/已开课/已结课）
   - 提供班级详情查看功能

2. **成绩管理**:
   - 在线录入和修改学生成绩
   - 支持平时成绩、期中成绩、实验成绩、期末成绩等多维度评分
   - 批量保存成绩功能
   - 实时计算总分和绩点
   - 支持单行保存和批量保存

3. **教学分析**:
   - 查看班级成绩统计（平均分、及格率、优秀率等）
   - 提供成绩可视化图表（柱状图、雷达图、环形图）
   - 支持成绩分布分析

4. **AI 教学助手**:
   - 针对特定班级的成绩分布进行 AI 智能分析
   - 提供教学改进建议和知识点诊断
   - 支持流式响应，实现打字机效果
   - 提供具体章节和知识点的教学建议

5. **个人信息管理**:
   - 查看和修改个人联系方式
   - 支持邮箱和电话信息更新

### 🛡️ 管理员端功能
1. **学生管理**:
   - 创建新学生账号和档案信息
   - 支持批量添加学生记录
   - 管理学生基本信息（学号、姓名、性别、专业、班级、年级、入学年份等）

2. **教师管理**:
   - 创建新教师账号和档案信息
   - 支持批量添加教师记录
   - 管理教师基本信息（教工号、姓名、性别、职称、院系等）

3. **课程管理**:
   - 创建和管理课程信息
   - 支持设置课程编号、名称、学分、学时等
   - 管理课程类型和描述信息

4. **教学班管理**:
   - 为课程分配授课教师
   - 设置教学班编号、学期、容量等
   - 管理教学班状态（未开课/已开课/已结课）
   - 设置教室和上课时间

## 🚀 快速开始

### 环境准备
1. 安装 JDK 21
2. 安装 MySQL 8.0+
3. 安装 Nacos 2.2+ (作为服务注册中心)
4. 安装 Maven 3.6+
5. 获取阿里云通义千问 API Key（可选，用于 AI 功能）

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

### Nacos 配置
启动 Nacos 服务：
```bash
# 使用 Docker 启动 Nacos
docker run --name nacos-standalone -e MODE=standalone -p 8848:8848 -d nacos/nacos-server
```

### AI 功能配置
本项目集成了阿里云通义千问大模型。请在各服务的 [application.yml](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp_Final/services/admin-service/src/main/resources/application.yml) 中配置 API Key：
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
   cd Exp_Final
   ```

2. 构建项目
   ```bash
   mvn clean install
   ```

3. 启动微服务（按顺序）
   ```bash
   # 启动顺序很重要
   # 1. 启动基础服务
   cd services/auth-service && mvn spring-boot:run &
   cd services/course-service && mvn spring-boot:run &
   cd services/score-service && mvn spring-boot:run &
   
   # 2. 启动业务服务
   cd services/student-service && mvn spring-boot:run &
   cd services/teacher-service && mvn spring-boot:run &
   cd services/admin-service && mvn spring-boot:run &
   cd services/core-service && mvn spring-boot:run &
   cd services/ai-service && mvn spring-boot:run &
   
   # 3. 启动网关服务（最后启动）
   cd services/gateway && mvn spring-boot:run &
   ```

4. 访问系统
   - 前端页面: `http://localhost:8080`
   - Nacos 控制台: `http://localhost:8848/nacos` (用户名/密码: nacos/nacos)

## 👥 默认测试账号

- **学生账号**: 
  - 用户名: `2021001`
  - 密码: `123456`

- **教师账号**: 
  - 用户名: `T1001`
  - 密码: `123456`

- **管理员账号**:
  - 用户名: `nissoncx`
  - 密码: `12345678` (在 [application.yaml](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/resources/application.yaml) 中配置 `app.super-admin` 属性)

## 🔐 安全特性

- JWT 无状态认证，支持 Token 自动刷新
- Spring Security 提供全面的安全保护
- 密码使用 BCrypt 加密存储
- 防止常见的安全漏洞
- 基于角色的访问控制（学生/教师/管理员）
- 服务间通信安全

## 🤖 AI 功能说明

本系统集成了 AI 功能，包括：
- **学生 AI 顾问**: 基于学生个人成绩数据提供学习建议和分析，支持流式响应
- **教师 AI 助手**: 分析班级整体成绩分布，提供教学改进建议，支持流式响应

AI 功能基于阿里云通义千问大模型，能够理解复杂的教育数据并提供有价值的洞察。

### AI 功能实现原理
1. **数据获取**: 从数据库获取相关业务数据（学生成绩、班级统计等）
2. **上下文构建**: 将业务数据转化为自然语言描述作为提示词上下文
3. **AI 处理**: 调用大语言模型进行智能分析
4. **结果返回**: 以结构化格式返回 AI 分析结果

## 📁 项目结构

```
.
├── libs/                       # 公共库模块
│   ├── common/                 # 公共实体类、DTO、VO
│   └── security-common/        # 公共安全配置
├── services/                   # 微服务模块
│   ├── admin-service/          # 管理员服务
│   ├── ai-service/             # AI服务
│   ├── auth-service/           # 认证服务
│   ├── core-service/           # 核心服务
│   ├── course-service/         # 课程服务
│   ├── gateway/                # API网关
│   ├── score-service/          # 成绩服务
│   ├── student-service/        # 学生服务
│   └── teacher-service/        # 教师服务
├── src/                        # 旧版单体应用（保留兼容性）
├── pom.xml                     # 父项目配置
├── 必看！项目启动顺序.md        # 微服务启动指南
└── check-services.sh           # 服务状态检查脚本
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

- 本项目使用微服务架构，需要启动多个服务模块
- 需要 Nacos 作为服务注册中心
- AI 功能需要有效的 API Key 才能正常工作
- 前端使用原生 JavaScript，未使用现代前端框架
- 数据库连接需要配置正确的时区（Asia/Shanghai）
- 成绩计算使用 BigDecimal 以避免浮点数精度问题
- 服务启动顺序很重要，必须按照文档说明启动

## 📞 联系方式

如需技术支持或有任何问题，请联系项目维护者。