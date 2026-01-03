# 前端数据显示修复 - 第二部分

## 更新时间
2026年1月4日

## 问题描述
在第一部分修复后，前端页面仍有部分数据无法显示：
- 学生成绩页面：课程名称、学分、学期显示为"-"，但总评和绩点正常显示
- 选课管理页面：课程信息不完整
- 教师端：班级学生信息显示不全（缺少学生姓名、学号）

## 根本原因
微服务架构下，各服务返回的数据不包含关联实体的详细信息：
- score-service 只返回成绩数据，不包含课程名称
- student-service 的选课列表只返回基本信息，不包含课程详情
- teacher-service 的班级学生列表缺少学生姓名等信息

## 修复方案

### 1. course-service 修改
**文件**: `services/course-service/src/main/java/com/cqu/course/service/impl/CourseServiceImpl.java`

修改了以下方法：
- `getClassDetail()`: 现在返回完整的课程信息（courseName, courseNo, credit, hours等）
- `getAvailableClasses()`: 现在返回完整的课程信息和状态文本

### 2. score-service 修改
**新增文件**:
- `services/score-service/src/main/java/com/cqu/score/client/CourseServiceClient.java`
- `services/score-service/src/main/java/com/cqu/score/client/StudentServiceClient.java`

**修改文件**:
- `services/score-service/src/main/java/com/cqu/score/service/impl/ScoreServiceImpl.java`
  - `getStudentScores()`: 现在通过调用course-service获取课程信息
  - 新增 `getClassStudentsWithScores()`: 返回包含学生信息的班级成绩列表

- `services/score-service/src/main/java/com/cqu/score/controller/ScoreController.java`
  - 新增 `/api/score/class/{classId}/students` 接口

- `services/score-service/pom.xml`: 添加OpenFeign依赖
- `services/score-service/src/main/java/com/cqu/score/ScoreApplication.java`: 添加@EnableFeignClients

### 3. student-service 修改
**修改文件**:
- `services/student-service/src/main/java/com/cqu/student/service/impl/StudentServiceImpl.java`
  - `getMyEnrollments()`: 现在返回完整的选课信息

- `services/student-service/src/main/java/com/cqu/student/controller/StudentController.java`
  - 新增 `/api/student/{studentId}` 接口

### 4. teacher-service 修改
**修改文件**:
- `services/teacher-service/src/main/java/com/cqu/teacher/client/CourseServiceClient.java`
  - 新增 `getClassDetail()` 方法

- `services/teacher-service/src/main/java/com/cqu/teacher/client/ScoreServiceClient.java`
  - 新增 `getClassStudentsWithScores()` 方法

- `services/teacher-service/src/main/java/com/cqu/teacher/service/impl/TeacherServiceImpl.java`
  - `getMyClasses()`: 现在返回完整的课程信息
  - `getClassStudents()`: 现在调用score-service的新接口

### 5. 前端 api.js 修改
**文件**: `src/main/resources/static/js/api.js`

重写了API客户端，主要改进：
- `getClassStatistics()`: 自动添加teacherId参数
- `inputScore()`: 自动添加teacherId参数
- `batchInputScores()`: 自动添加teacherId参数
- AI咨询接口更新为新的服务端点

## 返回数据格式

### 学生成绩 (/api/student/scores)
```json
{
  "code": 200,
  "data": {
    "scores": [
      {
        "courseName": "高等数学",
        "courseNo": "MATH001",
        "credit": 4.0,
        "semester": "2025-2026-1",
        "usualScore": 88,
        "midtermScore": 85,
        "experimentScore": 90,
        "finalScore": 87,
        "totalScore": 87.4,
        "gradePoint": 3.7
      }
    ],
    "averageScore": 87.4,
    "gpa": 3.7,
    "totalCourses": 3,
    "completedCourses": 3,
    "totalCredits": 12.0
  }
}
```

### 班级学生成绩 (/api/teacher/class/{id}/students)
```json
{
  "code": 200,
  "data": [
    {
      "studentId": 1,
      "studentNo": "2021001",
      "studentName": "张三",
      "usualScore": 88,
      "midtermScore": 85,
      "experimentScore": 90,
      "finalScore": 87,
      "totalScore": 87.4,
      "gradePoint": 3.7
    }
  ]
}
```

## 重新构建命令

```bash
# 重新构建修改过的服务
mvn clean install -pl services/score-service,services/course-service,services/student-service,services/teacher-service -DskipTests

# 或者重新构建所有服务
mvn clean install -DskipTests
```

## 注意事项
1. 服务间调用需要传递Authorization头
2. 使用了缓存机制避免重复调用
3. 调用失败时会返回默认值，不会导致整个请求失败
