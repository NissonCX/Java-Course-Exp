#!/bin/bash

# 学生成绩管理系统微服务停止脚本 - Phase 2

echo "停止所有 Phase 2 微服务..."

if [ -f logs/pids-phase2.txt ]; then
    PIDS=$(cat logs/pids-phase2.txt)
    echo "读取进程 ID: $PIDS"

    for PID in $PIDS; do
        if ps -p $PID > /dev/null; then
            echo "停止进程 $PID..."
            kill $PID
        else
            echo "进程 $PID 未运行"
        fi
    done

    rm logs/pids-phase2.txt
    echo "✅ 所有服务已停止"
else
    echo "⚠️  未找到 logs/pids-phase2.txt，尝试按端口停止..."

    # 按端口号查找并停止进程
    for PORT in 8080 8081 8083 8084 8085 8086 8087 8088; do
        PID=$(lsof -ti:$PORT)
        if [ ! -z "$PID" ]; then
            echo "停止端口 $PORT 的进程 (PID: $PID)..."
            kill $PID
        fi
    done

    echo "✅ 已尝试停止所有服务"
fi
