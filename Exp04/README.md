# 学生成绩管理系统后端 - Exp04

## 项目简介

这是一个基于Spring Boot的企业级学生成绩管理系统后端,结合了前两次实验的成果,并集成了AI功能。系统采用前后端分离架构,提供RESTful API接口。

### 核心特性

- **双角色权限系统**: 学生和教师两种角色,基于JWT的身份认证和授权
- **成绩管理**: 支持多维度成绩(平时、期中、实验、期末)的录入和查询
- **AI智能助手**: 集成LangChain4j,为学生提供学习建议,为教师提供教学分析
- **数据统计分析**: 自动计算平均分、绩点、成绩分布等统计信息
- **企业级架构**: 分层架构设计,代码规范,易于维护和扩展

## 技术栈

- **Java 21**: 最新LTS版本
- **Spring Boot 3.2.1**: 核心框架
- **Spring Security**: 安全认证授权
- **MyBatis 3.0.3**: 数据持久化
- **MySQL 8.0+**: 数据库
- **Redis**: 缓存支持
- **JWT (jjwt 0.12.5)**: Token认证
- **LangChain4j 0.35.0**: AI功能集成
- **阿里云通义千问(qwen-max)**: 大语言模型
- **Lombok**: 减少样板代码

## 项目结构

```
src/main/java/com/cqu/exp04/
├── config/                 # 配置类
│   ├── SecurityConfig.java        # Spring Security配置
│   └── LangChain4jConfig.java     # AI模型配置
├── controller/             # 控制器层(REST API)
│   ├── AuthController.java        # 认证接口
│   ├── StudentController.java     # 学生接口
│   ├── TeacherController.java     # 教师接口
│   └── InitController.java        # 数据初始化接口
├── service/                # 业务逻辑层
│   ├── AuthService.java           # 认证服务
│   ├── ScoreService.java          # 成绩服务
│   ├── AIService.java             # AI咨询服务
│   └── DataInitService.java       # 数据初始化服务
├── mapper/                 # MyBatis Mapper接口
│   ├── UserMapper.java
│   ├── StudentMapper.java
│   ├── TeacherMapper.java
│   ├── CourseMapper.java
│   ├── TeachingClassMapper.java
│   ├── EnrollmentMapper.java
│   └── ScoreMapper.java
├── entity/                 # 实体类
│   ├── User.java                  # 用户
│   ├── Student.java               # 学生
│   ├── Teacher.java               # 教师
│   ├── Course.java                # 课程
│   ├── TeachingClass.java         # 教学班
│   ├── Enrollment.java            # 选课记录
│   ├── Score.java                 # 成绩
│   └── ScoreWeight.java           # 成绩权重配置
├── dto/                    # 数据传输对象
│   ├── LoginRequest.java
│   ├── ScoreInputRequest.java
│   └── AIConsultRequest.java
├── vo/                     # 视图对象
│   ├── Result.java                # 统一响应结果
│   ├── LoginResponse.java
│   ├── StudentScoreVO.java
│   └── ClassScoreStatisticsVO.java
├── security/               # 安全相关
│   ├── JwtUtil.java               # JWT工具类
│   └── JwtAuthenticationFilter.java # JWT过滤器
└── exception/              # 异常处理
    └── GlobalExceptionHandler.java # 全局异常处理器

src/main/resources/
├── application.yaml        # 应用配置
├── schema.sql              # 数据库建表脚本
└── mapper/                 # MyBatis XML映射文件
    └── UserMapper.xml
```

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库配置

创建数据库:

```sql
CREATE DATABASE stu_grade_sys CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行建表脚本:

```bash
mysql -u root -p stu_grade_sys < src/main/resources/schema.sql
```

### 3. 配置文件

编辑 `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stu_grade_sys?...
    username: root
    password: your_password  # 修改为你的MySQL密码

# JWT密钥(生产环境请使用强密钥)
jwt:
  secret: your-secret-key-at-least-256-bits-long

