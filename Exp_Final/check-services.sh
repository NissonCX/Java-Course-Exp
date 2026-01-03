#!/bin/bash

# 检查所有微服务状态

echo "========================================="
echo "微服务状态检查"
echo "========================================="
echo ""

# 检查MySQL
echo "1. 检查 MySQL..."
if ps aux | grep -v grep | grep -q mysqld; then
    echo "   ✅ MySQL 运行中"
else
    echo "   ❌ MySQL 未运行"
fi
echo ""

# 检查Nacos
echo "2. 检查 Nacos..."
if curl -s http://localhost:8848/nacos/actuator/health > /dev/null 2>&1; then
    echo "   ✅ Nacos 运行中"
    NACOS_SERVICES=$(curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20" | python3 -c "import sys,json;data=json.load(sys.stdin);print(data['count'])" 2>/dev/null)
    echo "   已注册服务数: $NACOS_SERVICES"
else
    echo "   ❌ Nacos 未运行"
fi
echo ""

# 检查Gateway
echo "3. 检查 Gateway (8080)..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   ✅ Gateway 运行中"
else
    echo "   ❌ Gateway 未响应"
fi
echo ""

# 列出所有注册的服务
echo "4. Nacos 已注册的服务:"
curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20" 2>/dev/null | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    for service in sorted(data.get('doms', [])):
        print(f'   ✅ {service}')
except:
    print('   ❌ 无法获取服务列表')
" 2>/dev/null
echo ""

# 检查运行的进程
PROCESS_COUNT=$(ps aux | grep "spring-boot:run" | grep -v grep | wc -l | tr -d ' ')
echo "5. 运行中的微服务进程数: $PROCESS_COUNT"
echo ""

# 测试登录API
echo "6. 测试登录 API..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}' 2>/dev/null)

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "   ✅ 登录API正常"
else
    echo "   ❌ 登录API异常"
fi
echo ""

echo "========================================="
echo "状态检查完成"
echo "========================================="
