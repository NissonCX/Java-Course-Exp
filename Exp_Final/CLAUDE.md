# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Student Grade Management System** (学生成绩管理系统) built as a microservices architecture using Spring Boot 3.x, Spring Cloud, Nacos service discovery, and vanilla JavaScript frontend. The system supports role-based access (students, teachers, administrators) with JWT authentication and AI-powered advisory features using Alibaba's Qwen through LangChain4j.

**Key Technologies:**
- Spring Boot **3.2.1** (JDK 21)
- Spring Cloud **2023.0.0**
- Spring Cloud Alibaba **2022.0.0.0** (Nacos service discovery)
- Spring Cloud Gateway (API Gateway)
- Spring Security + JWT
- MyBatis-Plus for persistence
- MySQL database (`stu_grade_sys`)
- OpenFeign for inter-service communication
- LangChain4j with DashScope (Alibaba Qwen)
- Vanilla JavaScript frontend

## Architecture

### Microservices Structure

This is a Maven multi-module project with the following structure:

```
Exp_Final/
├── pom.xml                      # Root parent POM
├── libs/                        # Shared libraries
│   ├── common/                  # DTOs, VOs, entities, global exception handler
│   └── security-common/         # JWT utilities, security configs
└── services/                    # Microservices
    ├── gateway/                 # API Gateway (port 8080)
    ├── auth-service/            # Authentication (port 8081)
    ├── core-service/            # Legacy/AI endpoints (port 8082)
    ├── student-service/         # Student APIs (port 8083)
    ├── teacher-service/         # Teacher APIs (port 8084)
    ├── course-service/          # Course APIs (port 8085)
    ├── score-service/           # Score APIs (port 8086)
    ├── admin-service/           # Admin APIs (port 8087)
    └── ai-service/              # AI services (port 8088)
```

### Service Responsibilities

| Service | Port | Registered Name | Responsibilities |
|---------|------|-----------------|------------------|
| gateway | 8080 | gateway | Routes requests, serves static frontend, CORS handling |
| auth-service | 8081 | auth-service | Login, registration, JWT issuance |
| student-service | 8083 | student-service | Student profile, course enrollment |
| teacher-service | 8084 | teacher-service | Teacher profile, class management |
| course-service | 8085 | course-service | Course catalog, teaching class management |
| score-service | 8086 | score-service | Score recording, grade calculation, statistics |
| admin-service | 8087 | admin-service | Super admin operations, user/course creation |
| ai-service | 8088 | ai-service | AI advisory features (optional) |
| core-service | 8082 | core-service | AI endpoints (legacy), static resources |

### Service Discovery with Nacos

All services register with Nacos at `localhost:8848`:

- **Nacos Console**: http://localhost:8848/nacos (username/password: `nacos/nacos`)
- Services auto-register on startup using `spring.cloud.nacos.discovery.server-addr`
- Gateway uses load balancer URIs (`lb://service-name`) to route requests
- OpenFeign clients use service names (no hardcoded URLs) for inter-service calls

### API Gateway Routes

The gateway on port 8080 routes requests based on path prefixes:

- `/api/auth/**` → auth-service
- `/api/student/ai/**` → core-service (AI endpoints, higher priority)
- `/api/student/**` → student-service
- `/api/teacher/ai/**` → core-service (AI endpoints, higher priority)
- `/api/teacher/**` → teacher-service
- `/api/course/**` → course-service
- `/api/score/**` → score-service
- `/api/admin/**` → admin-service
- `/api/ai/**` → ai-service
- `/`, `/*.html`, `/css/**`, `/js/**` → core-service (static resources)

## Database Setup

**Database:** MySQL on `localhost:3306`
- Database name: `stu_grade_sys`
- Username: `root`
- Password: `12345678`

**Required Services:**
```bash
# Start MySQL (macOS)
mysql.server start

# Start Nacos (Docker)
docker-compose -f docker-compose-nacos.yml up -d
```

**Database Schema:** The system uses 8 main tables:
- `user` - User authentication (username, password, role)
- `student` - Student profiles
- `teacher` - Teacher profiles
- `course` - Course catalog
- `teaching_class` - Teaching class instances
- `enrollment` - Student course enrollments
- `score` - Student grades (usual, midterm, experiment, final, total, grade_point)
- `score_weight` - Configurable score weightings per teaching class

**Important:** Currently all services share the same database. Each service only accesses tables relevant to its domain through its own mappers.

## Build & Run Commands

### Building the Project

**IMPORTANT:** This project requires **JDK 21**. Ensure JAVA_HOME is set correctly:

```bash
# Set JAVA_HOME to JDK 21 (macOS)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java version
java -version  # Should show version 21

# Build entire project from root
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Build specific service
cd services/gateway && mvn clean install

# Run tests
mvn test
```

