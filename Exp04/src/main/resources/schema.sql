-- 学生成绩管理系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS stu_grade_sys CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE stu_grade_sys;

-- 用户表 (统一认证)
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL COMMENT '用户角色',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) COMMENT='用户表';

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    class_name VARCHAR(50) COMMENT '班级',
    grade INT NOT NULL COMMENT '年级',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_student_no (student_no),
    INDEX idx_major (major),
    INDEX idx_grade (grade)
) COMMENT='学生表';

-- 教师表
CREATE TABLE IF NOT EXISTS teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    teacher_no VARCHAR(20) NOT NULL UNIQUE COMMENT '教工号',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    department VARCHAR(100) NOT NULL COMMENT '院系',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_teacher_no (teacher_no),
    INDEX idx_department (department)
) COMMENT='教师表';

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_no VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit DECIMAL(3,1) NOT NULL COMMENT '学分',
    hours INT NOT NULL COMMENT '学时',
    course_type VARCHAR(50) COMMENT '课程类型',
    description TEXT COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course_no (course_no),
    INDEX idx_course_name (course_name)
) COMMENT='课程表';

-- 教学班表
CREATE TABLE IF NOT EXISTS teaching_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教学班ID',
    class_no VARCHAR(20) NOT NULL UNIQUE COMMENT '教学班号',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期(如: 2024-2025-1)',
    max_students INT DEFAULT 50 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    classroom VARCHAR(50) COMMENT '教室',
    schedule VARCHAR(100) COMMENT '上课时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-开课中, 0-已结课',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE,
    INDEX idx_class_no (class_no),
    INDEX idx_semester (semester),
    INDEX idx_teacher_id (teacher_id)
) COMMENT='教学班表';

-- 选课记录表
CREATE TABLE IF NOT EXISTS enrollment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    teaching_class_id BIGINT NOT NULL COMMENT '教学班ID',
    enroll_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-退课',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (teaching_class_id) REFERENCES teaching_class(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_class (student_id, teaching_class_id),
    INDEX idx_student_id (student_id),
    INDEX idx_teaching_class_id (teaching_class_id)
) COMMENT='选课记录表';

-- 成绩表
CREATE TABLE IF NOT EXISTS score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    enrollment_id BIGINT NOT NULL COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    teaching_class_id BIGINT NOT NULL COMMENT '教学班ID',
    usual_score DECIMAL(5,2) COMMENT '平时成绩',
    midterm_score DECIMAL(5,2) COMMENT '期中成绩',
    experiment_score DECIMAL(5,2) COMMENT '实验成绩',
    final_score DECIMAL(5,2) COMMENT '期末成绩',
    total_score DECIMAL(5,2) COMMENT '总评成绩',
    grade_point DECIMAL(3,2) COMMENT '绩点',
    usual_score_time DATETIME COMMENT '平时成绩录入时间',
    midterm_score_time DATETIME COMMENT '期中成绩录入时间',
    experiment_score_time DATETIME COMMENT '实验成绩录入时间',
    final_score_time DATETIME COMMENT '期末成绩录入时间',
    total_score_time DATETIME COMMENT '总评成绩计算时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (teaching_class_id) REFERENCES teaching_class(id) ON DELETE CASCADE,
    UNIQUE KEY uk_enrollment (enrollment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_teaching_class_id (teaching_class_id),
    INDEX idx_total_score (total_score)
) COMMENT='成绩表';

-- 成绩权重配置表
CREATE TABLE IF NOT EXISTS score_weight (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权重ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    usual_weight DECIMAL(3,2) DEFAULT 0.20 COMMENT '平时成绩权重',
    midterm_weight DECIMAL(3,2) DEFAULT 0.20 COMMENT '期中成绩权重',
    experiment_weight DECIMAL(3,2) DEFAULT 0.20 COMMENT '实验成绩权重',
    final_weight DECIMAL(3,2) DEFAULT 0.40 COMMENT '期末成绩权重',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    UNIQUE KEY uk_course (course_id)
) COMMENT='成绩权重配置表';

-- AI对话记录表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_role VARCHAR(20) NOT NULL COMMENT '用户角色',
    conversation_type VARCHAR(50) NOT NULL COMMENT '对话类型: STUDENT_ADVICE, TEACHER_STATISTICS',
    user_message TEXT NOT NULL COMMENT '用户消息',
    ai_response TEXT NOT NULL COMMENT 'AI响应',
    context_data JSON COMMENT '上下文数据(成绩等)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT='AI对话记录表';