# 阿里云通义千问API配置(用于AI功能)
langchain4j:
  dashscope:
    chat-model:
      api-key: ${DASHSCOPE_API_KEY:your-api-key}  # 设置环境变量或直接填写
      model-name: qwen-max
      temperature: 0.7
      top-p: 0.8
      enable-search: true
```

### 4. 编译和运行

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/Exp04-0.0.1-SNAPSHOT.jar
```

应用将在 `http://localhost:8080` 启动。

### 5. 初始化测试数据

启动后调用数据初始化接口:

```bash
curl -X POST http://localhost:8080/api/init/all
```

这将自动生成:
- 6名教师
- 5门课程
- 10-15个教学班
- 120名学生
- 360-600条选课记录

## API接口文档

### 认证接口

#### 1. 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "2023100001",  // 学生: 学号, 教师: 工号
  "password": "123456"       // 初始密码都是123456
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "2023100001",
    "realName": "张伟",
    "role": "STUDENT",
    "userId": 1,
    "roleId": 1
  }
}
```

### 学生接口 (需要认证,角色: STUDENT)

所有学生接口都需要在请求头中携带Token:

```http
Authorization: Bearer {token}
```

#### 1. 查询我的所有成绩

```http
GET /api/student/scores
```

响应:

```json
{
  "code": 200,
  "data": {
    "scores": [...],
    "averageScore": 85.5,
    "averageGPA": 3.5,
    "totalCredits": 16.5,
    "totalCourses": 5
  }
}
```

#### 2. AI学习建议咨询

```http
POST /api/student/ai/consult
Content-Type: application/json

{
  "message": "我的数据库成绩不太理想,该如何提高?"
}
```

响应:

```json
{
  "code": 200,
  "data": "根据你的成绩分析,数据库课程的期中成绩较低..."
}
```

### 教师接口 (需要认证,角色: TEACHER)

#### 1. 查询我的教学班列表

```http
GET /api/teacher/classes
```

#### 2. 查询教学班学生成绩

```http
GET /api/teacher/class/{classId}/scores
```

#### 3. 录入/更新学生成绩

```http
POST /api/teacher/score/input
Content-Type: application/json

{
  "teachingClassId": 1,
  "studentId": 10,
  "usualScore": 85,
  "midtermScore": 88,
  "experimentScore": 90,
  "finalScore": 87
}
```

系统会自动计算总评成绩(权重: 平时20% + 期中20% + 实验20% + 期末40%)和绩点。

#### 4. 查询教学班成绩统计

```http
GET /api/teacher/class/{classId}/statistics
```

响应:

```json
{
  "code": 200,
  "data": {
    "classNo": "TC0001",
    "courseName": "Java程序设计",
    "totalStudents": 45,
    "scoredStudents": 45,
    "averageScore": 78.5,
    "highestScore": 95.0,
    "lowestScore": 60.0,
    "excellentCount": 8,
    "goodCount": 15,
    "mediumCount": 12,
    "passCount": 7,
    "failCount": 3,
    "passRate": 93.3,
    "excellentRate": 17.8
  }
}
```

#### 5. AI教学分析咨询

```http
POST /api/teacher/ai/consult
Content-Type: application/json

