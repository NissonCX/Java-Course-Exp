# IntelliJ IDEA 启动微服务指南

## 问题诊断

你遇到的问题：
1. **端口被占用** - 服务已经在后台运行
2. **ClassNotFoundException** - IntelliJ IDEA 没有正确识别依赖

## 解决方案

### 步骤1: 清理所有运行中的服务

```bash
# 在终端执行（已为你执行）
lsof -ti:8080,8081,8082,8083,8084,8085,8086,8087,8088 | xargs kill -9
```

### 步骤2: 在IntelliJ IDEA中刷新Maven项目

1. 打开IntelliJ IDEA
2. 在右侧找到 **Maven** 工具窗口
3. 点击刷新按钮 (Reload All Maven Projects) 🔄
4. 等待Maven重新下载和导入所有依赖

### 步骤3: 重新构建项目

选择以下任一方式:

**方式A: 使用Maven命令**
```bash
cd /Users/nissoncx/code/Java-Course-Exp-main/Exp_Final
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
mvn clean install -DskipTests
```

**方式B: 在IntelliJ中**
1. 菜单: `Build` -> `Rebuild Project`
2. 等待构建完成

### 步骤4: 按正确顺序启动服务

⚠️ **重要**: 必须按以下顺序启动！

#### 4.1 确认前置条件
```bash
# 确认MySQL运行（检查进程）
ps aux | grep -v grep | grep mysqld

# 或者通过系统偏好设置查看MySQL状态
# macOS: 系统偏好设置 -> MySQL -> 查看状态

# 确认Nacos运行
curl http://localhost:8848/nacos/actuator/health
```

#### 4.2 启动顺序

**第一组 (先启动，等待完全启动)**:
1. ✅ **auth-service** (端口 8081)
2. ✅ **course-service** (端口 8085)
3. ✅ **score-service** (端口 8086)

**第二组 (等第一组启动完成后)**:
4. ✅ **student-service** (端口 8083)
5. ✅ **teacher-service** (端口 8084)
6. ✅ **admin-service** (端口 8087)
7. ✅ **core-service** (端口 8082)
8. ✅ **ai-service** (端口 8088) - 可选

**第三组 (最后启动)**:
9. ✅ **gateway** (端口 8080)

### 步骤5: 在IntelliJ IDEA中启动服务

#### 方法1: 使用运行配置

1. 找到每个服务的主类:
   - `com.cqu.auth.AuthApplication`
   - `com.cqu.course.CourseApplication`
   - `com.cqu.score.ScoreApplication`
   - `com.cqu.student.StudentApplication`
   - `com.cqu.teacher.TeacherApplication`
   - `com.cqu.admin.AdminApplication`
   - `com.cqu.core.CoreApplication`
   - `com.cqu.ai.AiApplication`
   - `com.cqu.gateway.GatewayApplication`

2. 右键点击主类 -> `Run 'XxxApplication'`

3. 等待服务完全启动（看到 "Started XxxApplication" 日志）再启动下一个

#### 方法2: 使用启动脚本（推荐）

```bash
cd /Users/nissoncx/code/Java-Course-Exp-main/Exp_Final
./start-all-services.sh
```

## 验证服务启动

### 检查Nacos注册

访问: http://localhost:8848/nacos (nacos/nacos)

应该看到9个服务全部注册成功:
- ✅ auth-service
- ✅ course-service
- ✅ score-service
- ✅ student-service
- ✅ teacher-service
- ✅ admin-service
- ✅ core-service
- ✅ ai-service
- ✅ gateway

### 测试API

```bash
# 测试登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}'
```

## 常见问题

### 问题1: "Port already in use"

**解决方案:**
```bash
# 找到占用端口的进程
lsof -ti:8083  # 替换成你的端口号

# 杀死进程
kill -9 <PID>

# 或者一次性清理所有
lsof -ti:8080,8081,8082,8083,8084,8085,8086,8087,8088 | xargs kill -9
```

### 问题2: "ClassNotFoundException: com.cqu.common.vo.Result"

**原因**: IntelliJ IDEA 没有正确加载 `libs/common` 模块

**解决方案:**
1. 关闭所有运行中的服务
2. 在Maven工具窗口点击刷新 🔄
3. 运行 `mvn clean install`
4. 在IntelliJ中: `File` -> `Invalidate Caches` -> 勾选"Clear file system cache" -> `Invalidate and Restart`

### 问题3: 服务启动慢或卡住

**可能原因:**
- 数据库连接失败
- Nacos未运行
- 端口被占用

**检查:**
```bash
# 检查MySQL
ps aux | grep -v grep | grep mysqld
# 如果没有输出，说明MySQL未运行
# macOS启动方法: 系统偏好设置 -> MySQL -> Start

# 检查Nacos
curl http://localhost:8848/nacos/actuator/health

# 检查端口
lsof -i:8083
```

## IntelliJ IDEA 配置建议

### 配置JDK

1. `File` -> `Project Structure` -> `Project`
2. 设置 SDK 为 **21 (Oracle)**
3. Language level: **21**

### 配置Maven

1. `File` -> `Settings` -> `Build, Execution, Deployment` -> `Build Tools` -> `Maven`
2. Maven home path: 使用系统默认
3. User settings file: 默认
4. JDK for importer: **21**

### 创建复合运行配置

1. `Run` -> `Edit Configurations`
2. 点击 `+` -> `Compound`
3. 命名为 "All Microservices"
4. 按顺序添加各个服务的运行配置
5. ⚠️ **不要勾选 "Run in parallel"** - 必须顺序启动！

## 推荐的开发工作流

### 启动开发环境
```bash
# 1. 启动MySQL (如果未启动)
# macOS: 系统偏好设置 -> MySQL -> Start MySQL Server
# 或者检查是否已运行:
ps aux | grep -v grep | grep mysqld

# 2. 启动Nacos
docker-compose -f docker-compose-nacos.yml up -d

# 3. 等待Nacos就绪（约10秒）
sleep 10

# 4. 启动所有微服务
./start-all-services.sh

# 5. 查看日志
tail -f logs/gateway.log
```

### 停止开发环境
```bash
# 停止所有微服务
./stop-all-services.sh

# 停止Nacos (可选)
docker-compose -f docker-compose-nacos.yml down

# 停止MySQL (可选)
# macOS: 系统偏好设置 -> MySQL -> Stop MySQL Server
```

## 调试技巧

### 查看服务日志

```bash
# 实时查看gateway日志
tail -f logs/gateway.log

# 查看特定服务
tail -f logs/student-service.log

# 搜索错误
grep -i error logs/*.log
```

### 在IntelliJ中调试

1. 选择要调试的服务主类
2. 右键 -> `Debug 'XxxApplication'`
3. 设置断点
4. 通过Postman或curl发送请求触发断点

### 使用Nacos控制台

访问: http://localhost:8848/nacos

功能:
- 查看服务注册状态
- 查看服务实例详情
- 临时下线/上线服务
- 查看服务健康状态

## 性能优化建议

### 加速启动

在每个服务的启动配置中添加JVM参数:
```
-XX:TieredStopAtLevel=1
-Dspring.devtools.restart.enabled=false
```

### 减少内存使用

```
-Xms256m -Xmx512m
```

### 跳过不需要的服务

如果不需要AI功能，可以不启动 `ai-service`。

---

**最后更新**: 2026-01-03
**适用版本**: IntelliJ IDEA 2023.x+, JDK 21
