# Java课程实验

本仓库包含重庆大学计算机学院**Java企业级应用开发**课程的实验作业，从基础的命令行应用到集成AI的现代化Web系统。

## 课程信息

- **课程名称**：Java企业级应用开发
- **授课教师**：曾令秋
- **学期**：2025-2026学年 大三上学期

## 实验内容

### 实验一：命令行学生成绩管理系统

基于Java开发的命令行学生成绩管理系统，展示了以下Java核心技术：
- 面向对象编程原则
- 集合框架的使用
- Java 8 Stream API进行数据处理
- MVC架构模式

#### 主要功能
- 自动生成模拟数据（学生、教师、课程、班级和成绩）
- 班级成绩管理与排序功能
- 按学号或姓名查询学生成绩
- 成绩统计与分布分析
- 学生排名系统

[实验一详情](Exp01/README.md)

### 实验二：基于角色的访问控制(RBAC)权限管理系统

完整的基于角色的访问控制系统，实现权限管理功能：
- 用户、角色和权限管理
- 安全认证机制
- 细粒度访问控制
- 审计日志功能
- MySQL数据库集成

#### 主要功能
- 完整的RBAC模型实现，支持用户、角色、权限之间的多对多关系
- 带尝试次数限制的安全登录
- 基于角色的功能访问控制
- 操作审计日志
- 防止自我删除功能，增强系统安全性

[实验二详情](Exp02/SystemDesign.md)

### 实验三：前端学生成绩管理系统

基于Vue 3 + TypeScript + Vite + Element Plus开发的学生成绩管理系统前端应用，采用现代化前端技术栈：
- **前端框架**: Vue 3 (Composition API)
- **开发语言**: TypeScript
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router

#### 主要功能
1. **数据初始化**
   - 自动生成 120 名学生数据（学号、姓名、性别、年龄、专业）
   - 自动生成 6 名教师数据（工号、姓名、职称、院系）
   - 自动生成 5 门课程数据（课程编号、名称、学分）
   - 自动创建教学班（每门课至少 2 个教学班）

2. **学生选课**
   - 每个学生随机选择 3-5 门课程
   - 自动分配到对应课程的教学班
   - 确保每个教学班至少 20 名学生
   - 查看教学班学生名单

3. **成绩录入**
   - 分教学班录入成绩
   - 支持录入平时成绩、期中成绩、实验成绩、期末成绩
   - 自动计算综合成绩（权重：平时20% + 期中20% + 实验20% + 期末40%）
   - 成绩采用正态分布生成（均值75，标准差10）
   - 记录成绩录入时间

4. **成绩查询**
   - 按学生查询：输入学号或姓名查询学生所有课程成绩
   - 按教学班查询：查看教学班所有学生成绩
   - 支持按学号或成绩排序
   - 显示学生成绩汇总（平均分、总学分、GPA）

5. **统计分析**
   - 分数段分布统计（90-100、80-89、70-79、60-69、0-59）
   - 支持按课程筛选统计
   - 可视化展示分数段占比
   - 显示优秀率、及格率、不及格率

6. **学生排名**
   - 支持按平均分、GPA、总学分排序
   - 前三名特殊标记（🥇🥈🥉）
   - 支持按专业筛选
   - 查看学生详情和成绩单
   - 导出排名数据

7. **系统概览**
   - 统计数据概览（学生、教师、课程、教学班数量）
   - 课程列表展示
   - 教学班列表展示
   - 快速操作入口

[实验三详情](Exp03/README.md)

### 实验四：集成AI的学生成绩管理系统（重点）

实验四是一个功能完整的现代化学生成绩管理系统，集成了人工智能技术，是前三个实验的综合升级版本。该系统基于Spring Boot 3.x开发，采用前后端分离架构，支持角色权限控制，并集成了AI智能顾问功能。

#### 技术架构

- **后端框架**: Spring Boot 3.2.1
- **安全框架**: Spring Security + JWT
- **数据访问**: MyBatis + MySQL
- **缓存技术**: Redis
- **AI集成**: LangChain4j + 阿里云通义千问(Qwen)
- **前端技术**: 原生JavaScript + HTML/CSS（无框架依赖）
- **数据验证**: Spring Validation
- **构建工具**: Maven

#### 核心功能

