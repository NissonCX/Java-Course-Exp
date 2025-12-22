# 前端开发指南 - 学生成绩管理系统

## 项目概述

本文档为重庆大学 Java 企业级应用开发 Exp04 项目的前端开发指南。后端基于 Spring Boot 3.2.1 + MyBatis + MySQL + Redis，提供 RESTful API 接口。

**后端地址**: `http://localhost:8080`

**主要功能模块**:
- 用户认证（学生/教师注册登录）
- 学生个人信息管理、选课、成绩查询
- 教师教学班管理、成绩录入、统计分析
- AI 智能咨询（学习建议/教学分析）
- 课程与教学班信息查询

---

## API 统一规范

### 1. 基础配置

**Base URL**: `http://localhost:8080`

**CORS**: 后端已配置 `@CrossOrigin`，支持跨域请求

### 2. 统一响应格式

所有 API 返回统一的 `Result<T>` 格式：

```typescript
interface Result<T> {
  code: number;        // 200: 成功, 500: 失败
  message: string;     // 提示信息
  data: T | null;      // 业务数据
}
```

**成功响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**错误响应示例**:
```json
{
  "code": 500,
  "message": "用户名或密码错误",
  "data": null
}
```

### 3. 认证机制

**JWT Token 认证**:
- 登录/注册成功后，响应中包含 `token` 字段
- 后续需要鉴权的请求需在请求头中携带 Token：
  ```
  Authorization: Bearer <token>
  ```
- Token 有效期：24 小时（86400000 毫秒）

**权限控制**:
- 学生 API 路径：`/api/student/*` - 需要学生角色
- 教师 API 路径：`/api/teacher/*` - 需要教师角色
- 公共 API 路径：`/api/auth/*`, `/api/course/*` - 无需认证或公开访问

---

## API 接口详细说明

### 一、认证模块 (`/api/auth`)

#### 1.1 用户登录

**接口**: `POST /api/auth/login`

**请求头**: 无需认证

**请求体**:
```typescript
interface LoginRequest {
  username: string;  // 必填，用户名（学号或教工号）
  password: string;  // 必填，密码
}
```

**响应数据**:
```typescript
interface LoginResponse {
  token: string;      // JWT Token
  username: string;   // 用户名
  realName: string;   // 真实姓名
  role: string;       // 角色: STUDENT | TEACHER | ADMIN
  userId: number;     // 用户ID
  roleId: number;     // 角色关联ID (student_id 或 teacher_id)
}
```

**示例**:
```javascript
// 请求
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: '2020123456',
    password: '123456'
  })
});

// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1...",
    "username": "2020123456",
    "realName": "张三",
    "role": "STUDENT",
    "userId": 1,
    "roleId": 1
  }
}
```

---

#### 1.2 学生注册

**接口**: `POST /api/auth/register/student`

**请求头**: 无需认证

**请求体**:
```typescript
interface StudentRegisterRequest {
  studentNo: string;        // 必填，10位数字学号
  password: string;         // 必填，密码
  name: string;             // 必填，姓名
  gender: 'MALE' | 'FEMALE'; // 必填，性别
  birthDate?: string;       // 可选，生日 (格式: YYYY-MM-DD)
  major: string;            // 必填，专业
  className?: string;       // 可选，班级
  grade?: number;           // 可选，年级
  enrollmentYear?: number;  // 可选，入学年份
  email?: string;           // 可选，邮箱
  phone?: string;           // 可选，电话
}
```

**响应数据**: 与登录响应相同 (`LoginResponse`)

**注意事项**:
- `studentNo` 必须是 10 位数字，不能重复
- `gender` 只能是 `MALE` 或 `FEMALE`（大写）

---

#### 1.3 教师注册

**接口**: `POST /api/auth/register/teacher`

**请求头**: 无需认证

**请求体**:
```typescript
interface TeacherRegisterRequest {
  teacherNo: string;        // 必填，教工号
  password: string;         // 必填，密码
  name: string;             // 必填，姓名
  gender: 'MALE' | 'FEMALE'; // 必填，性别
  title?: string;           // 可选，职称
  department: string;       // 必填，院系
  email?: string;           // 可选，邮箱
  phone?: string;           // 可选，电话
}
```

