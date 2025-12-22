-- 学生成绩管理系统 - 测试数据插入脚本
-- 密码统一使用BCrypt加密后的 "123456"
-- BCrypt: $2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau

USE stu_grade_sys;

-- ==================== 1. 插入教师用户和教师信息 ====================

-- 教师1
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1001', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '张伟', 'T1001@cqu.edu.cn', '13800001001', 1);
SET @teacher1_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1001', @teacher1_user_id, '张伟', 'MALE', '教授', '计算机学院', 'T1001@cqu.edu.cn', '13800001001');
SET @teacher1_id = LAST_INSERT_ID();

-- 教师2
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1002', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '李娜', 'T1002@cqu.edu.cn', '13800001002', 1);
SET @teacher2_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1002', @teacher2_user_id, '李娜', 'FEMALE', '副教授', '计算机学院', 'T1002@cqu.edu.cn', '13800001002');
SET @teacher2_id = LAST_INSERT_ID();

-- 教师3
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1003', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '王强', 'T1003@cqu.edu.cn', '13800001003', 1);
SET @teacher3_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1003', @teacher3_user_id, '王强', 'MALE', '讲师', '软件学院', 'T1003@cqu.edu.cn', '13800001003');
SET @teacher3_id = LAST_INSERT_ID();

-- 教师4
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1004', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '刘芳', 'T1004@cqu.edu.cn', '13800001004', 1);
SET @teacher4_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1004', @teacher4_user_id, '刘芳', 'FEMALE', '副教授', '软件学院', 'T1004@cqu.edu.cn', '13800001004');
SET @teacher4_id = LAST_INSERT_ID();

-- 教师5
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1005', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '陈明', 'T1005@cqu.edu.cn', '13800001005', 1);
SET @teacher5_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1005', @teacher5_user_id, '陈明', 'MALE', '教授', '人工智能学院', 'T1005@cqu.edu.cn', '13800001005');
SET @teacher5_id = LAST_INSERT_ID();

-- 教师6
INSERT INTO user (username, password, role, real_name, email, phone, status) VALUES
('T1006', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'TEACHER', '赵丽', 'T1006@cqu.edu.cn', '13800001006', 1);
SET @teacher6_user_id = LAST_INSERT_ID();
INSERT INTO teacher (teacher_no, user_id, name, gender, title, department, email, phone) VALUES
('T1006', @teacher6_user_id, '赵丽', 'FEMALE', '讲师', '人工智能学院', 'T1006@cqu.edu.cn', '13800001006');
SET @teacher6_id = LAST_INSERT_ID();

-- ==================== 2. 插入课程信息 ====================

INSERT INTO course (course_no, course_name, credit, hours, course_type, description) VALUES
('CS101', 'Java程序设计', 4.0, 64, '专业必修课', 'Java面向对象编程语言基础与应用'),
('CS102', '数据结构与算法', 4.0, 64, '专业必修课', '数据结构和算法设计与分析'),
('CS103', '数据库原理', 3.5, 56, '专业必修课', '关系数据库理论与SQL应用'),
('CS104', '操作系统', 3.5, 56, '专业必修课', '操作系统原理与实现'),
('CS105', '计算机网络', 3.0, 48, '专业必修课', '计算机网络协议与应用');

SET @course1_id = (SELECT id FROM course WHERE course_no = 'CS101');
SET @course2_id = (SELECT id FROM course WHERE course_no = 'CS102');
SET @course3_id = (SELECT id FROM course WHERE course_no = 'CS103');
SET @course4_id = (SELECT id FROM course WHERE course_no = 'CS104');
SET @course5_id = (SELECT id FROM course WHERE course_no = 'CS105');

-- ==================== 3. 插入教学班信息 ====================