##### 1. 用户管理与认证
- **角色系统**: 支持学生和教师两种角色
- **JWT认证**: 无状态身份验证机制
- **注册登录**: 学生和教师分别注册登录
- **权限控制**: 基于角色的访问控制

##### 2. 学生功能模块
- **个人信息管理**: 查看和更新个人资料
- **成绩查询**: 查看个人所有课程成绩及统计信息
- **选课管理**: 选课和退课操作
- **AI学习顾问**: 基于个人成绩数据的智能学习建议
  - 成绩分析：客观分析各科成绩，指出优势和薄弱环节
  - 知识点诊断：针对具体课程（如Java、数据结构、数据库等）分析薄弱知识点
  - 学习建议：提供具体的学习资源和练习方法
  - 实践指导：建议通过项目实践巩固知识
  - 职业规划：根据成绩特点给出技术方向建议

##### 3. 教师功能模块
- **个人信息管理**: 查看和更新个人资料
- **教学管理**: 管理所授课程和教学班
- **成绩录入**: 录入和更新学生成绩
- **成绩统计**: 查看班级成绩分布和统计信息
- **AI教学分析**: 基于班级数据的智能教学改进建议
  - 整体教学效果评价
  - 成绩分布分析
  - 具体知识点诊断
  - 教学改进建议（具体到知识点）
  - 需要关注的学生群体

##### 4. AI智能顾问系统
系统集成了基于阿里云通义千问（Qwen）的AI智能顾问，提供专业化的学习和教学分析：

- **学生AI顾问**：根据学生个人成绩情况，提供个性化学习建议
- **教师AI顾问**：根据班级成绩统计，提供教学改进方案
- **流式响应**：支持SSE（Server-Sent Events）流式输出，提供实时AI回复体验
- **专业知识点**：针对计算机专业课程（Java、数据结构、数据库等）提供具体的知识点分析

##### 5. 数据模型
系统包含以下核心实体：
- **User**: 用户认证信息（用户名、密码、角色）
- **Student**: 学生个人信息
- **Teacher**: 教师个人信息
- **Course**: 课程信息
- **TeachingClass**: 教学班（关联课程、教师、学期）
- **Enrollment**: 学生选课记录
- **Score**: 学生成绩（平时、期中、实验、期末、总分、绩点）
- **ScoreWeight**: 成绩权重配置（可配置各部分占比）

##### 6. 系统特色
- **AI驱动**: 集成先进的AI技术，提供智能化学习和教学建议
- **角色分离**: 学生和教师功能分离，权限控制明确
- **实时AI交互**: 支持流式AI回复，提供流畅的交互体验
- **数据完整性**: 支持成绩构成的完整记录（平时、期中、实验、期末）
- **灵活配置**: 成绩权重可配置，适应不同课程需求
- **安全可靠**: JWT身份验证，Spring Security权限控制

#### 项目结构

```
Exp04/
├── src/main/java/com/cqu/exp04/
│   ├── controller/         # 控制器层
│   │   ├── AuthController.java        # 认证相关
│   │   ├── CourseController.java      # 课程相关
│   │   ├── StudentController.java     # 学生相关
│   │   ├── TeacherController.java     # 教师相关
│   │   └── PasswordTestController.java # 密码测试
│   ├── dto/               # 数据传输对象
│   │   ├── AIConsultRequest.java      # AI咨询请求
│   │   ├── LoginRequest.java          # 登录请求
│   │   ├── ScoreInputRequest.java     # 成绩录入请求
│   │   ├── StudentRegisterRequest.java # 学生注册请求
│   │   └── TeacherRegisterRequest.java # 教师注册请求
│   ├── entity/            # 实体类
│   │   ├── BaseEntity.java           # 基础实体
│   │   ├── Course.java              # 课程实体
│   │   ├── Enrollment.java          # 选课实体
│   │   ├── Score.java               # 成绩实体
│   │   ├── ScoreWeight.java         # 成绩权重实体
│   │   ├── Student.java             # 学生实体
│   │   ├── Teacher.java             # 教师实体
│   │   ├── TeachingClass.java       # 教学班实体
│   │   └── User.java                # 用户实体
│   ├── mapper/            # MyBatis映射器
│   │   ├── CourseMapper.java
│   │   ├── EnrollmentMapper.java
│   │   ├── ScoreMapper.java
│   │   ├── StudentMapper.java
│   │   ├── TeacherMapper.java
│   │   ├── TeachingClassMapper.java
│   │   └── UserMapper.java
│   ├── service/           # 业务逻辑层
│   │   ├── impl/         # 业务实现
│   │   ├── AIService.java # AI服务
│   │   ├── AuthService.java # 认证服务
│   │   ├── CourseService.java # 课程服务
│   │   ├── ScoreService.java # 成绩服务
│   │   ├── StudentService.java # 学生服务
│   │   └── TeacherService.java # 教师服务
│   ├── vo/                # 视图对象
│   │   └── ...            # 各种返回对象
│   ├── config/            # 配置类
│   │   ├── LangChain4jConfig.java # LangChain4j配置
│   │   └── SecurityConfig.java # 安全配置
│   ├── security/          # 安全相关
│   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   └── JwtUtil.java   # JWT工具类
│   ├── exception/         # 异常处理
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   └── Exp04Application.java # 应用启动类
├── src/main/resources/
│   ├── mapper/            # MyBatis映射文件
│   │   └── ...            # 各实体的XML映射
│   ├── static/            # 静态资源
│   │   ├── css/           # 样式文件
│   │   ├── js/            # JavaScript文件
│   │   ├── index.html     # 主页
│   │   ├── student.html   # 学生页面
│   │   └── teacher.html   # 教师页面
│   └── application.yaml   # 应用配置文件
├── pom.xml               # Maven依赖配置
└── CLAUDE.md             # 项目开发指南
```