**响应数据**: 与登录响应相同 (`LoginResponse`)

---

#### 1.4 测试接口

**接口**: `GET /api/auth/test`

**请求头**: 无需认证

**响应数据**: `"API is working!"`

用于测试后端服务是否正常运行。

---

### 二、学生模块 (`/api/student`)

所有学生 API 均需要在请求头中携带有效的学生 Token。

#### 2.1 获取个人信息

**接口**: `GET /api/student/profile`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```typescript
interface Student {
  id: number;
  studentNo: string;
  userId: number;
  name: string;
  gender: 'MALE' | 'FEMALE';
  birthDate: string;        // YYYY-MM-DD
  major: string;
  className: string;
  grade: number;
  enrollmentYear: number;
  createTime: string;       // ISO 8601 格式
  updateTime: string;
}
```

---

#### 2.2 更新个人信息

**接口**: `PUT /api/student/profile`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface StudentUpdateRequest {
  name?: string;
  gender?: 'MALE' | 'FEMALE';
  birthDate?: string;
  major?: string;
  className?: string;
  grade?: number;
  enrollmentYear?: number;
}
```

**响应数据**: `"更新成功"`

**注意**: 只需传递要更新的字段，未传递的字段保持不变。

---

#### 2.3 查询我的成绩

**接口**: `GET /api/student/scores`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```typescript
interface ScoreResponse {
  scores: Score[];           // 成绩列表
  statistics: {
    totalCourses: number;    // 总课程数
    averageScore: number;    // 平均分
    totalCredits: number;    // 总学分
    averageGPA: number;      // 平均绩点
  };
}

interface Score {
  id: number;
  enrollmentId: number;
  studentId: number;
  teachingClassId: number;
  usualScore: number;        // 平时成绩
  midtermScore: number;      // 期中成绩
  experimentScore: number;   // 实验成绩
  finalScore: number;        // 期末成绩
  totalScore: number;        // 总评成绩
  gradePoint: number;        // 绩点
  usualScoreTime: string;
  midtermScoreTime: string;
  experimentScoreTime: string;
  finalScoreTime: string;
  totalScoreTime: string;
  // 关联对象
  student: Student;
  teachingClass: TeachingClass;
}
```

---

#### 2.4 查询我的选课

**接口**: `GET /api/student/enrollments`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```typescript
interface Enrollment {
  id: number;
  studentId: number;
  teachingClassId: number;
  enrollTime: string;
  status: number;            // 1-正常, 0-退课
  createTime: string;
  updateTime: string;
  // 关联对象
  teachingClass?: TeachingClass;
}
```

---

#### 2.5 选课

**接口**: `POST /api/student/enroll`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface EnrollRequest {
  teachingClassId: number;  // 教学班ID
}
```

**响应数据**: `"选课成功"`

---

#### 2.6 退课

**接口**: `DELETE /api/student/enroll/{enrollmentId}`

**请求头**: `Authorization: Bearer <token>`

**路径参数**: `enrollmentId` - 选课记录ID

**响应数据**: `"退课成功"`

---

#### 2.7 AI 学习建议咨询

**接口**: `POST /api/student/ai/consult`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface AIConsultRequest {
  message: string;  // 必填，咨询问题
}
```

**响应数据**: `string` - AI 返回的学习建议

**示例**:
```javascript
const response = await fetch('http://localhost:8080/api/student/ai/consult', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    message: '我的数学成绩不太好，该如何提高？'
  })
});

