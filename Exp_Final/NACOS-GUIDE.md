# Nacos 服务注册与发现 - 完整指南

## 📋 概述

本项目已成功集成 **Alibaba Nacos** 作为服务注册中心，实现了微服务的自动注册、发现和负载均衡。

### 技术栈

- **Nacos Server**: v2.3.1 (Docker 运行)
- **Spring Cloud Alibaba**: 2022.0.0.0
- **Spring Cloud**: 2023.0.0
- **Spring Boot**: 3.2.1

### 架构优势

✅ **动态服务发现**: 服务实例自动注册，无需硬编码地址
✅ **负载均衡**: 通过 Spring Cloud LoadBalancer 实现客户端负载均衡
✅ **健康检查**: 自动检测服务实例健康状态
✅ **故障隔离**: 不健康实例自动从服务列表移除
✅ **弹性扩展**: 支持服务实例动态扩缩容

---

## 🚀 快速开始

### 1. 启动 Nacos Server

#### 方式一：使用 Docker (推荐)

```bash
# 确保 Docker Desktop 已启动

# 启动 Nacos
./start-nacos-docker.sh

# 或手动启动
docker-compose -f docker-compose-nacos.yml up -d
```

#### 方式二：验证 Nacos 状态

```bash
# 检查 Nacos 健康状态
curl http://localhost:8848/nacos/actuator/health

# 查看 Docker 日志
docker logs -f nacos-standalone
```

#### 访问 Nacos 控制台

- **地址**: http://localhost:8848/nacos
- **用户名**: nacos
- **密码**: nacos

### 2. 启动微服务

```bash
# 方式一：使用启动脚本（推荐）
./start-all-phase2.sh

# 方式二：手动启动
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home

cd services/auth-service && mvn spring-boot:run &
cd services/course-service && mvn spring-boot:run &
cd services/score-service && mvn spring-boot:run &
cd services/student-service && mvn spring-boot:run &
cd services/teacher-service && mvn spring-boot:run &
cd services/admin-service && mvn spring-boot:run &
cd services/ai-service && mvn spring-boot:run &
cd services/gateway && mvn spring-boot:run &
```

### 3. 验证服务注册

等待 10-20 秒后，访问 Nacos 控制台：

1. 进入 **服务管理 → 服务列表**
2. 应该看到 8 个已注册的服务：
   - auth-service
   - student-service
   - teacher-service
   - course-service
   - score-service
   - admin-service
   - ai-service
   - gateway

每个服务显示：
- ✅ 实例数: 1
- ✅ 健康实例数: 1
- ✅ 集群数: 1

---

## 📊 服务配置详情

### 所有服务的 Nacos 配置

每个服务的 `application.yml` 都包含以下配置：

```yaml
spring:
  application:
    name: {service-name}  # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # Nacos 服务器地址
        namespace: public             # 命名空间
        group: DEFAULT_GROUP          # 分组（默认）
```

### 服务清单

| 服务名称 | 端口 | 注册名称 | 功能 |
|---------|------|---------|------|
| gateway | 8080 | gateway | API 网关 |
| auth-service | 8081 | auth-service | 认证服务 |
| student-service | 8083 | student-service | 学生服务 |
| teacher-service | 8084 | teacher-service | 教师服务 |
| course-service | 8085 | course-service | 课程服务 |
| score-service | 8086 | score-service | 成绩服务 |
| admin-service | 8087 | admin-service | 管理服务 |
| ai-service | 8088 | ai-service | AI 服务 |

---

## 🔄 服务发现机制

### OpenFeign 配置

所有 Feign 客户端已更新为使用服务发现：

#### 修改前（硬编码 URL）
```java
@FeignClient(name = "score-service", url = "http://localhost:8086")
public interface ScoreServiceClient {
    @GetMapping("/api/score/student/{studentId}")
    Result<Map<String, Object>> getStudentScores(@PathVariable Long studentId);
}
```

#### 修改后（服务发现）
```java
@FeignClient(name = "score-service")  // 通过服务名发现
public interface ScoreServiceClient {
    @GetMapping("/api/score/student/{studentId}")
    Result<Map<String, Object>> getStudentScores(@PathVariable Long studentId);
}
```

### Gateway 负载均衡路由