-- Java程序设计 - 2个班
INSERT INTO teaching_class (class_no, course_id, teacher_id, semester, max_students, current_students, classroom, schedule, status) VALUES
('TC0001', @course1_id, @teacher1_id, '2024-2025-1', 50, 0, '教1-101', '周一 1-2节', 1),
('TC0002', @course1_id, @teacher2_id, '2024-2025-1', 50, 0, '教1-102', '周二 3-4节', 1);

-- 数据结构与算法 - 2个班
INSERT INTO teaching_class (class_no, course_id, teacher_id, semester, max_students, current_students, classroom, schedule, status) VALUES
('TC0003', @course2_id, @teacher2_id, '2024-2025-1', 50, 0, '教2-201', '周三 1-2节', 1),
('TC0004', @course2_id, @teacher3_id, '2024-2025-1', 50, 0, '教2-202', '周四 3-4节', 1);

-- 数据库原理 - 3个班
INSERT INTO teaching_class (class_no, course_id, teacher_id, semester, max_students, current_students, classroom, schedule, status) VALUES
('TC0005', @course3_id, @teacher3_id, '2024-2025-1', 50, 0, '教3-301', '周一 3-4节', 1),
('TC0006', @course3_id, @teacher4_id, '2024-2025-1', 50, 0, '教3-302', '周二 1-2节', 1),
('TC0007', @course3_id, @teacher1_id, '2024-2025-1', 50, 0, '教3-303', '周三 5-6节', 1);

-- 操作系统 - 2个班
INSERT INTO teaching_class (class_no, course_id, teacher_id, semester, max_students, current_students, classroom, schedule, status) VALUES
('TC0008', @course4_id, @teacher4_id, '2024-2025-1', 50, 0, '教4-401', '周四 1-2节', 1),
('TC0009', @course4_id, @teacher5_id, '2024-2025-1', 50, 0, '教4-402', '周五 3-4节', 1);

-- 计算机网络 - 2个班
INSERT INTO teaching_class (class_no, course_id, teacher_id, semester, max_students, current_students, classroom, schedule, status) VALUES
('TC0010', @course5_id, @teacher5_id, '2024-2025-1', 50, 0, '教5-501', '周一 5-6节', 1),
('TC0011', @course5_id, @teacher6_id, '2024-2025-1', 50, 0, '教5-502', '周二 5-6节', 1);

-- ==================== 4. 插入学生用户和学生信息 (示例10个) ====================

-- 学生1
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100001', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '张三', '2023100001@stu.cqu.edu.cn', 1);
SET @student1_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100001', @student1_user_id, '张三', 'MALE', '2004-05-15', '计算机科学与技术', '2023级1班', 2023, 2023);
SET @student1_id = LAST_INSERT_ID();

-- 学生2
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100002', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '李四', '2023100002@stu.cqu.edu.cn', 1);
SET @student2_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100002', @student2_user_id, '李四', 'MALE', '2004-08-20', '计算机科学与技术', '2023级1班', 2023, 2023);
SET @student2_id = LAST_INSERT_ID();

-- 学生3
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100003', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '王五', '2023100003@stu.cqu.edu.cn', 1);
SET @student3_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100003', @student3_user_id, '王五', 'FEMALE', '2004-03-10', '软件工程', '2023级2班', 2023, 2023);
SET @student3_id = LAST_INSERT_ID();

-- 学生4
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100004', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '赵六', '2023100004@stu.cqu.edu.cn', 1);
SET @student4_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100004', @student4_user_id, '赵六', 'FEMALE', '2004-11-25', '软件工程', '2023级2班', 2023, 2023);
SET @student4_id = LAST_INSERT_ID();

-- 学生5
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100005', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '孙七', '2023100005@stu.cqu.edu.cn', 1);
SET @student5_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100005', @student5_user_id, '孙七', 'MALE', '2004-07-08', '人工智能', '2023级3班', 2023, 2023);
SET @student5_id = LAST_INSERT_ID();