// 响应
{
  "code": 200,
  "message": "success",
  "data": "根据您的成绩分析，建议您..."
}
```

---

### 三、教师模块 (`/api/teacher`)

所有教师 API 均需要在请求头中携带有效的教师 Token。

#### 3.1 获取个人信息

**接口**: `GET /api/teacher/profile`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```typescript
interface Teacher {
  id: number;
  teacherNo: string;
  userId: number;
  name: string;
  gender: 'MALE' | 'FEMALE';
  title: string;
  department: string;
  email: string;
  phone: string;
  createTime: string;
  updateTime: string;
}
```

---

#### 3.2 更新个人信息

**接口**: `PUT /api/teacher/profile`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface TeacherUpdateRequest {
  name?: string;
  gender?: 'MALE' | 'FEMALE';
  title?: string;
  department?: string;
  email?: string;
  phone?: string;
}
```

**响应数据**: `"更新成功"`

---

#### 3.3 查询我的教学班

**接口**: `GET /api/teacher/classes`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```typescript
interface TeachingClass {
  id: number;
  classNo: string;
  courseId: number;
  teacherId: number;
  semester: string;          // 例如: 2024-2025-1
  maxStudents: number;
  currentStudents: number;
  classroom: string;
  schedule: string;
  status: number;            // 1-开课中, 0-已结课
  createTime: string;
  updateTime: string;
  // 关联对象
  course?: Course;
  teacher?: Teacher;
}
```

---

#### 3.4 查询教学班学生列表

**接口**: `GET /api/teacher/class/{classId}/students`

**请求头**: `Authorization: Bearer <token>`

**路径参数**: `classId` - 教学班ID

**响应数据**:
```typescript
type StudentInfo = {
  id: number;
  studentNo: string;
  name: string;
  gender: string;
  major: string;
  className: string;
  grade: number;
  enrollmentId: number;      // 选课记录ID
  enrollTime: string;
}[]
```

---

#### 3.5 查询教学班成绩列表

**接口**: `GET /api/teacher/class/{classId}/scores`

**请求头**: `Authorization: Bearer <token>`

**路径参数**: `classId` - 教学班ID

**响应数据**: `Score[]` - 成绩列表（格式见 2.3）

---

#### 3.6 录入/更新学生成绩

**接口**: `POST /api/teacher/score/input`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface ScoreInputRequest {
  teachingClassId: number;    // 必填，教学班ID
  studentId: number;          // 必填，学生ID
  usualScore?: number;        // 可选，平时成绩 (0-100)
  midtermScore?: number;      // 可选，期中成绩 (0-100)
  experimentScore?: number;   // 可选，实验成绩 (0-100)
  finalScore?: number;        // 可选，期末成绩 (0-100)
}
```

**响应数据**: `"成绩录入成功"`

**注意**:
- 只传递要录入/更新的成绩字段
- 后端会根据成绩权重自动计算总评成绩和绩点
- 可以多次调用更新单项成绩

---

#### 3.7 批量录入成绩

**接口**: `POST /api/teacher/score/batch`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface BatchScoreRequest {
  teachingClassId: number;
  scores: ScoreInputRequest[];  // 多个学生的成绩
}
```

**响应数据**: `"批量录入成功"`

**示例**:
```javascript
{
  "teachingClassId": 1,
  "scores": [
    {
      "studentId": 1,
      "usualScore": 85,
      "midtermScore": 78,
      "experimentScore": 90,
      "finalScore": 88
    },
    {
      "studentId": 2,
      "usualScore": 92,
      "midtermScore": 85,
      "experimentScore": 95,
      "finalScore": 90
    }
  ]
}
```

---

#### 3.8 查询教学班成绩统计

**接口**: `GET /api/teacher/class/{classId}/statistics`

**请求头**: `Authorization: Bearer <token>`

**路径参数**: `classId` - 教学班ID

**响应数据**:
```typescript
interface ClassScoreStatistics {
  teachingClassId: number;
  classNo: string;
  courseName: string;
  semester: string;
  totalStudents: number;     // 总学生数
  scoredStudents: number;    // 已录入成绩学生数
  averageScore: number;      // 平均分
  highestScore: number;      // 最高分
  lowestScore: number;       // 最低分
  excellentCount: number;    // 优秀人数 (90-100)
  goodCount: number;         // 良好人数 (80-89)
  mediumCount: number;       // 中等人数 (70-79)
  passCount: number;         // 及格人数 (60-69)
  failCount: number;         // 不及格人数 (<60)
  passRate: number;          // 及格率 (0-1)
  excellentRate: number;     // 优秀率 (0-1)
}
```

