# 数据初始化和注册指南

## 数据库初始化

### 1. 创建数据库和表结构

```bash
# 执行建表脚本
mysql -u root -p < src/main/resources/schema.sql
```

### 2. 插入初始测试数据

```bash
# 执行数据插入脚本
mysql -u root -p < src/main/resources/data.sql
```

这将自动创建:
- ✅ 6名教师 (T1001 ~ T1006)
- ✅ 5门课程 (Java、数据结构、数据库、操作系统、计算机网络)
- ✅ 11个教学班
- ✅ 10名示例学生 (2023100001 ~ 2023100010)
- ✅ 示例选课记录

**默认密码**: 所有账号的密码都是 `123456`

## 用户注册

系统提供了学生和教师的注册功能。

### 学生注册

**接口**: `POST /api/auth/register/student`

**请求体**:
```json
{
  "studentNo": "2023100011",
  "password": "123456",
  "name": "新学生",
  "gender": "MALE",
  "birthDate": "2004-01-01",
  "major": "计算机科学与技术",
  "className": "2023级1班",
  "grade": 2023,
  "enrollmentYear": 2023,
  "email": "2023100011@stu.cqu.edu.cn",
  "phone": "13800001234"
}
```

**字段说明**:
- `studentNo`: 学号 (必填,10位数字)
- `password`: 密码 (必填)
- `name`: 姓名 (必填)
- `gender`: 性别 (必填, MALE或FEMALE)
- `birthDate`: 出生日期 (可选)
- `major`: 专业 (必填)
- `className`: 班级 (可选)
- `grade`: 年级 (可选,默认2024)
- `enrollmentYear`: 入学年份 (可选,默认2024)
- `email`: 邮箱 (可选)
- `phone`: 电话 (可选)

**响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "2023100011",
    "realName": "新学生",
    "role": "STUDENT",
    "userId": 11,
    "roleId": 11
  }
}
```

注册成功后会自动登录并返回JWT Token。

### 教师注册

**接口**: `POST /api/auth/register/teacher`

**请求体**:
```json
{
  "teacherNo": "T1007",
  "password": "123456",
  "name": "新教师",
  "gender": "FEMALE",
  "title": "讲师",
  "department": "计算机学院",
  "email": "T1007@cqu.edu.cn",
  "phone": "13800002345"
}
```

**字段说明**:
- `teacherNo`: 教工号 (必填, T开头+4位数字)
- `password`: 密码 (必填)
- `name`: 姓名 (必填)
- `gender`: 性别 (必填, MALE或FEMALE)
- `title`: 职称 (可选, 如: 教授、副教授、讲师、助教)
- `department`: 院系 (必填)
- `email`: 邮箱 (可选)
- `phone`: 电话 (可选)

**响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "T1007",
    "realName": "新教师",
    "role": "TEACHER",
    "userId": 7,
    "roleId": 7
  }
}
```

## cURL测试示例

### 学生注册测试

```bash
curl -X POST http://localhost:8080/api/auth/register/student \
  -H "Content-Type: application/json" \
  -d '{
    "studentNo": "2023100011",
    "password": "123456",
    "name": "李明",
    "gender": "MALE",
    "birthDate": "2004-03-15",
    "major": "软件工程",
    "className": "2023级2班",
    "grade": 2023,
    "enrollmentYear": 2023,
    "email": "2023100011@stu.cqu.edu.cn"
  }'
```

### 教师注册测试

```bash
curl -X POST http://localhost:8080/api/auth/register/teacher \
  -H "Content-Type: application/json" \
  -d '{
    "teacherNo": "T1007",
    "password": "123456",
    "name": "王教授",
    "gender": "MALE",
    "title": "教授",
    "department": "软件学院",
    "email": "T1007@cqu.edu.cn",
    "phone": "13912345678"
  }'
```

### 登录测试

```bash
# 学生登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "2023100011",
    "password": "123456"
  }'

# 教师登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "T1007",
    "password": "123456"
  }'
```

## 完整测试流程

### 1. 初始化数据库

```bash
# 建表
mysql -u root -p < src/main/resources/schema.sql

# 插入初始数据
mysql -u root -p < src/main/resources/data.sql
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

### 3. 测试现有账号登录

```bash
# 学生登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2023100001","password":"123456"}'
```

### 4. 测试注册新学生

```bash
curl -X POST http://localhost:8080/api/auth/register/student \
  -H "Content-Type: application/json" \
  -d '{
    "studentNo": "2023100020",
    "password": "mypassword",
    "name": "测试学生",
    "gender": "FEMALE",
    "major": "人工智能",
    "className": "2023级3班"
  }'
```

### 5. 使用新账号登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2023100020","password":"mypassword"}'
```

## 注意事项

### 学号/教工号格式

- **学号**: 10位数字 (如: 2023100001)
- **教工号**: T开头+4位数字 (如: T1001)

### 唯一性检查

- 学号/教工号不能重复
- 用户名不能重复
- 注册时会自动检查,重复会返回错误

### 密码安全

- 密码会使用BCrypt自动加密存储
- 建议密码长度至少6位

### 自动登录

- 注册成功后会自动登录
- 返回的token可以直接使用访问其他接口

### 默认值

学生注册时,如果不提供以下字段,会使用默认值:
- `grade`: 2024
- `enrollmentYear`: 2024

## 错误处理

### 常见错误

1. **学号/教工号已存在**
   ```json
   {
     "code": 500,
     "message": "注册失败: 学号已存在"
   }
   ```

2. **格式错误**
   ```json
   {
     "code": 400,
     "message": "参数校验失败: {studentNo=学号格式错误,应为10位数字}"
   }
   ```

3. **必填字段缺失**
   ```json
   {
     "code": 400,
     "message": "参数校验失败: {name=姓名不能为空}"
   }
   ```

## 数据库查询

### 查看所有用户

```sql
SELECT u.username, u.role, u.real_name, u.status
FROM user u
ORDER BY u.create_time DESC;
```

### 查看学生列表

```sql
SELECT s.student_no, s.name, s.gender, s.major, s.class_name
FROM student s
ORDER BY s.student_no;
```

### 查看教师列表

```sql
SELECT t.teacher_no, t.name, t.title, t.department
FROM teacher t
ORDER BY t.teacher_no;
```

## 与前端集成

前端可以创建注册表单,调用相应的接口:

```javascript
// 学生注册
const registerStudent = async (formData) => {
  const response = await fetch('http://localhost:8080/api/auth/register/student', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(formData),
  });
  const result = await response.json();
  if (result.code === 200) {
    // 注册成功,保存token
    localStorage.setItem('token', result.data.token);
    // 跳转到主页
  }
};

// 教师注册
const registerTeacher = async (formData) => {
  const response = await fetch('http://localhost:8080/api/auth/register/teacher', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(formData),
  });
  const result = await response.json();
  if (result.code === 200) {
    localStorage.setItem('token', result.data.token);
  }
};
```

---

**提示**: 生产环境中建议增加邮箱验证、手机验证等额外的安全措施。
