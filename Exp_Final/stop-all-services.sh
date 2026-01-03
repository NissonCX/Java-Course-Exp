#!/bin/bash

# 学生成绩管理系统微服务停止脚本
# Student Grade Management System - Microservices Stop Script

echo "========================================"
echo "停止所有微服务..."
echo "========================================"
echo ""

# 停止所有 spring-boot:run 进程
echo "正在查找并停止所有微服务进程..."

# 查找所有 mvn spring-boot:run 进程
PIDS=$(ps aux | grep "mvn spring-boot:run" | grep -v grep | awk '{print $2}')

if [ -z "$PIDS" ]; then
    echo "没有找到运行中的微服务进程"
else
    echo "找到以下进程:"
    ps aux | grep "mvn spring-boot:run" | grep -v grep | awk '{print "  PID: " $2 " - " $11 " " $12 " " $13}'
    echo ""
    echo "正在停止进程..."

    for PID in $PIDS; do
        echo "  停止 PID: $PID"
        kill $PID 2>/dev/null
    done

    # 等待进程终止
    sleep 3

    # 检查是否还有残留进程，强制杀死
    REMAINING=$(ps aux | grep "mvn spring-boot:run" | grep -v grep | awk '{print $2}')
    if [ ! -z "$REMAINING" ]; then
        echo ""
        echo "强制停止残留进程..."
        for PID in $REMAINING; do
            echo "  强制停止 PID: $PID"
            kill -9 $PID 2>/dev/null
        done
    fi
fi

echo ""
echo "========================================"
echo "所有微服务已停止"
echo "========================================"