**Common Build Issue:** If you encounter `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag`, this means Maven is using a different Java version (e.g., Java 25 from Homebrew). Always set JAVA_HOME before building.

### Starting Services

**Prerequisites:**
1. Ensure MySQL is running on localhost:3306
2. Ensure Nacos is running on localhost:8848

**Start All Services (Recommended):**

```bash
# Use the startup script (automatically sets JAVA_HOME)
./start-all-services.sh

# This script will:
# - Verify MySQL and Nacos are running
# - Start all microservices in the correct order
# - Output logs to logs/ directory
```

**Start Services Manually:**

```bash
# Set JAVA_HOME (macOS with JDK 21)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home

# Start services in background (each from their directory)
cd services/auth-service && mvn spring-boot:run &
cd services/course-service && mvn spring-boot:run &
cd services/score-service && mvn spring-boot:run &
cd services/student-service && mvn spring-boot:run &
cd services/teacher-service && mvn spring-boot:run &
cd services/admin-service && mvn spring-boot:run &
cd services/core-service && mvn spring-boot:run &
cd services/ai-service && mvn spring-boot:run &  # Optional
cd services/gateway && mvn spring-boot:run &

# Alternatively, use IntelliJ IDEA: Open root pom.xml and run each service's main class
```

**Stop All Services:**

```bash
# Use the stop script
./stop-all-services.sh
```

**Startup Order Recommendation:**
1. Start Nacos first
2. Start all domain services (auth, student, teacher, course, score, admin, ai, core)
3. Start gateway last (it will discover services from Nacos)

**Verification:**
```bash
# Check all services status with one command
./check-services.sh

# This will verify:
# - MySQL is running
# - Nacos is running and shows registered services
# - Gateway is responsive
# - All 9 services are registered in Nacos
# - Login API is working

# Manual checks:
# Check Nacos service list
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=student-service

# Test through gateway
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}'
```

### Accessing the Application

- **Frontend**: http://localhost:8080/index.html
- **Student Dashboard**: http://localhost:8080/student.html
- **Teacher Dashboard**: http://localhost:8080/teacher.html
- **Admin Dashboard**: http://localhost:8080/admin.html
- **Nacos Console**: http://localhost:8848/nacos
- **API Base**: http://localhost:8080/api

## Authentication & Security

### JWT Configuration

JWT is configured in `libs/security-common` and shared across services:
- Secret: Defined in each service's `application.yml` (`jwt.secret`)
- Expiration: 86400000ms (24 hours)
- **Critical**: All services validating JWTs must use the **same secret**

### Authentication Flow

1. User logs in via `/api/auth/login` (handled by auth-service)
2. Auth-service validates credentials and issues JWT containing:
   - `userId`, `roleId`, `username`, `role` (STUDENT/TEACHER/ADMIN)
3. Client includes token in `Authorization: Bearer <token>` header
4. Each service validates JWT using shared `JwtUtil` from security-common
5. Services extract user info from JWT claims for authorization

### Role-Based Access

- **STUDENT**: Access to `/api/student/**` endpoints
- **TEACHER**: Access to `/api/teacher/**` endpoints
- **ADMIN**: Access to `/api/admin/**` endpoints
- **SUPER_ADMIN**: Configured in core-service `application.yml` (`app.super-admin`)

## Inter-Service Communication

Services communicate using **OpenFeign** with Nacos service discovery:

```java
// Example: StudentService calling ScoreService
@FeignClient(name = "score-service")  // Uses service name, not URL
public interface ScoreServiceClient {
    @GetMapping("/api/score/student/{studentId}")
    Result<Map<String, Object>> getStudentScores(@PathVariable Long studentId);
}
```

**Key Points:**
- No hardcoded URLs in `@FeignClient` annotations
- Nacos provides service instance discovery
- Spring Cloud LoadBalancer handles client-side load balancing
- Gateway also uses `lb://service-name` URIs for routing

## Frontend Integration Contract

The frontend (`src/main/resources/static/` in core-service) uses:
- `BASE_URL: '/api'` in `js/api.js`
- All requests go through gateway on port 8080
- Requests like `/api/auth/login`, `/api/student/scores`, `/api/teacher/classes`

**Important:** When modifying APIs, maintain backward compatibility with the frontend or update `static/js/` accordingly.

## AI Features

The system integrates Alibaba Qwen (通义千问) via LangChain4j for:
- **Student AI Advisor**: Personalized learning recommendations based on grades
- **Teacher AI Assistant**: Teaching analytics and suggestions based on class performance

**Configuration:**
- API Key: Set in `application.yml` as `langchain4j.dashscope.chat-model.api-key`
- Or use environment variable `DASHSCOPE_API_KEY`
- Model: `qwen-max`
- AI endpoints are primarily in `core-service` (legacy pattern):
  - `/api/student/ai/**`
  - `/api/teacher/ai/**`