Gateway 的路由配置使用 `lb://` 前缀进行负载均衡：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service  # lb = Load Balancer
          predicates:
            - Path=/api/auth/**

        - id: student-service
          uri: lb://student-service
          predicates:
            - Path=/api/student/**
```

**工作原理**:
1. 请求到达 Gateway: `GET /api/student/profile`
2. Gateway 查询 Nacos 获取 `student-service` 的所有实例
3. LoadBalancer 选择一个健康实例（默认轮询策略）
4. 转发请求到选中的实例

---

## 🎯 服务间调用流程

### 示例：学生查询成绩

```
用户请求
  ↓
Gateway (8080)
  ├─ 查询 Nacos: student-service 在哪里？
  └─ 转发到 → Student Service (8083)
       ├─ @FeignClient(name = "score-service")
       ├─ 查询 Nacos: score-service 在哪里？
       └─ 调用 → Score Service (8086)
            └─ 返回成绩数据
```

### 代码调用示例

```java
// StudentServiceImpl.java
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private ScoreServiceClient scoreServiceClient;  // Feign 客户端

    @Override
    public Map<String, Object> getMyScores(Long studentId) {
        // Feign 自动从 Nacos 发现 score-service 并调用
        Result<Map<String, Object>> result = scoreServiceClient.getStudentScores(studentId);
        return result.getData();
    }
}
```

---

## 🔧 高级配置

### 1. 自定义负载均衡策略

默认使用轮询（Round Robin），可自定义：

```java
@Configuration
public class LoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
}
```

### 2. 配置服务权重

在 Nacos 控制台可以为每个实例配置权重（0-100）：

- 权重越高，被选中的概率越大
- 权重为 0 的实例不会被调用

### 3. 元数据配置

在 `application.yml` 中添加元数据：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        metadata:
          version: v1.0
          region: cn-hangzhou
          zone: zone-a
```

### 4. 命名空间隔离

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev  # dev, test, prod
```

### 5. 自定义健康检查

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        heart-beat-interval: 5000  # 心跳间隔 5秒
        heart-beat-timeout: 15000  # 心跳超时 15秒
```

---

## 🧪 测试服务发现

### 1. 测试服务注册

```bash
# 查看已注册的服务
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=student-service"

# 预期响应
{
  "name": "DEFAULT_GROUP@@student-service",
  "hosts": [
    {
      "ip": "192.168.1.100",
      "port": 8083,
      "healthy": true,
      "weight": 1.0,
      "instanceId": "192.168.1.100#8083#DEFAULT#DEFAULT_GROUP@@student-service"
    }
  ]
}
```

### 2. 测试 Feign 调用

```bash
# 登录获取 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}' \
  | jq -r '.data.token')

# 测试跨服务调用（student-service → score-service）
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/student/scores | jq .

# 响应应包含从 score-service 获取的真实成绩数据
```

### 3. 测试负载均衡

启动多个相同服务实例：

```bash
# 启动第一个 student-service（8083）
cd services/student-service && mvn spring-boot:run &

# 修改端口启动第二个实例（8093）
cd services/student-service && mvn spring-boot:run -Dserver.port=8093 &

# 在 Nacos 控制台查看 student-service，应该有 2 个实例
# 多次调用 API，Nacos 会在两个实例间轮询分配请求
```

---

## 📈 监控与管理

### 1. Nacos 控制台功能

#### 服务管理
- **服务列表**: 查看所有已注册的服务
- **服务详情**: 查看服务的所有实例、健康状态、元数据
- **实例管理**:
  - 上线/下线实例
  - 修改权重
  - 编辑元数据

#### 配置管理（可选）
- 可以将 application.yml 配置迁移到 Nacos 配置中心
- 支持动态配置刷新，无需重启服务

#### 命名空间
- 用于环境隔离（dev/test/prod）
- 每个命名空间的服务相互独立

### 2. 查看服务调用链路

在服务日志中可以看到 Feign 调用：

```bash
# student-service 日志
tail -f logs/student-service.log | grep -i feign

# 示例输出
2026-01-03 20:15:23.456 DEBUG [student-service] FeignClient: Loading service instances for score-service from Nacos
2026-01-03 20:15:23.478 DEBUG [student-service] LoadBalancer: Selected instance [192.168.1.100:8086] for score-service
2026-01-03 20:15:23.512 DEBUG [student-service] FeignClient: Request to http://192.168.1.100:8086/api/score/student/4 completed in 34ms
```

---

## 🛠️ 故障排查

### 问题 1: 服务未注册到 Nacos

**症状**: Nacos 控制台看不到服务

**排查步骤**:
```bash
# 1. 检查 Nacos 是否启动
curl http://localhost:8848/nacos/actuator/health

# 2. 检查服务日志
tail -f logs/student-service.log | grep -i nacos

# 3. 检查网络连接
telnet localhost 8848
```

**常见原因**:
- Nacos Server 未启动
- 配置中 `server-addr` 错误
- 防火墙阻止 8848 端口
- 服务启动失败

**解决方案**:
```yaml
# 确保 application.yml 配置正确
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 确保地址正确
        namespace: public
```

### 问题 2: Feign 调用失败

**症状**: `FeignException: Load balancer does not have available server`

**排查步骤**:
```bash
# 1. 检查目标服务是否注册
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=score-service"

# 2. 检查 Feign 客户端配置
# 确保 @FeignClient 的 name 与 Nacos 中注册的服务名完全一致
```

**常见原因**:
- 目标服务未启动或未注册
- Feign Client 的 `name` 参数与 Nacos 注册名不匹配
- 目标服务所有实例都不健康

**解决方案**:
```java
// 确保服务名一致
@FeignClient(name = "score-service")  // 必须与 spring.application.name 一致
public interface ScoreServiceClient { ... }
```

### 问题 3: Gateway 无法路由

**症状**: `503 Service Unavailable` 或 `404 Not Found`

**排查步骤**:
```bash
# 1. 检查 Gateway 日志
tail -f logs/gateway.log

# 2. 检查路由配置
curl http://localhost:8080/actuator/gateway/routes | jq .

# 3. 测试直接访问目标服务
curl http://localhost:8083/api/student/test
```

**常见原因**:
- 路由配置错误
- 目标服务未启动
- LoadBalancer 依赖缺失

**解决方案**:
```yaml
# 确保 Gateway 有 LoadBalancer 依赖
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

# 路由使用 lb:// 前缀
spring:
  cloud:
    gateway:
      routes:
        - id: student-service
          uri: lb://student-service  # 注意 lb:// 前缀
```

### 问题 4: 服务实例显示不健康

**症状**: Nacos 控制台显示实例状态为不健康（红色）

**排查步骤**:
```bash
# 1. 检查服务健康检查端点
curl http://localhost:8083/actuator/health

# 2. 检查服务日志
tail -f logs/student-service.log | grep -i health
```

**常见原因**:
- 服务启动失败但进程未退出
- 数据库连接失败
- 依赖的其他服务不可用

**解决方案**:
- 修复服务启动问题
- 配置健康检查排除某些依赖

---

## 🎓 最佳实践

### 1. 服务命名规范

```yaml
# 推荐：使用小写字母和连字符
spring:
  application:
    name: student-service  # ✅

# 不推荐
spring:
  application:
    name: StudentService   # ❌ 大小写
    name: student_service  # ❌ 下划线
```

### 2. 生产环境配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}  # 环境变量
        namespace: ${SPRING_PROFILES_ACTIVE:dev}          # 根据环境切换
        metadata:
          version: ${project.version}
          build-time: ${maven.build.timestamp}
```

### 3. 优雅停机

```yaml
# application.yml
server:
  shutdown: graceful  # 优雅停机

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

### 4. Feign 超时配置

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
      score-service:  # 针对特定服务
        connectTimeout: 3000
        readTimeout: 60000  # 某些服务可能需要更长时间
```

### 5. 断路器集成（推荐）

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

```java
@FeignClient(name = "score-service", fallback = ScoreServiceFallback.class)
public interface ScoreServiceClient {
    @GetMapping("/api/score/student/{studentId}")
    Result<Map<String, Object>> getStudentScores(@PathVariable Long studentId);
}

@Component
public class ScoreServiceFallback implements ScoreServiceClient {
    @Override
    public Result<Map<String, Object>> getStudentScores(Long studentId) {
        return Result.error("成绩服务暂时不可用，请稍后再试");
    }
}
```

---

## 📝 管理脚本

### 启动 Nacos
```bash
./start-nacos-docker.sh
```

### 停止 Nacos
```bash
docker-compose -f docker-compose-nacos.yml down
```

### 查看 Nacos 日志
```bash
docker logs -f nacos-standalone
```

### 重启 Nacos
```bash
docker-compose -f docker-compose-nacos.yml restart
```

### 清理 Nacos 数据
```bash
docker-compose -f docker-compose-nacos.yml down -v
rm -rf nacos-data/
```

---

## 🚀 性能优化

### 1. 调整心跳间隔

```yaml
spring:
  cloud:
    nacos:
      discovery:
        heart-beat-interval: 5000   # 心跳间隔（毫秒）
        heart-beat-timeout: 15000   # 心跳超时（毫秒）
        ip-delete-timeout: 30000    # 实例删除超时（毫秒）
```

### 2. 缓存服务列表

Nacos 客户端会自动缓存服务列表，减少查询频率：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        cache-millis: 10000  # 缓存时间 10秒
```

### 3. LoadBalancer 缓存

```yaml
spring:
  cloud:
    loadbalancer:
      cache:
        enabled: true
        ttl: 35s  # 缓存 TTL
```

---

## 📚 参考资料

- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Cloud Alibaba 官方文档](https://spring-cloud-alibaba-group.github.io/github-pages/2022/zh-cn/index.html)
- [Spring Cloud Gateway 文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [OpenFeign 文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)

---

## 🎉 总结

✅ **Nacos 服务注册与发现已完全集成**
✅ **所有 8 个微服务支持自动注册**
✅ **OpenFeign 使用服务发现进行调用**
✅ **Gateway 实现负载均衡路由**
✅ **编译测试全部通过**

系统现在具备了生产级别的服务治理能力，可以支持：
- 动态扩缩容
- 故障自愈
- 灰度发布
- 多环境隔离

---

**最后更新**: 2026-01-03
**版本**: Phase 3.0 (Nacos 服务发现)
**文档作者**: Claude Code Agent