**用途**: 适用于生成成绩分析图表、分布统计等可视化展示。

---

#### 3.9 AI 教学分析咨询

**接口**: `POST /api/teacher/ai/consult`

**请求头**: `Authorization: Bearer <token>`

**请求体**:
```typescript
interface AIConsultRequest {
  teachingClassId: number;  // 必填，教学班ID
  message: string;          // 必填，咨询问题
}
```

**响应数据**: `string` - AI 返回的教学分析建议

**示例**:
```javascript
{
  "teachingClassId": 1,
  "message": "如何提高班级整体成绩？"
}
```

---

### 四、课程模块 (`/api/course`)

公共接口，部分需要认证（取决于具体业务需求）。

#### 4.1 查询所有课程

**接口**: `GET /api/course/list`

**请求头**: 无需认证（公开）

**响应数据**:
```typescript
interface Course {
  id: number;
  courseNo: string;
  courseName: string;
  credit: number;            // 学分（如 3.5）
  hours: number;             // 学时
  courseType: string;        // 课程类型（必修/选修）
  description: string;       // 课程描述
  createTime: string;
  updateTime: string;
}[]
```

---

#### 4.2 查询课程详情

**接口**: `GET /api/course/{id}`

**请求头**: 无需认证（公开）

**路径参数**: `id` - 课程ID

**响应数据**: `Course` - 单个课程对象

---

#### 4.3 查询可选教学班

**接口**: `GET /api/course/classes?semester={semester}`

**请求头**: 无需认证（公开）

**查询参数**:
- `semester` (可选) - 学期筛选，例如 `2024-2025-1`

**响应数据**:
```typescript
type ClassInfo = {
  id: number;
  classNo: string;
  courseId: number;
  courseName: string;
  teacherId: number;
  teacherName: string;
  semester: string;
  maxStudents: number;
  currentStudents: number;
  classroom: string;
  schedule: string;
  status: number;
}[]
```

**用途**: 学生选课时展示可选的教学班列表。

---

#### 4.4 查询课程的教学班列表

**接口**: `GET /api/course/{courseId}/classes`

**请求头**: 无需认证（公开）

**路径参数**: `courseId` - 课程ID

**响应数据**: `TeachingClass[]` - 该课程的所有教学班

---

#### 4.5 查询教学班详情

**接口**: `GET /api/course/class/{classId}`

**请求头**: 无需认证（公开）

**路径参数**: `classId` - 教学班ID

**响应数据**:
```typescript
interface ClassDetail {
  id: number;
  classNo: string;
  course: Course;
  teacher: Teacher;
  semester: string;
  maxStudents: number;
  currentStudents: number;
  classroom: string;
  schedule: string;
  status: number;
}
```

---

## 数据模型详细说明

### 1. 枚举类型

```typescript
// 性别
enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE'
}

// 用户角色
enum Role {
  STUDENT = 'STUDENT',
  TEACHER = 'TEACHER',
  ADMIN = 'ADMIN'
}

// 教学班状态
enum ClassStatus {
  ACTIVE = 1,     // 开课中
  CLOSED = 0      // 已结课
}

// 选课状态
enum EnrollmentStatus {
  ACTIVE = 1,     // 正常
  DROPPED = 0     // 已退课
}
```

### 2. 成绩计算规则

总评成绩由四部分组成，权重可在数据库 `score_weight` 表配置（默认值）：

- **平时成绩**: 20%
- **期中成绩**: 20%
- **实验成绩**: 20%
- **期末成绩**: 40%

**总评计算公式**:
```
totalScore = usualScore × 0.2 + midtermScore × 0.2 + experimentScore × 0.2 + finalScore × 0.4
```

**绩点计算规则**:
- 90-100: 4.0
- 80-89: 3.0
- 70-79: 2.0
- 60-69: 1.0
- <60: 0.0

