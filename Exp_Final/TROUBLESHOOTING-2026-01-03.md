# 微服务启动问题修复总结

## 问题概述

用户报告前端无法访问，同时Nacos服务注册列表为空。

## 根本原因分析

系统存在三个主要问题导致微服务无法正常启动：

### 1. PasswordEncoder Bean缺失 (auth-service)

**症状：**
```
Error creating bean with name 'authServiceImpl':
Unsatisfied dependency expressed through field 'passwordEncoder':
No qualifying bean of type 'PasswordEncoder' available
```

**原因：**
- auth-service需要`PasswordEncoder`用于密码加密验证
- 但auth-service不应该使用security-common中的完整`SecurityConfig`（包含JWT过滤器）
- auth-service负责签发JWT，不需要验证JWT

**解决方案：**
1. 为auth-service创建了自定义的`SecurityConfig`类，只提供`PasswordEncoder` Bean
2. 修改`AuthApplication.java`，使用`@ComponentScan`的`excludeFilters`排除security-common中的`SecurityConfig`

**修改的文件：**
- `services/auth-service/src/main/java/com/cqu/auth/config/SecurityConfig.java` (新建)
- `services/auth-service/src/main/java/com/cqu/auth/AuthApplication.java` (添加excludeFilters)

### 2. 重复的SecurityConfig导致Bean冲突 (core-service)

**症状：**
```
ConflictingBeanDefinitionException:
Annotation-specified bean name 'securityConfig' for bean class
[com.cqu.security.SecurityConfig] conflicts with existing bean
[com.cqu.core.config.SecurityConfig]
```

**原因：**
- core-service有自己的`SecurityConfig`（配置静态资源路由）
- 同时`@ComponentScan`扫描了`com.cqu.security`包，加载了security-common的`SecurityConfig`
- Spring容器中存在两个同名的SecurityConfig Bean

**解决方案：**
修改`CoreApplication.java`，排除security-common中的`SecurityConfig`类

**修改的文件：**
- `services/core-service/src/main/java/com/cqu/core/CoreApplication.java`

### 3. 重复的JWT类导致Bean冲突 (core-service)

**症状：**
```
ConflictingBeanDefinitionException:
bean name 'jwtAuthenticationFilter' conflicts with existing bean
```

**原因：**
- git restore操作恢复了core-service自己的`JwtAuthenticationFilter`和`JwtUtil`类
- 这些类与security-common中的同名类冲突

**解决方案：**
删除core-service中的重复security目录：
```bash
rm -rf services/core-service/src/main/java/com/cqu/core/security/
```

**删除的文件：**
- `services/core-service/src/main/java/com/cqu/core/security/JwtAuthenticationFilter.java`
- `services/core-service/src/main/java/com/cqu/core/security/JwtUtil.java`

### 4. 启动脚本路径问题

**症状：**
脚本中的`cd services/xxx`命令失败，导致除auth-service外的其他服务未启动

**原因：**
后台运行时相对路径解析失败

**解决方案：**
修改`start-all-services.sh`，使用绝对路径：
```bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/services/auth-service" && nohup mvn spring-boot:run > "$SCRIPT_DIR/logs/auth-service.log" 2>&1 &
```

**修改的文件：**
- `start-all-services.sh`

## 最终结果

### 修复前：
- ✗ 前端无法访问（503错误）
- ✗ Nacos只注册了auth-service或没有服务注册
- ✗ 多个微服务启动失败

### 修复后：
✅ **所有9个微服务成功启动并注册到Nacos:**
- gateway
- auth-service
- core-service
- student-service
- teacher-service
- course-service
- score-service
- admin-service
- ai-service

✅ **前端可以正常访问:** http://localhost:8080/index.html

✅ **登录API正常工作:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}'

# 返回：
{"code":200,"message":"success","data":{"token":"eyJ...","username":"2021001",...}}
```

## 新增工具

### 1. check-services.sh - 系统状态检查脚本

一键检查所有微服务状态：
```bash
./check-services.sh
```

功能：
- ✅ 检查MySQL运行状态
- ✅ 检查Nacos运行状态和注册服务数
- ✅ 检查Gateway健康状态
- ✅ 列出所有在Nacos中注册的服务
- ✅ 统计运行中的微服务进程数
- ✅ 测试登录API功能

### 2. 更新的启动/停止脚本

**start-all-services.sh:**
- 使用绝对路径，避免后台运行时路径问题
- 自动检查MySQL和Nacos前置条件
- 按正确顺序启动所有服务
- 输出日志到logs/目录

**stop-all-services.sh:**
- 优雅停止所有mvn spring-boot:run进程
- 如有残留强制杀死

## 文档更新

更新了`CLAUDE.md`文档，添加：

1. **新的Common Issues部分:**
   - PasswordEncoder Bean Not Found
   - Conflicting Bean Definitions (SecurityConfig/JwtAuthenticationFilter)
   - 详细的症状、原因和解决方案说明

2. **增强的Verification部分:**
   - 添加了check-services.sh的使用说明
   - 完整的验证步骤

## 架构设计原则总结

通过这次修复，明确了微服务中shared library的使用原则：

### security-common的使用策略：

| 服务 | 使用JwtUtil | 使用JwtAuthenticationFilter | 使用SecurityConfig | 说明 |
|------|------------|----------------------------|-------------------|------|
| auth-service | ✅ | ❌ | ❌ (自定义) | 只需要签发JWT和加密密码 |
| core-service | ✅ | ✅ | ❌ (自定义) | 需要JWT验证，但有特殊静态资源路由 |
| 其他服务 | ✅ | ✅ | ✅ | 标准JWT验证流程 |

### ComponentScan配置原则：

```java
// auth-service: 排除完整的SecurityConfig
@ComponentScan(
    basePackages = {"com.cqu.auth", "com.cqu.security", "com.cqu.common"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)

// core-service: 同样排除SecurityConfig
@ComponentScan(
    basePackages = {"com.cqu.core", "com.cqu.security", "com.cqu.common"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)

// 其他服务: 直接扫描，无需排除
@ComponentScan(basePackages = {"com.cqu.xxx", "com.cqu.security", "com.cqu.common"})
```

## 验证命令

```bash
# 1. 停止所有服务
./stop-all-services.sh

# 2. 重新构建
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
mvn clean install -DskipTests

# 3. 启动所有服务
./start-all-services.sh

# 4. 等待30-40秒后检查状态
./check-services.sh

# 5. 访问前端
open http://localhost:8080/index.html
```

## 遗留问题

无。所有微服务均正常运行。

---

**修复日期:** 2026-01-03
**修复人员:** Claude Code
**测试环境:** macOS, JDK 21, MySQL 8.x, Nacos (Docker)
