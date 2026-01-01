# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Student Grade Management System** (学生成绩管理系统) built with Spring Boot 3.x for the backend and vanilla JavaScript for the frontend. The system supports role-based access for students and teachers, with JWT authentication and AI-powered academic advisory features using Alibaba's Qwen (通义千问) through LangChain4j.

**Key Technologies:**
- Spring Boot 3.x with Spring Security
- MyBatis for database ORM
- MySQL database (`stu_grade_sys`)
- Redis for caching (configured but not fully implemented)
- JWT for stateless authentication
- LangChain4j with DashScope (Alibaba Qwen) for AI features
- Vanilla JavaScript frontend (no framework)

## Database Setup

**Database:** MySQL on `localhost:3306`
- Database name: `stu_grade_sys`
- Username: `root`
- Password: `12345678`

**Required Services:**
```bash
# Start MySQL
mysql.server start

# Start Redis (configured but optional)
redis-server
```

**Database Schema:** The system uses 8 main tables:
- `user` - User authentication (username, password, role)
- `student` - Student profiles
- `teacher` - Teacher profiles
- `course` - Course catalog
- `teaching_class` - Teaching class instances (links courses, teachers, semesters)
- `enrollment` - Student course enrollments
- `score` - Student grades (usual, midterm, experiment, final, total, grade_point)
- `score_weight` - Configurable score weightings per teaching class

## Build & Run Commands

Since no `pom.xml` or `mvnw` exists in this directory, development commands must be run from an IDE (IntelliJ IDEA):

**Run Application:**
- Main class: `com.cqu.exp04.Exp04Application`
- Default port: `8080`
- Run from IDE or use Maven wrapper from parent directory if available

**Testing:**
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=Exp04ApplicationTests
```

**Access Application:**
- Frontend: `http://localhost:8080/index.html`
- Student Dashboard: `http://localhost:8080/student.html`
- Teacher Dashboard: `http://localhost:8080/teacher.html`
- API Base: `http://localhost:8080/api`

## Architecture

### Package Structure

```
com.cqu.exp04/
├── entity/          # Domain models (User, Student, Teacher, Course, Score, etc.)
├── dto/             # Request DTOs (LoginRequest, StudentRegisterRequest, ScoreInputRequest, etc.)
├── vo/              # Response VOs (Result, LoginResponse, StudentScoreVO, ClassScoreStatisticsVO)
├── mapper/          # MyBatis mapper interfaces
├── service/         # Business logic layer
│   └── impl/        # Service implementations
├── controller/      # REST API controllers
├── config/          # Spring configurations (SecurityConfig, LangChain4jConfig)
├── security/        # JWT utilities (JwtUtil, JwtAuthenticationFilter)
└── exception/       # Global exception handler
```

### Authentication Flow

1. **User Registration:** Students/teachers register via `/api/auth/register/student` or `/api/auth/register/teacher`
2. **Login:** POST to `/api/auth/login` returns JWT token and user details
3. **Authorization:** JWT token in `Authorization: Bearer <token>` header
4. **Role Extraction:** `JwtAuthenticationFilter` extracts user info and sets `roleId` + `userRole` in request attributes
5. **Role-Based Access:** Controllers check `request.getAttribute("roleId")` for user identity

**Security Configuration:**
- Public endpoints: `/api/auth/**`, `/api/course/**`, static resources
- Student endpoints: `/api/student/**` requires `ROLE_STUDENT`
- Teacher endpoints: `/api/teacher/**` requires `ROLE_TEACHER`
- Admin endpoints: `/api/admin/**` requires `ROLE_ADMIN` (not implemented)

### MyBatis Integration

- **Mapper XML files:** Located in `src/main/resources/mapper/`
- **Key mappers:** `ScoreMapper.xml`, `TeachingClassMapper.xml`, `UserMapper.xml`
- **Important:** Many mapper methods are declared in Java interfaces but NOT implemented in XML files - this is a known TODO
- **Convention:** Snake_case in DB, camelCase in Java (auto-converted by `map-underscore-to-camel-case: true`)

### AI Integration (LangChain4j + Qwen)