### 3. 日期时间格式

- **日期**: `YYYY-MM-DD` (例如: `2024-01-15`)
- **日期时间**: ISO 8601 格式 `YYYY-MM-DDTHH:mm:ss` (例如: `2024-01-15T14:30:00`)

---

## 前端技术栈建议

### 推荐框架

根据项目 Exp03 使用 Vue 3 + TypeScript 的经验：

1. **框架**: Vue 3 + TypeScript
2. **路由**: Vue Router 4
3. **状态管理**: Pinia
4. **UI 组件库**:
   - Element Plus (适合管理后台)
   - Naive UI (现代化设计)
   - Ant Design Vue (企业级)
5. **HTTP 请求**: Axios
6. **图表库**: ECharts (用于成绩统计可视化)
7. **构建工具**: Vite

### 项目结构建议

```
frontend/
├── src/
│   ├── api/                 # API 接口封装
│   │   ├── auth.ts
│   │   ├── student.ts
│   │   ├── teacher.ts
│   │   └── course.ts
│   ├── components/          # 通用组件
│   │   ├── Header.vue
│   │   ├── Sidebar.vue
│   │   └── ScoreChart.vue
│   ├── views/               # 页面视图
│   │   ├── Login.vue
│   │   ├── student/
│   │   │   ├── Dashboard.vue
│   │   │   ├── Profile.vue
│   │   │   ├── Scores.vue
│   │   │   ├── Enroll.vue
│   │   │   └── AIConsult.vue
│   │   └── teacher/
│   │       ├── Dashboard.vue
│   │       ├── Classes.vue
│   │       ├── ScoreInput.vue
│   │       ├── Statistics.vue
│   │       └── AIConsult.vue
│   ├── stores/              # Pinia 状态管理
│   │   ├── user.ts
│   │   └── course.ts
│   ├── router/              # 路由配置
│   │   └── index.ts
│   ├── utils/               # 工具函数
│   │   ├── request.ts       # Axios 封装
│   │   └── auth.ts          # Token 管理
│   ├── types/               # TypeScript 类型定义
│   │   ├── api.ts
│   │   ├── entity.ts
│   │   └── dto.ts
│   └── App.vue
├── package.json
└── vite.config.ts
```

---

## 代码实现示例

### 1. Axios 封装 (`utils/request.ts`)

```typescript
import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';

const request: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 自动添加 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一错误处理
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res;
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      localStorage.removeItem('token');
      window.location.href = '/login';
    } else {
      ElMessage.error(error.message || '网络错误');
    }
    return Promise.reject(error);
  }
);

export default request;
```

---

### 2. API 接口封装 (`api/auth.ts`)

```typescript
import request from '@/utils/request';
import type { Result, LoginRequest, LoginResponse, StudentRegisterRequest } from '@/types/api';

export const authAPI = {
  // 登录
  login(data: LoginRequest) {
    return request.post<Result<LoginResponse>>('/api/auth/login', data);
  },

  // 学生注册
  registerStudent(data: StudentRegisterRequest) {
    return request.post<Result<LoginResponse>>('/api/auth/register/student', data);
  },

  // 教师注册
  registerTeacher(data: TeacherRegisterRequest) {
    return request.post<Result<LoginResponse>>('/api/auth/register/teacher', data);
  },

  // 测试接口
  test() {
    return request.get<Result<string>>('/api/auth/test');
  }
};
```

---

### 3. 学生 API 封装 (`api/student.ts`)

