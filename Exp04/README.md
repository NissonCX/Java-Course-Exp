# 学生成绩管理系统 (Student Grade Management System)

一个基于 Spring Boot 和原生前端技术构建的学生成绩管理系统，集成了 AI 辅助功能，为学生提供学业咨询，为教师提供教学分析。

## 📋 项目概述

本项目是一个完整的学生成绩管理系统，具有以下特点：
- 前后端分离架构，使用 Spring Boot 作为后端框架
- 集成 JWT 无状态认证机制
- 使用 MyBatis 作为持久层框架
- 集成 LangChain4j 与阿里云通义千问大模型，提供 AI 智能咨询功能
- 支持多维度成绩管理（平时、期中、实验、期末）
- 提供可视化数据统计和分析功能

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
- **Maven**: 3.6+

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

- **管理员账号**:
  - 用户名: `admin`
  - 密码: `admin123` (需要在 [application.yaml](file:///Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/resources/application.yaml) 中配置 `app.super-admin` 属性)

## 🔐 安全特性

- JWT 无状态认证，支持 Token 自动刷新
- Spring Security 提供全面的安全保护
- 密码使用 BCrypt 加密存储
- 防止常见的安全漏洞
- 基于角色的访问控制（学生/教师）

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
src/
├── main/
│   ├── java/com/cqu/exp04/          # Java 源码
│   │   ├── config/                  # 配置类
│   │   │   ├── LangChain4jConfig.java    # LangChain4j 配置
│   │   │   └── SecurityConfig.java       # Spring Security 配置
│   │   ├── controller/              # 控制器层
│   │   │   ├── AuthController.java       # 认证控制器
│   │   │   ├── CourseController.java     # 课程控制器
│   │   │   ├── PasswordTestController.java # 密码测试控制器
│   │   │   ├── StudentController.java    # 学生控制器
│   │   │   └── TeacherController.java    # 教师控制器
│   │   ├── dto/                     # 数据传输对象
│   │   │   ├── AIConsultRequest.java     # AI咨询请求
│   │   │   ├── LoginRequest.java         # 登录请求
│   │   │   ├── ScoreInputRequest.java    # 成绩录入请求
│   │   │   ├── StudentRegisterRequest.java # 学生注册请求
│   │   │   └── TeacherRegisterRequest.java # 教师注册请求
│   │   ├── entity/                  # 实体类
│   │   │   ├── BaseEntity.java           # 基础实体类
│   │   │   ├── Course.java              # 课程实体
│   │   │   ├── Enrollment.java          # 选课记录实体
│   │   │   ├── Score.java               # 成绩实体
│   │   │   ├── Student.java             # 学生实体
│   │   │   ├── Teacher.java             # 教师实体
│   │   │   ├── TeachingClass.java       # 教学班实体
│   │   │   └── User.java                # 用户实体
│   │   ├── exception/               # 异常处理
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   ├── mapper/                  # MyBatis 映射接口
│   │   │   ├── CourseMapper.java        # 课程数据访问层
│   │   │   ├── EnrollmentMapper.java    # 选课数据访问层
│   │   │   ├── ScoreMapper.java         # 成绩数据访问层
│   │   │   ├── StudentMapper.java       # 学生数据访问层
│   │   │   ├── TeacherMapper.java       # 教师数据访问层
│   │   │   ├── TeachingClassMapper.java # 教学班数据访问层
│   │   │   └── UserMapper.java          # 用户数据访问层
│   │   ├── security/                # 安全相关
│   │   │   ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│   │   │   └── JwtUtil.java             # JWT 工具类
│   │   ├── service/                 # 业务逻辑层
│   │   │   ├── AIService.java           # AI服务
│   │   │   ├── AuthService.java         # 认证服务
│   │   │   ├── CourseService.java       # 课程服务
│   │   │   ├── ScoreService.java        # 成绩服务
│   │   │   ├── StudentService.java      # 学生服务
│   │   │   ├── TeacherService.java      # 教师服务
│   │   │   └── impl/                    # 服务实现类
│   │   │       ├── AuthServiceImpl.java    # 认证服务实现
│   │   │       ├── CourseServiceImpl.java  # 课程服务实现
│   │   │       ├── StudentServiceImpl.java # 学生服务实现
│   │   │       └── TeacherServiceImpl.java # 教师服务实现
│   │   ├── vo/                      # 视图对象
│   │   │   ├── ClassScoreStatisticsVO.java # 班级成绩统计视图对象
│   │   │   ├── LoginResponse.java        # 登录响应
│   │   │   └── Result.java               # 统一响应结果
│   │   └── Exp04Application.java    # 应用启动类
│   └── resources/
│       ├── mapper/                  # MyBatis XML 映射文件
│       │   ├── CourseMapper.xml
│       │   ├── EnrollmentMapper.xml
│       │   ├── ScoreMapper.xml
│       │   ├── StudentMapper.xml
│       │   ├── TeacherMapper.xml
│       │   ├── TeachingClassMapper.xml
│       │   └── UserMapper.xml
│       ├── static/                  # 静态资源 (HTML, CSS, JS)
│       │   ├── css/
│       │   │   ├── dashboard.css         # 仪表板样式
│       │   │   ├── login.css             # 登录页面样式
│       │   │   └── style.css             # 通用样式
│       │   ├── js/
│       │   │   ├── api.js                # API 接口封装
│       │   │   ├── login.js              # 登录页面逻辑
│       │   │   ├── register.js           # 注册页面逻辑
│       │   │   ├── student.js            # 学生端页面逻辑
│       │   │   ├── teacher.js            # 教师端页面逻辑
│       │   │   └── utils.js              # 工具函数
│       │   ├── index.html               # 登录页面
│       │   ├── register.html            # 注册页面
│       │   ├── student.html             # 学生端主页面
│       │   └── teacher.html             # 教师端主页面
│       └── application.yaml         # 应用配置文件
└── test/                            # 测试代码
    └── java/com/cqu/exp04/
        └── Exp04ApplicationTests.java # 应用测试类
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
- 数据库连接需要配置正确的时区（Asia/Shanghai）
- 成绩计算使用 BigDecimal 以避免浮点数精度问题

## 📞 联系方式

如需技术支持或有任何问题，请联系项目维护者。