-- 学生6
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100006', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '周八', '2023100006@stu.cqu.edu.cn', 1);
SET @student6_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100006', @student6_user_id, '周八', 'FEMALE', '2004-09-12', '人工智能', '2023级3班', 2023, 2023);
SET @student6_id = LAST_INSERT_ID();

-- 学生7
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100007', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '吴九', '2023100007@stu.cqu.edu.cn', 1);
SET @student7_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100007', @student7_user_id, '吴九', 'MALE', '2004-01-30', '数据科学', '2023级4班', 2023, 2023);
SET @student7_id = LAST_INSERT_ID();

-- 学生8
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100008', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '郑十', '2023100008@stu.cqu.edu.cn', 1);
SET @student8_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100008', @student8_user_id, '郑十', 'FEMALE', '2004-06-18', '数据科学', '2023级4班', 2023, 2023);
SET @student8_id = LAST_INSERT_ID();

-- 学生9
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100009', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '钱一', '2023100009@stu.cqu.edu.cn', 1);
SET @student9_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100009', @student9_user_id, '钱一', 'MALE', '2004-12-05', '网络工程', '2023级5班', 2023, 2023);
SET @student9_id = LAST_INSERT_ID();

-- 学生10
INSERT INTO user (username, password, role, real_name, email, status) VALUES
('2023100010', '$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau', 'STUDENT', '孙二', '2023100010@stu.cqu.edu.cn', 1);
SET @student10_user_id = LAST_INSERT_ID();
INSERT INTO student (student_no, user_id, name, gender, birth_date, major, class_name, grade, enrollment_year) VALUES
('2023100010', @student10_user_id, '孙二', 'FEMALE', '2004-04-22', '网络工程', '2023级5班', 2023, 2023);
SET @student10_id = LAST_INSERT_ID();

-- ==================== 5. 插入选课记录(示例) ====================

-- 学生1 选3门课
INSERT INTO enrollment (student_id, teaching_class_id, enroll_time, status) VALUES
(@student1_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0001'), NOW(), 1),
(@student1_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0003'), NOW(), 1),
(@student1_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0005'), NOW(), 1);

-- 学生2 选4门课
INSERT INTO enrollment (student_id, teaching_class_id, enroll_time, status) VALUES
(@student2_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0001'), NOW(), 1),
(@student2_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0004'), NOW(), 1),
(@student2_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0006'), NOW(), 1),
(@student2_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0008'), NOW(), 1);

-- 学生3 选3门课
INSERT INTO enrollment (student_id, teaching_class_id, enroll_time, status) VALUES
(@student3_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0002'), NOW(), 1),
(@student3_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0003'), NOW(), 1),
(@student3_id, (SELECT id FROM teaching_class WHERE class_no = 'TC0007'), NOW(), 1);

-- 更新教学班当前学生数
UPDATE teaching_class SET current_students = (SELECT COUNT(*) FROM enrollment WHERE teaching_class_id = teaching_class.id);

-- ==================== 6. 插入成绩权重配置 ====================

INSERT INTO score_weight (course_id, usual_weight, midterm_weight, experiment_weight, final_weight) VALUES
(@course1_id, 0.20, 0.20, 0.20, 0.40),
(@course2_id, 0.20, 0.20, 0.20, 0.40),
(@course3_id, 0.20, 0.20, 0.20, 0.40),
(@course4_id, 0.20, 0.20, 0.20, 0.40),
(@course5_id, 0.20, 0.20, 0.20, 0.40);

-- 完成提示
SELECT '数据初始化完成！' AS message;
SELECT '默认密码: 123456' AS note;
SELECT CONCAT('教师账号: T1001 ~ T1006 (共', COUNT(*), '个)') AS teachers FROM teacher;
SELECT CONCAT('学生账号: 2023100001 ~ 2023100010 (示例', COUNT(*), '个)') AS students FROM student;
SELECT CONCAT('课程数量: ', COUNT(*), '门') AS courses FROM course;
SELECT CONCAT('教学班数量: ', COUNT(*), '个') AS classes FROM teaching_class;