```typescript
import request from '@/utils/request';
import type { Result, Student, Score, Enrollment } from '@/types/api';

export const studentAPI = {
  // 获取个人信息
  getProfile() {
    return request.get<Result<Student>>('/api/student/profile');
  },

  // 更新个人信息
  updateProfile(data: Partial<Student>) {
    return request.put<Result<string>>('/api/student/profile', data);
  },

  // 查询我的成绩
  getMyScores() {
    return request.get<Result<{
      scores: Score[];
      statistics: {
        totalCourses: number;
        averageScore: number;
        totalCredits: number;
        averageGPA: number;
      };
    }>>('/api/student/scores');
  },

  // 查询我的选课
  getMyEnrollments() {
    return request.get<Result<Enrollment[]>>('/api/student/enrollments');
  },

  // 选课
  enrollCourse(teachingClassId: number) {
    return request.post<Result<string>>('/api/student/enroll', { teachingClassId });
  },

  // 退课
  dropCourse(enrollmentId: number) {
    return request.delete<Result<string>>(`/api/student/enroll/${enrollmentId}`);
  },

  // AI 学习建议咨询
  aiConsult(message: string) {
    return request.post<Result<string>>('/api/student/ai/consult', { message });
  }
};
```

---

### 4. Pinia Store 示例 (`stores/user.ts`)

```typescript
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { authAPI } from '@/api/auth';
import type { LoginRequest, LoginResponse } from '@/types/api';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<LoginResponse | null>(null);

  // 登录
  const login = async (loginData: LoginRequest) => {
    const res = await authAPI.login(loginData);
    token.value = res.data.token;
    userInfo.value = res.data;
    localStorage.setItem('token', res.data.token);
    localStorage.setItem('userInfo', JSON.stringify(res.data));
  };

  // 登出
  const logout = () => {
    token.value = '';
    userInfo.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
  };

  // 初始化用户信息（从本地存储恢复）
  const initUserInfo = () => {
    const storedInfo = localStorage.getItem('userInfo');
    if (storedInfo) {
      userInfo.value = JSON.parse(storedInfo);
    }
  };

  return {
    token,
    userInfo,
    login,
    logout,
    initUserInfo
  };
});
```

---

### 5. 路由配置 (`router/index.ts`)

```typescript
import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/student',
    name: 'Student',
    component: () => import('@/layouts/StudentLayout.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/student/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue')
      },
      {
        path: 'scores',
        name: 'StudentScores',
        component: () => import('@/views/student/Scores.vue')
      },
      {
        path: 'enroll',
        name: 'StudentEnroll',
        component: () => import('@/views/student/Enroll.vue')
      },
      {
        path: 'ai-consult',
        name: 'StudentAIConsult',
        component: () => import('@/views/student/AIConsult.vue')
      }
    ]
  },
  {
    path: '/teacher',
    name: 'Teacher',
    component: () => import('@/layouts/TeacherLayout.vue'),
    meta: { requiresAuth: true, role: 'TEACHER' },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue')
      },
      {
        path: 'classes',
        name: 'TeacherClasses',
        component: () => import('@/views/teacher/Classes.vue')
      },
      {
        path: 'score-input/:classId',
        name: 'TeacherScoreInput',
        component: () => import('@/views/teacher/ScoreInput.vue')
      },
      {
        path: 'statistics/:classId',
        name: 'TeacherStatistics',
        component: () => import('@/views/teacher/Statistics.vue')
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫 - 验证登录和权限
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.meta.role && userInfo.role !== to.meta.role) {
    next('/login');
  } else {
    next();
  }
});

export default router;
```

---

### 6. TypeScript 类型定义 (`types/api.ts`)