{
  "teachingClassId": 1,
  "message": "请分析这个班的整体学习情况"
}
```

## 核心功能说明

### 1. JWT认证流程

1. 用户登录 → 验证用户名密码 → 生成JWT Token
2. 客户端存储Token,后续请求携带在Header中
3. JwtAuthenticationFilter拦截请求,验证Token合法性
4. 从Token中提取用户信息,设置到SecurityContext
5. Controller通过HttpServletRequest获取当前用户信息

### 2. 成绩计算规则

- **总评成绩**: 平时20% + 期中20% + 实验20% + 期末40%
- **绩点计算**:
  - 90-100: 4.0
  - 85-89: 3.7
  - 82-84: 3.3
  - 78-81: 3.0
  - 75-77: 2.7
  - 72-74: 2.3
  - 68-71: 2.0
  - 64-67: 1.5
  - 60-63: 1.0
  - <60: 0.0

### 3. AI功能实现

#### 学生AI助手

根据学生的所有成绩数据,提供个性化学习建议:
- 分析各科成绩表现
- 识别薄弱环节
- 提供学习方法建议
- 给出未来发展方向

#### 教师AI助手

根据教学班的成绩统计数据,提供教学分析:
- 整体教学效果评价
- 成绩分布合理性分析
- 学困生群体识别
- 教学改进建议

### 4. 数据权限控制

- **学生**: 只能查看自己的成绩,不能查看其他学生
- **教师**: 只能查看和管理自己授课的教学班
- **跨角色访问**: 通过Spring Security拦截,JWT中存储角色信息

## 设计模式应用

1. **分层架构模式**: Controller → Service → Mapper,职责清晰
2. **DTO/VO模式**: 请求使用DTO,响应使用VO,与实体类分离
3. **策略模式**: 成绩计算策略可扩展
4. **模板方法模式**: BaseEntity抽象基类
5. **单例模式**: Spring Bean默认单例
6. **工厂模式**: JWT Token生成
7. **适配器模式**: LangChain4j接入不同AI模型

## 开发注意事项

### 1. 数据库连接

确保MySQL和Redis都已启动:

```bash
# 启动MySQL
sudo service mysql start

# 启动Redis
redis-server
```

### 2. 阿里云通义千问API配置

AI功能使用阿里云通义千问(DashScope)模型。配置方式:

```bash
# 方式1: 环境变量(推荐)
export DASHSCOPE_API_KEY=sk-xxxxx

# 方式2: 直接修改application.yaml
langchain4j:
  dashscope:
    chat-model:
      api-key: sk-xxxxx
```

**获取API Key**:
1. 访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)
2. 开通DashScope服务
3. 创建API Key

**优势**:
- 国内访问稳定,无需翻墙
- 中文理解能力强
- 价格相对便宜
- 响应速度快

如果没有API Key,可以暂时注释掉AI相关代码。

### 3. 跨域配置

所有Controller都添加了`@CrossOrigin`注解,支持前端跨域访问。

### 4. 日志查看

MyBatis SQL日志已开启,可在控制台查看执行的SQL语句。

## 测试账号

初始化数据后,所有账号的默认密码都是 `123456`。

### 学生账号

- 学号: `2023100001` ~ `2023100120`
- 密码: `123456`

### 教师账号

- 工号: `T1001` ~ `T1006`
- 密码: `123456`

## 后续扩展建议

1. **Redis缓存**: 在查询接口添加缓存,提高性能
2. **分页查询**: 对列表接口添加分页支持
3. **文件上传**: 支持成绩批量导入(Excel)
4. **消息通知**: 成绩发布后通知学生
5. **数据导出**: 导出成绩报表(PDF/Excel)
6. **日志审计**: 记录所有数据变更操作
7. **单元测试**: 补充Service层和Controller层测试
8. **API文档**: 集成Swagger/Knife4j生成在线文档

## 常见问题

### Q1: 启动报错 "Table 'stu_grade_sys.user' doesn't exist"

A: 需要先执行schema.sql建表脚本。

### Q2: JWT验证失败

A: 检查请求头格式是否正确: `Authorization: Bearer {token}`

### Q3: AI咨询无响应

A: 检查通义千问(DashScope) API Key是否配置正确。确认:
- API Key已正确设置在`application.yaml`或环境变量中
- API Key有效且有足够的调用额度
- 网络连接正常(国内可直接访问,无需翻墙)

### Q4: 成绩未自动计算总分

A: 总分只有在四项成绩(平时、期中、实验、期末)都录入后才会自动计算。

## 许可证

本项目仅供学习交流使用。

---

**重庆大学计算机学院 Java企业级应用开发课程实验**

**实验四: 学生成绩管理系统后端 (Spring Boot + MyBatis + JWT + AI)**