#### 环境要求与部署

1. **数据库配置**：
   - MySQL数据库，用户名：root，密码：12345678
   - 数据库名：stu_grade_sys
   - 需要手动创建数据库并导入表结构

2. **Redis配置**：
   - 本地Redis服务，端口6379
   - 用于JWT令牌存储和会话管理

3. **AI配置**：
   - 阿里云通义千问API密钥（通过DASHSCOPE_API_KEY环境变量设置）
   - 默认使用qwen-max模型

4. **运行方式**：
   ```bash
   # 编译运行
   mvn spring-boot:run
   
   # 或打包后运行
   mvn clean package
   java -jar target/Exp04-0.0.1-SNAPSHOT.jar
   ```

5. **访问地址**：
   - 主页：http://localhost:8080
   - 学生页面：http://localhost:8080/student.html
   - 教师页面：http://localhost:8080/teacher.html

#### 未来发展与改进方向

根据项目规划，未来改进方向包括：
1. **完善Mapper实现**：完成所有MyBatis映射器的XML实现
2. **AI功能优化**：增加对话上下文记忆和对话记录持久化
3. **Redis应用**：充分利用Redis缓存和会话管理功能
4. **微服务架构**：考虑分布式部署和微服务化改造
5. **高并发优化**：针对成绩发布等高并发场景进行性能优化
6. **中间件集成**：引入RabbitMQ等消息队列提升系统稳定性

## 项目演进历程

实验一到实验四体现了从简单到复杂的演进过程：
1. **实验一**：基础的命令行应用，展示面向对象编程思想
2. **实验二**：引入权限控制和数据库，构建基础的业务系统
3. **实验三**：前端技术的现代化，使用Vue 3构建用户界面
4. **实验四**：综合前三个实验，加入AI技术，构建完整的现代化Web应用

## 环境要求

- Java JDK 21或更高版本
- Apache Maven 3.6或更高版本
- MySQL数据库（实验二、四需要）
- Redis（实验四需要）
- 阿里云通义千问API密钥（实验四AI功能需要）

## 快速开始

1. 克隆仓库：
   ```bash
   git clone https://github.com/your-username/Java-Course-Exp.git
   ```

2. 进入特定实验目录：
   ```bash
   cd Java-Course-Exp/Exp01  # 或 Exp02, Exp03, Exp04
   ```

3. 编译和打包项目：
   ```bash
   mvn clean package
   ```

4. 运行应用程序：
   ```bash
   java -cp target/[jar-file-name].jar [main-class-name]
   ```

有关详细说明，请参阅每个实验的独立README文件。

## 注意事项

1. 本代码仅供学习参考，请勿直接抄袭
2. 代码可能存在不足或错误，欢迎指正
3. 使用前请确保理解代码逻辑
4. 使用此材料作为参考时请遵守学术诚信政策
5. 实验四需要配置数据库、Redis和AI API密钥才能正常运行

## 许可证

本项目仅供学习交流使用。