```typescript
// 统一响应结果
export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

// 登录请求
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应
export interface LoginResponse {
  token: string;
  username: string;
  realName: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  userId: number;
  roleId: number;
}

// 学生注册请求
export interface StudentRegisterRequest {
  studentNo: string;
  password: string;
  name: string;
  gender: 'MALE' | 'FEMALE';
  birthDate?: string;
  major: string;
  className?: string;
  grade?: number;
  enrollmentYear?: number;
  email?: string;
  phone?: string;
}

// 学生实体
export interface Student {
  id: number;
  studentNo: string;
  userId: number;
  name: string;
  gender: 'MALE' | 'FEMALE';
  birthDate: string;
  major: string;
  className: string;
  grade: number;
  enrollmentYear: number;
  createTime: string;
  updateTime: string;
}

// 教师实体
export interface Teacher {
  id: number;
  teacherNo: string;
  userId: number;
  name: string;
  gender: 'MALE' | 'FEMALE';
  title: string;
  department: string;
  email: string;
  phone: string;
  createTime: string;
  updateTime: string;
}

// 课程实体
export interface Course {
  id: number;
  courseNo: string;
  courseName: string;
  credit: number;
  hours: number;
  courseType: string;
  description: string;
  createTime: string;
  updateTime: string;
}

// 教学班实体
export interface TeachingClass {
  id: number;
  classNo: string;
  courseId: number;
  teacherId: number;
  semester: string;
  maxStudents: number;
  currentStudents: number;
  classroom: string;
  schedule: string;
  status: number;
  createTime: string;
  updateTime: string;
  course?: Course;
  teacher?: Teacher;
}

// 成绩实体
export interface Score {
  id: number;
  enrollmentId: number;
  studentId: number;
  teachingClassId: number;
  usualScore: number;
  midtermScore: number;
  experimentScore: number;
  finalScore: number;
  totalScore: number;
  gradePoint: number;
  usualScoreTime: string;
  midtermScoreTime: string;
  experimentScoreTime: string;
  finalScoreTime: string;
  totalScoreTime: string;
  student?: Student;
  teachingClass?: TeachingClass;
}

// 选课记录
export interface Enrollment {
  id: number;
  studentId: number;
  teachingClassId: number;
  enrollTime: string;
  status: number;
  createTime: string;
  updateTime: string;
  teachingClass?: TeachingClass;
}

// 成绩录入请求
export interface ScoreInputRequest {
  teachingClassId: number;
  studentId: number;
  usualScore?: number;
  midtermScore?: number;
  experimentScore?: number;
  finalScore?: number;
}

// 教学班统计
export interface ClassScoreStatistics {
  teachingClassId: number;
  classNo: string;
  courseName: string;
  semester: string;
  totalStudents: number;
  scoredStudents: number;
  averageScore: number;
  highestScore: number;
  lowestScore: number;
  excellentCount: number;
  goodCount: number;
  mediumCount: number;
  passCount: number;
  failCount: number;
  passRate: number;
  excellentRate: number;
}

// AI 咨询请求
export interface AIConsultRequest {
  message: string;
  teachingClassId?: number; // 教师咨询时必填
}
```

---

## 常见问题与注意事项

### 1. CORS 跨域问题

后端已配置 `@CrossOrigin`，前端开发时无需额外配置代理。如果遇到跨域问题，检查：
- 后端是否正确启动
- 请求 URL 是否正确
- 浏览器控制台是否有其他错误

### 2. Token 过期处理

Token 有效期为 24 小时。前端需要在以下情况处理 Token 过期：
- 收到 401 响应时，跳转到登录页
- 应用启动时检查 Token 是否存在
- 建议实现自动续期机制（可选）

### 3. 日期格式处理

后端返回的日期时间可能包含时区信息，前端显示时需要格式化：

```typescript
import dayjs from 'dayjs';

// 格式化日期
const formatDate = (dateStr: string) => {
  return dayjs(dateStr).format('YYYY-MM-DD');
};

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss');
};
```

### 4. 数字精度处理

成绩分数使用 `BigDecimal` 存储，前端显示时建议：

```typescript
// 保留两位小数
const formatScore = (score: number) => {
  return score?.toFixed(2) || '-';
};
```

### 5. 性别枚举显示

后端返回 `MALE` 和 `FEMALE`，前端显示时需要转换：

```typescript
const genderMap = {
  MALE: '男',
  FEMALE: '女'
};

const getGenderText = (gender: 'MALE' | 'FEMALE') => {
  return genderMap[gender];
};
```

### 6. 成绩统计图表

使用 ECharts 绘制成绩分布图：

