#!/bin/bash

# 学生成绩管理系统微服务启动脚本 - Phase 2
# 使用 JDK 21 启动所有微服务

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "=========================================="
echo "学生成绩管理系统 - 微服务架构 Phase 2"
echo "=========================================="
echo ""

# 创建日志目录
mkdir -p logs

# 检查数据库是否运行
echo "[1/10] 检查 MySQL 数据库..."
MYSQL_BIN="/usr/local/mysql-8.4.7-macos15-arm64/bin/mysql"
if ! pgrep -x mysqld > /dev/null 2>&1; then
    echo "⚠️  MySQL 未运行，请手动启动 MySQL"
    echo "   提示: 使用系统偏好设置或命令行启动 MySQL"
else
    echo "✅ MySQL 已运行"
fi
echo ""

# 启动 core-service (静态资源服务)
echo "[2/10] 启动 core-service (端口 8082, 静态资源)..."
cd services/core-service
mvn spring-boot:run > ../../logs/core-service.log 2>&1 &
CORE_PID=$!
echo "✅ core-service 已启动 (PID: $CORE_PID)"
cd ../..
sleep 8
echo ""

# 启动 auth-service
echo "[3/10] 启动 auth-service (端口 8081)..."
cd services/auth-service
mvn spring-boot:run > ../../logs/auth-service.log 2>&1 &
AUTH_PID=$!
echo "✅ auth-service 已启动 (PID: $AUTH_PID)"
cd ../..
sleep 8
echo ""

# 启动 course-service
echo "[4/10] 启动 course-service (端口 8085)..."
cd services/course-service
mvn spring-boot:run > ../../logs/course-service.log 2>&1 &
COURSE_PID=$!
echo "✅ course-service 已启动 (PID: $COURSE_PID)"
cd ../..
sleep 8
echo ""

# 启动 score-service
echo "[5/10] 启动 score-service (端口 8086)..."
cd services/score-service
mvn spring-boot:run > ../../logs/score-service.log 2>&1 &
SCORE_PID=$!
echo "✅ score-service 已启动 (PID: $SCORE_PID)"
cd ../..
sleep 8
echo ""

# 启动 student-service
echo "[6/10] 启动 student-service (端口 8083)..."
cd services/student-service
mvn spring-boot:run > ../../logs/student-service.log 2>&1 &
STUDENT_PID=$!
echo "✅ student-service 已启动 (PID: $STUDENT_PID)"
cd ../..
sleep 8
echo ""

# 启动 teacher-service
echo "[7/10] 启动 teacher-service (端口 8084)..."
cd services/teacher-service
mvn spring-boot:run > ../../logs/teacher-service.log 2>&1 &
TEACHER_PID=$!
echo "✅ teacher-service 已启动 (PID: $TEACHER_PID)"
cd ../..
sleep 8
echo ""

# 启动 admin-service
echo "[8/10] 启动 admin-service (端口 8087)..."
cd services/admin-service
mvn spring-boot:run > ../../logs/admin-service.log 2>&1 &
ADMIN_PID=$!
echo "✅ admin-service 已启动 (PID: $ADMIN_PID)"
cd ../..
sleep 8
echo ""

# 启动 ai-service
echo "[9/10] 启动 ai-service (端口 8088)..."
cd services/ai-service
mvn spring-boot:run > ../../logs/ai-service.log 2>&1 &
AI_PID=$!
echo "✅ ai-service 已启动 (PID: $AI_PID)"
cd ../..
sleep 8
echo ""

# 启动 gateway
echo "[10/10] 启动 gateway (端口 8080)..."
cd services/gateway
mvn spring-boot:run > ../../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo "✅ gateway 已启动 (PID: $GATEWAY_PID)"
cd ../..
sleep 5
echo ""

echo "=========================================="
echo "所有服务启动完成！"
echo "=========================================="
echo ""
echo "服务列表："
echo "  - Core Service:       http://localhost:8082 (PID: $CORE_PID) - 静态资源"
echo "  - Auth Service:       http://localhost:8081 (PID: $AUTH_PID)"
echo "  - Student Service:    http://localhost:8083 (PID: $STUDENT_PID)"
echo "  - Teacher Service:    http://localhost:8084 (PID: $TEACHER_PID)"
echo "  - Course Service:     http://localhost:8085 (PID: $COURSE_PID)"
echo "  - Score Service:      http://localhost:8086 (PID: $SCORE_PID)"
echo "  - Admin Service:      http://localhost:8087 (PID: $ADMIN_PID)"
echo "  - AI Service:         http://localhost:8088 (PID: $AI_PID)"
echo "  - Gateway:            http://localhost:8080 (PID: $GATEWAY_PID)"
echo ""
echo "前端访问："
echo "  - 主页:               http://localhost:8080/index.html"
echo "  - 学生页面:           http://localhost:8080/student.html"
echo "  - 教师页面:           http://localhost:8080/teacher.html"
echo "  - 管理员页面:         http://localhost:8080/admin.html"
echo ""
echo "Nacos 控制台："
echo "  - 地址:               http://localhost:8848/nacos"
echo "  - 用户名/密码:        nacos/nacos"
echo ""
echo "API 路由："
echo "  - 认证:               /api/auth/**     → auth-service"
echo "  - 学生:               /api/student/**  → student-service"
echo "  - 教师:               /api/teacher/**  → teacher-service"
echo "  - 课程:               /api/course/**   → course-service"
echo "  - 成绩:               /api/score/**    → score-service"
echo "  - 管理:               /api/admin/**    → admin-service"
echo "  - AI:                 /api/ai/**       → ai-service"
echo "  - 静态资源:           /, /*.html        → core-service (via gateway)"
echo ""
echo "查看日志："
echo "  tail -f logs/core-service.log"
echo "  tail -f logs/auth-service.log"
echo "  tail -f logs/student-service.log"
echo "  tail -f logs/teacher-service.log"
echo "  tail -f logs/course-service.log"
echo "  tail -f logs/score-service.log"
echo "  tail -f logs/admin-service.log"
echo "  tail -f logs/ai-service.log"
echo "  tail -f logs/gateway.log"
echo ""
echo "停止所有服务："
echo "  ./stop-all-phase2.sh"
echo ""
echo "进程 ID 已保存到 logs/pids-phase2.txt"
echo "$CORE_PID $AUTH_PID $STUDENT_PID $TEACHER_PID $COURSE_PID $SCORE_PID $ADMIN_PID $AI_PID $GATEWAY_PID" > logs/pids-phase2.txt
