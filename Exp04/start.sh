#!/bin/bash

# 学生成绩管理系统后端 - 快速启动脚本

echo "========================================="
echo "  学生成绩管理系统后端 - 快速启动"
echo "========================================="
echo ""

# 检查Java版本
echo "[1/6] 检查Java版本..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge "17" ]; then
        echo "✓ Java版本: $(java -version 2>&1 | head -n 1)"
    else
        echo "✗ Java版本过低,需要Java 17或以上"
        exit 1
    fi
else
    echo "✗ 未检测到Java,请先安装JDK 21"
    exit 1
fi

# 检查Maven
echo ""
echo "[2/6] 检查Maven..."
if command -v mvn &> /dev/null; then
    echo "✓ Maven版本: $(mvn -version | head -n 1)"
else
    echo "✗ 未检测到Maven,请先安装Maven 3.6+"
    exit 1
fi

# 检查MySQL
echo ""
echo "[3/6] 检查MySQL连接..."
if command -v mysql &> /dev/null; then
    echo "✓ MySQL已安装"
    echo "  请确保MySQL服务正在运行,数据库'stu_grade_sys'已创建"
else
    echo "⚠ 未检测到MySQL,请确保MySQL 8.0+已安装并运行"
fi

# 检查Redis
echo ""
echo "[4/6] 检查Redis连接..."
if command -v redis-cli &> /dev/null; then
    if redis-cli ping > /dev/null 2>&1; then
        echo "✓ Redis服务正在运行"
    else
        echo "⚠ Redis未运行,请启动Redis服务"
        echo "  命令: redis-server"
    fi
else
    echo "⚠ 未检测到Redis,请确保Redis 6.0+已安装并运行"
fi

# 编译项目
echo ""
echo "[5/6] 编译项目..."
mvn clean compile -DskipTests
if [ $? -eq 0 ]; then
    echo "✓ 编译成功"
else
    echo "✗ 编译失败,请检查错误信息"
    exit 1
fi

# 启动应用
echo ""
echo "[6/6] 启动应用..."
echo ""
echo "========================================="
echo "  应用启动中,请稍候..."
echo "  访问地址: http://localhost:8080"
echo "  API文档: 见 API_TEST_GUIDE.md"
echo "========================================="
echo ""
echo "提示:"
echo "1. 首次启动后,调用 POST /api/init/all 初始化测试数据"
echo "2. 默认账号密码都是: 123456"
echo "3. 学生账号: 2023100001 ~ 2023100120"
echo "4. 教师账号: T1001 ~ T1006"
echo "5. AI功能使用阿里云通义千问,需配置DASHSCOPE_API_KEY"
echo "   详见: DASHSCOPE_CONFIG.md"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

mvn spring-boot:run
