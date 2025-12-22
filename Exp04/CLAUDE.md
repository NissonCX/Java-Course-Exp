# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Exp04 is part of a Java Course series (重庆大学 Java企业级应用开发). This is a Spring Boot 3.2.1 application using:
- **Java 21**
- **Spring Boot Web MVC**
- **MyBatis** for database access
- **MySQL** (database: `stu_grade_sys`)
- **Redis** for caching
- **Lombok** for reducing boilerplate

The project is a student grade management system backend, building upon previous experiments in this repository series.

## Development Commands

### Build and Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Build JAR package
mvn clean package

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=Exp04ApplicationTests
```

### Database Setup
The application connects to MySQL at `localhost:3306/stu_grade_sys`. Ensure:
1. MySQL is running on port 3306
2. Database `stu_grade_sys` exists
3. Credentials match `application.yaml` (default: root/12345678)

### Redis Setup
The application requires Redis running on `localhost:6379` (database 0).

## Project Architecture

### Package Structure Pattern
Based on the repository's previous experiments, this project likely follows:
```
com.cqu.exp04/
├── entity/          # Domain entities (Student, Course, Score, etc.)
├── mapper/          # MyBatis mapper interfaces (@MapperScan configured)
├── service/         # Business logic layer
├── controller/      # REST API endpoints (if web service)
└── config/          # Configuration classes
```

### MyBatis Configuration
- **Mapper XML location**: `src/main/resources/mapper/*.xml`
- **Mapper scanning**: `@MapperScan("com.cqu.exp04.mapper")` in main application class
- **Convention**: Underscore to camelCase mapping enabled
- **Logging**: MyBatis SQL logging enabled via `StdOutImpl`

### Database Entity Conventions
Following patterns from previous experiments (Exp02, Exp03):
- Student records (学号, 姓名, 性别, 专业, etc.)
- Course information
- Score/Grade management with multiple components (平时成绩, 期中, 实验, 期末)
- Teaching class assignments

### Redis Integration
Spring Data Redis is configured for caching. Use `@Cacheable`, `@CacheEvict`, etc. annotations or `RedisTemplate` for cache operations.

## Configuration Files

### application.yaml
Located at `src/main/resources/application.yaml`. Key configurations:
- **Database URL**: jdbc:mysql://localhost:3306/stu_grade_sys
- **MyBatis mapper location**: classpath:mapper/*.xml
- **Logging level**: DEBUG for mapper package

**Note**: Update database credentials before running if defaults don't match your environment.

## Development Practices

### Lombok Usage
The project uses Lombok extensively. Common annotations:
- `@Data` for entities
- `@Builder` for builder pattern
- `@AllArgsConstructor` / `@NoArgsConstructor`
- Annotation processing is configured in maven-compiler-plugin

### MyBatis Mapper Development
1. Create interface in `mapper` package
2. Create corresponding XML in `src/main/resources/mapper/`
3. Use parameterized queries to prevent SQL injection
4. Leverage MyBatis dynamic SQL features (`<if>`, `<where>`, `<foreach>`)

### Testing
- Test classes in `src/test/java/com/cqu/exp04/`
- Spring Boot test dependencies included for web and Redis testing

## Context from Previous Experiments

This is Exp04 in a series. Previous experiments:
- **Exp01**: Command-line grade management (no database, in-memory)
- **Exp02**: RBAC permission system with MySQL and JDBC
- **Exp03**: Vue 3 + TypeScript frontend for grade management

Exp04 likely builds a proper backend API for the grade management system, combining web services, database persistence, and caching.

## Known Database Schema

Database name: `stu_grade_sys` (student grade system)

Based on the context, expected tables may include:
- Students (学生)
- Courses (课程)
- Teachers (教师)
- Teaching classes (教学班)
- Scores/Grades (成绩)
- Enrollments (选课记录)

Always check existing schema before making changes.
