#!/bin/bash

# 学生成绩管理系统微服务启动脚本
# Student Grade Management System - Microservices Startup Script

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 设置 JAVA_HOME 为 JDK 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "========================================"
echo "学生成绩管理系统 - 微服务启动"
echo "Student Grade Management System"
echo "========================================"
echo ""
echo "工作目录: $SCRIPT_DIR"
echo ""
echo "使用 Java 版本:"
java -version
echo ""

# 检查 MySQL 是否运行
echo "检查 MySQL 状态..."
if ps aux | grep -v grep | grep -q mysqld; then
    echo "✅ MySQL 正在运行"
else
    echo "❌ 错误: MySQL 未运行! 请先启动 MySQL"
    echo "   提示: 在 macOS 系统偏好设置中启动 MySQL"
    exit 1
fi
echo ""

# 检查 Nacos 是否运行
echo "检查 Nacos 状态..."
if ! curl -s http://localhost:8848/nacos/actuator/health > /dev/null 2>&1; then
    echo "❌ 错误: Nacos 未运行! 请先启动 Nacos:"
    echo "   docker-compose -f docker-compose-nacos.yml up -d"
    exit 1
fi
echo "✅ Nacos 正在运行"
echo ""

# 创建 logs 目录
mkdir -p "$SCRIPT_DIR/logs"

echo "正在启动微服务..."
echo ""

# 启动认证服务 (Authentication Service)
echo "启动 auth-service (端口 8081)..."
cd "$SCRIPT_DIR/services/auth-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/auth-service.log" 2>&1 &
AUTH_PID=$!
echo "  PID: $AUTH_PID"
cd "$SCRIPT_DIR"

# 启动课程服务 (Course Service)
echo "启动 course-service (端口 8085)..."
cd "$SCRIPT_DIR/services/course-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/course-service.log" 2>&1 &
COURSE_PID=$!
echo "  PID: $COURSE_PID"
cd "$SCRIPT_DIR"

# 启动成绩服务 (Score Service)
echo "启动 score-service (端口 8086)..."
cd "$SCRIPT_DIR/services/score-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/score-service.log" 2>&1 &
SCORE_PID=$!
echo "  PID: $SCORE_PID"
cd "$SCRIPT_DIR"

# 启动学生服务 (Student Service)
echo "启动 student-service (端口 8083)..."
cd "$SCRIPT_DIR/services/student-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/student-service.log" 2>&1 &
STUDENT_PID=$!
echo "  PID: $STUDENT_PID"
cd "$SCRIPT_DIR"

# 启动教师服务 (Teacher Service)
echo "启动 teacher-service (端口 8084)..."
cd "$SCRIPT_DIR/services/teacher-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/teacher-service.log" 2>&1 &
TEACHER_PID=$!
echo "  PID: $TEACHER_PID"
cd "$SCRIPT_DIR"

# 启动管理服务 (Admin Service)
echo "启动 admin-service (端口 8087)..."
cd "$SCRIPT_DIR/services/admin-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/admin-service.log" 2>&1 &
ADMIN_PID=$!
echo "  PID: $ADMIN_PID"
cd "$SCRIPT_DIR"

# 启动核心服务 (Core Service)
echo "启动 core-service (端口 8082)..."
cd "$SCRIPT_DIR/services/core-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/core-service.log" 2>&1 &
CORE_PID=$!
echo "  PID: $CORE_PID"
cd "$SCRIPT_DIR"

# 启动 AI 服务 (AI Service - 可选)
echo "启动 ai-service (端口 8088) - 可选..."
cd "$SCRIPT_DIR/services/ai-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/ai-service.log" 2>&1 &
AI_PID=$!
echo "  PID: $AI_PID"
cd "$SCRIPT_DIR"

# 等待服务启动
echo ""
echo "等待所有服务启动 (约30秒)..."
sleep 30

# 启动网关 (Gateway)
echo "启动 gateway (端口 8080)..."
cd "$SCRIPT_DIR/services/gateway" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/gateway.log" 2>&1 &
GATEWAY_PID=$!
echo "  PID: $GATEWAY_PID"
cd "$SCRIPT_DIR"

echo ""
echo "========================================"
echo "所有微服务已启动!"
echo "========================================"
echo ""
echo "服务列表:"
echo "  - auth-service:    http://localhost:8081 (PID: $AUTH_PID)"
echo "  - core-service:    http://localhost:8082 (PID: $CORE_PID)"
echo "  - student-service: http://localhost:8083 (PID: $STUDENT_PID)"
echo "  - teacher-service: http://localhost:8084 (PID: $TEACHER_PID)"
echo "  - course-service:  http://localhost:8085 (PID: $COURSE_PID)"
echo "  - score-service:   http://localhost:8086 (PID: $SCORE_PID)"
echo "  - admin-service:   http://localhost:8087 (PID: $ADMIN_PID)"
echo "  - ai-service:      http://localhost:8088 (PID: $AI_PID)"
echo "  - gateway:         http://localhost:8080 (PID: $GATEWAY_PID)"
echo ""
echo "访问地址:"
echo "  - 前端: http://localhost:8080"
echo "  - Nacos控制台: http://localhost:8848/nacos (nacos/nacos)"
echo ""
echo "日志文件位置: logs/"
echo ""
echo "停止所有服务:"
echo "  ./stop-all-services.sh"
echo ""
echo "查看实时日志:"
echo "  tail -f logs/gateway.log"
echo ""
