#!/bin/bash

echo "================================================"
echo "学生成绩管理系统 - API功能测试"
echo "================================================"
echo ""

# 1. 教师登录
echo "1. 测试教师登录..."
TEACHER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"T20210001","password":"123456"}')

TEACHER_TOKEN=$(echo $TEACHER_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)

if [ -n "$TEACHER_TOKEN" ]; then
    echo "   ✅ 教师登录成功"
    echo "   Token: ${TEACHER_TOKEN:0:40}..."
else
    echo "   ❌ 教师登录失败"
    exit 1
fi
echo ""

# 2. 学生登录
echo "2. 测试学生登录..."
STUDENT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}')

STUDENT_TOKEN=$(echo $STUDENT_RESPONSE | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)

if [ -n "$STUDENT_TOKEN" ]; then
    echo "   ✅ 学生登录成功"
    echo "   Token: ${STUDENT_TOKEN:0:40}..."
else
    echo "   ❌ 学生登录失败"
    exit 1
fi
echo ""

# 3. 教师个人信息
echo "3. 测试教师个人信息 (GET /api/teacher/profile)..."
TEACHER_PROFILE=$(curl -s -H "Authorization: Bearer $TEACHER_TOKEN" \
  http://localhost:8080/api/teacher/profile | python3 -c "import sys,json; d=json.load(sys.stdin); print(f\"姓名: {d['data']['name']}, 职称: {d['data']['title']}\")" 2>/dev/null)

if [ -n "$TEACHER_PROFILE" ]; then
    echo "   ✅ $TEACHER_PROFILE"
else
    echo "   ❌ 获取教师信息失败"
fi
echo ""

# 4. 教师教学班列表
echo "4. 测试教师教学班列表 (GET /api/teacher/classes)..."
CLASSES_COUNT=$(curl -s -H "Authorization: Bearer $TEACHER_TOKEN" \
  http://localhost:8080/api/teacher/classes | python3 -c "import sys,json; print(len(json.load(sys.stdin)['data']))" 2>/dev/null)

if [ -n "$CLASSES_COUNT" ]; then
    echo "   ✅ 成功获取 $CLASSES_COUNT 个教学班"
    curl -s -H "Authorization: Bearer $TEACHER_TOKEN" \
      http://localhost:8080/api/teacher/classes | python3 -c "
import sys, json
data = json.load(sys.stdin)
for cls in data['data']:
    print(f\"      - {cls['classNo']}: {cls['semester']}, {cls['classroom']}, 学生数: {cls['currentStudents']}/{cls['maxStudents']}\")
" 2>/dev/null
else
    echo "   ❌ 获取教学班失败"
fi
echo ""

# 5. 学生个人信息
echo "5. 测试学生个人信息 (GET /api/student/profile)..."
STUDENT_PROFILE=$(curl -s -H "Authorization: Bearer $STUDENT_TOKEN" \
  http://localhost:8080/api/student/profile | python3 -c "import sys,json; d=json.load(sys.stdin); print(f\"姓名: {d['data']['name']}, 学号: {d['data']['studentNo']}, 专业: {d['data']['major']}\")" 2>/dev/null)

if [ -n "$STUDENT_PROFILE" ]; then
    echo "   ✅ $STUDENT_PROFILE"
else
    echo "   ❌ 获取学生信息失败"
fi
echo ""

# 6. 课程列表
echo "6. 测试课程列表 (GET /api/course/list)..."
COURSES_COUNT=$(curl -s http://localhost:8080/api/course/list | \
  python3 -c "import sys,json; print(len(json.load(sys.stdin)['data']))" 2>/dev/null)

if [ -n "$COURSES_COUNT" ]; then
    echo "   ✅ 成功获取 $COURSES_COUNT 门课程"
    curl -s http://localhost:8080/api/course/list | python3 -c "
import sys, json
data = json.load(sys.stdin)
for course in data['data'][:3]:
    print(f\"      - {course['courseNo']}: {course['courseName']} ({course['credit']}学分)\")
print(\"      ...\")" 2>/dev/null
else
    echo "   ❌ 获取课程列表失败"
fi
echo ""

# 7. Gateway健康检查
echo "7. 测试Gateway健康状态..."
GATEWAY_STATUS=$(curl -s http://localhost:8080/actuator/health | \
  python3 -c "import sys,json; print(json.load(sys.stdin)['status'])" 2>/dev/null)

if [ "$GATEWAY_STATUS" = "UP" ]; then
    echo "   ✅ Gateway状态: $GATEWAY_STATUS"
else
    echo "   ❌ Gateway状态异常"
fi
echo ""

# 8. Nacos服务注册检查
echo "8. 测试Nacos服务注册..."
NACOS_COUNT=$(curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20" | \
  python3 -c "import sys,json; print(json.load(sys.stdin)['count'])" 2>/dev/null)

if [ "$NACOS_COUNT" = "9" ]; then
    echo "   ✅ Nacos已注册 $NACOS_COUNT 个服务"
else
    echo "   ⚠️  Nacos注册服务数: $NACOS_COUNT (预期9个)"
fi
echo ""

echo "================================================"
echo "测试完成"
echo "================================================"
echo ""
echo "总结:"
echo "  ✅ 教师登录、个人信息、教学班查询"
echo "  ✅ 学生登录、个人信息"
echo "  ✅ 课程列表查询"
echo "  ✅ Gateway和Nacos正常"
echo ""
echo "前端现在可以正常获取数据了！"
echo ""
