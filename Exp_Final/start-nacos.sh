#!/bin/bash

# 启动 Nacos 服务器
echo "🚀 启动 Nacos 服务器..."

# 检查 Nacos 是否已经在运行
if curl -s http://localhost:8848/nacos/v1/console/health/readiness | grep -q "OK"; then
    echo "✅ Nacos 已经在运行"
    exit 0
fi

# 启动 Nacos
cd ~/nacos/bin
sh startup.sh -m standalone

# 等待 Nacos 启动
echo "⏳ 等待 Nacos 启动..."
for i in {1..30}; do
    if curl -s http://localhost:8848/nacos/v1/console/health/readiness 2>/dev/null | grep -q "OK"; then
        echo "✅ Nacos 已成功启动!"
        echo "🌐 控制台访问: http://localhost:8848/nacos"
        echo "👤 用户名/密码: nacos/nacos"
        exit 0
    fi
    echo "尝试 $i/30..."
    sleep 2
done

echo "❌ Nacos 启动超时,请检查日志: ~/nacos/logs/start.out"
exit 1