## Development Guidelines

### Adding New Endpoints

1. **Determine the correct service** based on domain (student/teacher/course/score/admin)
2. **Add controller method** in appropriate service
3. **Update gateway routes** if using a new path pattern (services/gateway/application.yml)
4. **Ensure JWT validation** is enabled for protected endpoints
5. **Test through gateway** at http://localhost:8080

### Shared Code

- **DTOs, VOs, Entities**: Place in `libs/common`
- **Security utilities (JwtUtil, etc.)**: Place in `libs/security-common`
- **Avoid circular dependencies**: Services should not depend on each other's modules, only on libs

### Inter-Service Calls

- Use OpenFeign with service names only (no URLs)
- Handle failures gracefully (consider circuit breakers for production)
- Prefer eventual consistency over synchronous calls where appropriate

### Database Changes

- Currently all services share `stu_grade_sys` database
- Each service should only access its domain tables
- Use MyBatis-Plus mappers in each service
- Mapper XML files are in `src/main/resources/mapper/` of each service

## Testing

```bash
# Run all tests from root
mvn test

# Run tests for specific service
cd services/student-service && mvn test

# Integration testing through gateway
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"2021001","password":"123456"}'
```

## Common Issues

### Service Not Registering with Nacos

**Check:**
1. Nacos is running: `curl http://localhost:8848/nacos/actuator/health`
2. Service configuration has correct `spring.cloud.nacos.discovery.server-addr`
3. Service logs for Nacos connection errors
4. Nacos console shows the service

### Gateway 503 Errors

**Check:**
1. Target service is running and registered in Nacos
2. Gateway routes are configured correctly (see services/gateway/application.yml)
3. Gateway has `spring-cloud-starter-loadbalancer` dependency
4. Route URIs use `lb://` prefix

### JWT Validation Failures

**Check:**
1. All services use the **same** JWT secret in `application.yml`
2. Token is included in `Authorization: Bearer <token>` header
3. Token has not expired (default 24 hours)
4. Service has security-common dependency

### PasswordEncoder Bean Not Found (auth-service)

**Symptom:** `UnsatisfiedDependencyException: No qualifying bean of type 'PasswordEncoder'`

**Root Cause:** auth-service needs PasswordEncoder but should not use the full SecurityConfig from security-common (which includes JWT filter).

**Solution:**
1. auth-service has its own `SecurityConfig` that only provides `PasswordEncoder` bean
2. auth-service's `@ComponentScan` excludes `com.cqu.security.SecurityConfig` using `excludeFilters`
3. See `services/auth-service/src/main/java/com/cqu/auth/AuthApplication.java` and `config/SecurityConfig.java`

### Conflicting Bean Definitions (SecurityConfig or JwtAuthenticationFilter)

**Symptom:** `ConflictingBeanDefinitionException: bean name 'securityConfig' or 'jwtAuthenticationFilter' conflicts`

**Root Cause:** Multiple services define the same bean (from both security-common and their own config package).

**Solutions:**
- **auth-service**: Should exclude security-common's `SecurityConfig` (it doesn't need JWT validation, only PasswordEncoder)
- **core-service**: Should exclude security-common's `SecurityConfig` (has its own with custom static resource rules)
- **Other services**: Can remove their local security files and use security-common's shared config

**Files to check:**
- `/services/*/src/main/java/com/cqu/*/security/` - Remove duplicate JWT classes if present
- `/services/*/src/main/java/com/cqu/*/*Application.java` - Add excludeFilters if needed

**Example fix (in XxxApplication.java):**
```java
@ComponentScan(
    basePackages = {"com.cqu.xxx", "com.cqu.security", "com.cqu.common"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
```

### Feign Client Failures

**Check:**
1. Target service name matches Nacos registration name exactly
2. `@FeignClient` annotation has no `url` parameter (should use service discovery)
3. Target service endpoint path is correct
4. Services can communicate (network/firewall not blocking)

## Important Files

- `pom.xml` - Root POM with dependency management
- `libs/common/` - Shared DTOs, VOs, entities, exception handler
- `libs/security-common/` - JWT utilities, security configs
- `services/gateway/src/main/resources/application.yml` - Gateway routes
- `services/*/src/main/resources/application.yml` - Each service's config (port, Nacos, DB)
- `services/core-service/src/main/resources/static/` - Frontend files
- `NACOS-GUIDE.md` - Detailed Nacos setup and troubleshooting
- `README.md` - User-facing documentation in Chinese

## Notes

- This project was refactored from a monolith to microservices architecture
- Nacos service discovery enables dynamic service registration and load balancing
- Each service validates JWT independently using shared security-common library
- AI features require valid DashScope API key
- No Redis dependency (purely JWT-based stateless authentication)
- Frontend uses vanilla JavaScript (no framework)