**Configuration:**
- API Key: Set via `DASHSCOPE_API_KEY` environment variable or defaults to hardcoded key in `application.yaml`
- Model: `qwen-max` (Alibaba's Qwen)
- Features: `enable-search: true` for web search capabilities

**AI Services:**
1. **Student Advisor** (`AIService.studentConsult`):
   - Analyzes student's all course grades
   - Provides personalized learning advice
   - Endpoint: `POST /api/student/ai/consult`

2. **Teacher Analytics** (`AIService.teacherConsult`):
   - Analyzes class performance statistics
   - Provides teaching improvement suggestions
   - Endpoint: `POST /api/teacher/ai/consult`

**Usage Pattern:**
```java
@Autowired
private ChatLanguageModel chatLanguageModel;  // Auto-configured by Spring Boot Starter

String response = chatLanguageModel.generate(promptWithContext);
```

### Score Calculation System

**Score Components:**
- `usual_score` (平时成绩)
- `midterm_score` (期中成绩)
- `experiment_score` (实验成绩)
- `final_score` (期末成绩)

**Calculation:**
- Weights are configurable per teaching class via `score_weight` table
- `total_score` = weighted sum of components
- `grade_point` = calculated based on total score (90+: 4.0, 85-89: 3.7, etc.)
- Each component has a timestamp field (e.g., `usual_score_time`)

## API Endpoints

### Authentication (`/api/auth`)
- `POST /login` - User login (returns JWT + role info)
- `POST /register/student` - Student registration
- `POST /register/teacher` - Teacher registration
- `GET /test` - Health check

### Student APIs (`/api/student`)
- `GET /profile` - Get student profile
- `PUT /profile` - Update profile
- `GET /scores` - Get all grades with statistics
- `GET /enrollments` - Get enrolled courses
- `POST /enroll` - Enroll in course
- `DELETE /enroll/{enrollmentId}` - Drop course
- `POST /ai/consult` - AI learning advisor

### Teacher APIs (`/api/teacher`)
- `GET /profile` - Get teacher profile
- `PUT /profile` - Update profile
- `GET /classes` - Get teaching classes
- `GET /class/{classId}/students` - Get class roster
- `GET /class/{classId}/scores` - Get class grades
- `POST /score/input` - Input/update single grade
- `POST /score/batch` - Batch input grades
- `GET /class/{classId}/statistics` - Get class statistics
- `POST /ai/consult` - AI teaching analytics (requires `teachingClassId`)

### Course APIs (`/api/course`)
- Public endpoints for browsing courses (implementation varies)

## Known Issues & TODOs

Based on `我对项目的下一步思考.txt`:

1. **Mapper Implementation Incomplete:** Many mapper methods declared in Java interfaces lack XML implementations
2. **AI Context Memory:** No conversation history persistence - each AI query is stateless
3. **Redis Underutilized:** Configured but not used for caching or AI conversation storage
4. **Performance:** No optimization for high-concurrency scenarios (e.g., grade release spikes)
5. **Architecture:** Coupling issues - needs refactoring for better separation of concerns
6. **Microservices:** Currently monolithic - distributed architecture not implemented

## Development Guidelines

### Adding New Features

1. **New Entity:** Create in `entity/` package, extend `BaseEntity` for common fields (`id`, `createTime`, `updateTime`)
2. **New Mapper:** Create interface in `mapper/` package + XML in `resources/mapper/`
3. **MyBatis ResultMaps:** Use `<association>` for one-to-one, `<collection>` for one-to-many relationships
4. **Service Layer:** Define interface in `service/`, implement in `service/impl/`
5. **Controller:** Add to appropriate controller or create new one with `@RestController`, `@RequestMapping`, `@CrossOrigin`
6. **Security:** Update `SecurityConfig` if new endpoints need authentication rules

### Working with MyBatis

**Check before implementing queries:**
1. Read the mapper interface to see declared methods
2. Read the corresponding XML file to see which methods are implemented
3. Implement missing XML queries before using mapper methods

**Common patterns:**
```xml
<!-- Simple select with association -->
<resultMap id="ScoreWithStudentResultMap" type="Score" extends="ScoreResultMap">
    <association property="student" javaType="Student">
        <id property="id" column="s_id"/>
        <result property="name" column="s_name"/>
    </association>
</resultMap>
```

### AI Prompt Engineering

When modifying AI features in `AIService.java`:
- Keep prompts in Chinese for better Qwen model performance
- Include concrete data context (scores, statistics) before questions
- Limit response length in prompt (e.g., "300字以内")
- Use structured format requests (numbered lists, specific sections)

### Frontend Integration

- **Static files:** Located in `src/main/resources/static/`
- **API calls:** Use `utils.js` for common API request helpers
- **Token storage:** JWT stored in `localStorage.getItem('token')`
- **Role-based UI:** Check `localStorage.getItem('role')` to show/hide features

## Environment Variables

```bash
# Required for AI features
export DASHSCOPE_API_KEY=sk-your-api-key-here

# Optional overrides (defaults in application.yaml)
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/stu_grade_sys
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=12345678
export SPRING_DATA_REDIS_HOST=localhost
export SPRING_DATA_REDIS_PORT=6379
```

## Debugging

**Enable SQL logging:**
Already configured in `application.yaml`:
```yaml
logging:
  level:
    com.cqu.exp04.mapper: debug  # Shows SQL statements
```

**Common issues:**
- **401 Unauthorized:** Check JWT token presence and validity
- **403 Forbidden:** User role doesn't match endpoint requirements
- **500 SQL errors:** Likely missing mapper XML implementation - check console for which mapper method failed
- **AI timeout:** DashScope API may be slow, consider increasing `timeout` in config