```typescript
import * as echarts from 'echarts';

const drawScoreChart = (statistics: ClassScoreStatistics) => {
  const chartDom = document.getElementById('score-chart')!;
  const myChart = echarts.init(chartDom);

  const option = {
    title: { text: '成绩分布统计' },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: ['优秀\n90-100', '良好\n80-89', '中等\n70-79', '及格\n60-69', '不及格\n<60']
    },
    yAxis: { type: 'value' },
    series: [{
      data: [
        statistics.excellentCount,
        statistics.goodCount,
        statistics.mediumCount,
        statistics.passCount,
        statistics.failCount
      ],
      type: 'bar',
      itemStyle: {
        color: (params: any) => {
          const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'];
          return colors[params.dataIndex];
        }
      }
    }]
  };

  myChart.setOption(option);
};
```

---

## 调试与测试

### 1. 测试后端连接

在浏览器或使用 Postman 访问测试接口：

```
GET http://localhost:8080/api/auth/test
```

预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": "API is working!"
}
```

### 2. 测试数据

后端项目包含 `data.sql` 初始化测试数据，可直接使用以下账号登录：

查看 `/Users/nissoncx/code/Java-Course-Exp-main/Exp04/src/main/resources/data.sql` 了解测试账号。

### 3. 开发环境运行

```bash
# 启动后端
cd /Users/nissoncx/code/Java-Course-Exp-main/Exp04
mvn spring-boot:run

# 启动前端（假设使用 Vite）
cd frontend
npm install
npm run dev
```

---

## API 接口清单速查表

| 模块 | 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|------|
| **认证** | POST | `/api/auth/login` | 否 | 用户登录 |
| | POST | `/api/auth/register/student` | 否 | 学生注册 |
| | POST | `/api/auth/register/teacher` | 否 | 教师注册 |
| | GET | `/api/auth/test` | 否 | 测试接口 |
| **学生** | GET | `/api/student/profile` | 是 | 获取个人信息 |
| | PUT | `/api/student/profile` | 是 | 更新个人信息 |
| | GET | `/api/student/scores` | 是 | 查询我的成绩 |
| | GET | `/api/student/enrollments` | 是 | 查询我的选课 |
| | POST | `/api/student/enroll` | 是 | 选课 |
| | DELETE | `/api/student/enroll/{id}` | 是 | 退课 |
| | POST | `/api/student/ai/consult` | 是 | AI 学习咨询 |
| **教师** | GET | `/api/teacher/profile` | 是 | 获取个人信息 |
| | PUT | `/api/teacher/profile` | 是 | 更新个人信息 |
| | GET | `/api/teacher/classes` | 是 | 查询我的教学班 |
| | GET | `/api/teacher/class/{id}/students` | 是 | 教学班学生列表 |
| | GET | `/api/teacher/class/{id}/scores` | 是 | 教学班成绩列表 |
| | POST | `/api/teacher/score/input` | 是 | 录入/更新成绩 |
| | POST | `/api/teacher/score/batch` | 是 | 批量录入成绩 |
| | GET | `/api/teacher/class/{id}/statistics` | 是 | 教学班成绩统计 |
| | POST | `/api/teacher/ai/consult` | 是 | AI 教学分析 |
| **课程** | GET | `/api/course/list` | 否 | 查询所有课程 |
| | GET | `/api/course/{id}` | 否 | 查询课程详情 |
| | GET | `/api/course/classes` | 否 | 查询可选教学班 |
| | GET | `/api/course/{id}/classes` | 否 | 课程教学班列表 |
| | GET | `/api/course/class/{id}` | 否 | 教学班详情 |

---

## 附录

### 相关文档

- **后端配置**: `src/main/resources/application.yaml`
- **数据库 Schema**: `src/main/resources/schema.sql`
- **测试数据**: `src/main/resources/data.sql`
- **项目说明**: `CLAUDE.md`

### 技术支持

如遇到问题，请检查：
1. 后端服务是否正常运行（端口 8080）
2. MySQL 数据库是否正常连接
3. Redis 服务是否启动
4. Token 是否正确携带
5. 请求/响应数据格式是否符合规范

---

**文档版本**: v1.0
**更新日期**: 2025-12-22
**后端版本**: Spring Boot 3.2.1
