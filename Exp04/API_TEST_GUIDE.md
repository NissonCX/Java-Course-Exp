# API测试指南

本文件提供完整的API测试用例,使用curl命令进行测试。

## 环境变量设置

```bash
# 设置基础URL
export BASE_URL=http://localhost:8080
export TOKEN=""  # 登录后设置
```

## 1. 数据初始化

### 1.1 初始化所有测试数据

```bash
curl -X POST "${BASE_URL}/api/init/all" \
  -H "Content-Type: application/json" | jq
```

预期响应:
```json
{
  "code": 200,
  "message": "数据初始化成功",
  "data": {
    "teachersCount": 6,
    "coursesCount": 5,
    "teachingClassesCount": 12,
    "studentsCount": 120,
    "enrollmentsCount": 480
  }
}
```

## 2. 认证接口

### 2.1 学生登录

```bash
curl -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "2023100001",
    "password": "123456"
  }' | jq

# 保存token到环境变量
export STUDENT_TOKEN="从响应中复制token"
```

### 2.2 教师登录

```bash
curl -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "T1001",
    "password": "123456"
  }' | jq

# 保存token
export TEACHER_TOKEN="从响应中复制token"
```

## 3. 学生接口测试

### 3.1 查询我的所有成绩

```bash
curl -X GET "${BASE_URL}/api/student/scores" \
  -H "Authorization: Bearer ${STUDENT_TOKEN}" | jq
```

### 3.2 AI学习建议咨询

```bash
curl -X POST "${BASE_URL}/api/student/ai/consult" \
  -H "Authorization: Bearer ${STUDENT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "我的数据库成绩不太理想,该如何提高?"
  }' | jq
```

另一个例子:
```bash
curl -X POST "${BASE_URL}/api/student/ai/consult" \
  -H "Authorization: Bearer ${STUDENT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "根据我的成绩情况,未来适合从事哪方面的工作?"
  }' | jq
```

## 4. 教师接口测试

### 4.1 查询我的教学班列表

```bash
curl -X GET "${BASE_URL}/api/teacher/classes" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" | jq

# 从响应中找到一个教学班ID,保存到环境变量
export CLASS_ID=1
```

### 4.2 查询教学班学生成绩列表

```bash
curl -X GET "${BASE_URL}/api/teacher/class/${CLASS_ID}/scores" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" | jq
```

### 4.3 录入学生成绩

```bash
# 从上一步响应中找到一个学生ID
export STUDENT_ID=10

# 录入成绩
curl -X POST "${BASE_URL}/api/teacher/score/input" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"teachingClassId\": ${CLASS_ID},
    \"studentId\": ${STUDENT_ID},
    \"usualScore\": 85,
    \"midtermScore\": 88,
    \"experimentScore\": 90,
    \"finalScore\": 87
  }" | jq
```

### 4.4 查询教学班成绩统计

```bash
curl -X GET "${BASE_URL}/api/teacher/class/${CLASS_ID}/statistics" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" | jq
```

预期响应示例:
```json
{
  "code": 200,
  "data": {
    "classNo": "TC0001",
    "courseName": "Java程序设计",
    "semester": "2024-2025-1",
    "totalStudents": 45,
    "scoredStudents": 45,
    "averageScore": 78.50,
    "highestScore": 95.00,
    "lowestScore": 60.00,
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

### 4.5 AI教学分析咨询

```bash
curl -X POST "${BASE_URL}/api/teacher/ai/consult" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"teachingClassId\": ${CLASS_ID},
    \"message\": \"请分析这个班的整体学习情况,给出教学改进建议\"
  }" | jq
```

另一个例子:
```bash
curl -X POST "${BASE_URL}/api/teacher/ai/consult" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"teachingClassId\": ${CLASS_ID},
    \"message\": \"如何提高班级的及格率?\"
  }" | jq
```

## 5. 权限测试

### 5.1 测试学生访问教师接口(应该失败)

```bash
curl -X GET "${BASE_URL}/api/teacher/classes" \
  -H "Authorization: Bearer ${STUDENT_TOKEN}" | jq
```

预期响应:
```json
{
  "code": 403,
  "message": "没有权限访问该资源"
}
```

### 5.2 测试未认证访问(应该失败)

```bash
curl -X GET "${BASE_URL}/api/student/scores" | jq
```

预期响应:
```json
{
  "code": 401,
  "message": "Unauthorized"
}
```

## 6. 完整测试流程脚本

将以下内容保存为 `test_api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== 1. 初始化数据 ==="
curl -s -X POST "${BASE_URL}/api/init/all" | jq .

echo -e "\n=== 2. 学生登录 ==="
STUDENT_LOGIN=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "2023100001", "password": "123456"}')
echo "$STUDENT_LOGIN" | jq .
STUDENT_TOKEN=$(echo "$STUDENT_LOGIN" | jq -r '.data.token')

echo -e "\n=== 3. 教师登录 ==="
TEACHER_LOGIN=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "T1001", "password": "123456"}')
echo "$TEACHER_LOGIN" | jq .
TEACHER_TOKEN=$(echo "$TEACHER_LOGIN" | jq -r '.data.token')

echo -e "\n=== 4. 学生查询成绩 ==="
curl -s -X GET "${BASE_URL}/api/student/scores" \
  -H "Authorization: Bearer ${STUDENT_TOKEN}" | jq .

echo -e "\n=== 5. 教师查询教学班 ==="
CLASSES=$(curl -s -X GET "${BASE_URL}/api/teacher/classes" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}")
echo "$CLASSES" | jq .
CLASS_ID=$(echo "$CLASSES" | jq -r '.data[0].id')

echo -e "\n=== 6. 教师查询教学班成绩 ==="
SCORES=$(curl -s -X GET "${BASE_URL}/api/teacher/class/${CLASS_ID}/scores" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}")
echo "$SCORES" | jq .
STUDENT_ID=$(echo "$SCORES" | jq -r '.data[0].studentId')

echo -e "\n=== 7. 教师录入成绩 ==="
curl -s -X POST "${BASE_URL}/api/teacher/score/input" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"teachingClassId\": ${CLASS_ID},
    \"studentId\": ${STUDENT_ID},
    \"usualScore\": 85,
    \"midtermScore\": 88,
    \"experimentScore\": 90,
    \"finalScore\": 87
  }" | jq .

echo -e "\n=== 8. 教师查询成绩统计 ==="
curl -s -X GET "${BASE_URL}/api/teacher/class/${CLASS_ID}/statistics" \
  -H "Authorization: Bearer ${TEACHER_TOKEN}" | jq .

echo -e "\n=== 测试完成 ==="
```

运行测试:
```bash
chmod +x test_api.sh
./test_api.sh
```

## 7. Postman导入

如果使用Postman测试,可以创建以下环境变量:
- `base_url`: http://localhost:8080
- `student_token`: 学生登录后的token
- `teacher_token`: 教师登录后的token
- `class_id`: 教学班ID
- `student_id`: 学生ID

## 注意事项

1. **首次测试**: 必须先调用初始化接口创建测试数据
2. **Token过期**: 默认24小时过期,过期后需重新登录
3. **AI功能**: 需要配置OpenAI API Key才能使用
4. **跨域问题**: 如果前端遇到跨域问题,检查Controller的@CrossOrigin注解
5. **数据库**: 确保MySQL和Redis都在运行

## 常用测试账号

### 学生账号
- 学号: 2023100001 ~ 2023100120
- 密码: 123456

### 教师账号
- 工号: T1001 ~ T1006
- 密码: 123456
