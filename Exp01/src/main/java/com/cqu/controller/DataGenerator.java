package com.cqu.controller;

import com.cqu.model.*;

import java.time.LocalDate;
import java.util.*;

public class DataGenerator {

    private static final Random random = new Random();
    private static final String[] FIRST_NAMES = {
            "张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴",
            "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    };

    private static final String[] LAST_NAMES = {
            "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
            "勇", "艳", "杰", "娟", "涛", "明", "超", "秀英", "霞", "平"
    };

    private static final String[] COURSE_NAMES = {
            "高等数学", "大学英语", "Java企业级应用", "数据结构", "算法设计", "操作系统", "数据库原理"
    };

    private static final String[] SEMESTERS = {
            "2024年春季", "2024年秋季", "2025年春季", "2025年秋季"
    };

    /**
     * 生成指定数量的学生
     */
    public static List<Student> generateStudents(int count) {
        List<Student> students = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (int i = 0; i < count; i++) {
            // 生成唯一学号，利用HashSet确保每个学号唯一
            String studentId;
            do {
                studentId = String.format("S%04d", random.nextInt(2000));
            } while (usedIds.contains(studentId));
            usedIds.add(studentId);

            // 生成姓名
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String name = firstName + lastName;

            // 生成性别
            String gender = random.nextBoolean() ? "男" : "女";

            // 生成年龄
            int age = 18 + random.nextInt(5); // 18-22岁

            Student student = new Student();
            student.setStudentId(studentId);
            student.setName(name);
            student.setGender(gender);
            student.setAge(age);

            students.add(student);
        }

        return students;
    }

    /**
     * 生成指定数量的教师，类似学生生成做法
     */
    public static List<Teacher> generateTeachers(int count) {
        List<Teacher> teachers = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (int i = 0; i < count; i++) {
            // 生成唯一教师编号，同样是利用HashSet确保每个教师编号唯一
            String teacherId;
            do {
                teacherId = String.format("T%03d", random.nextInt(200));
            } while (usedIds.contains(teacherId));
            usedIds.add(teacherId);

            // 生成姓名
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String name = firstName + lastName;

            // 生成性别
            String gender = random.nextBoolean() ? "男" : "女";

            // 生成年龄
            int age = 30 + random.nextInt(20); // 30-49岁

            Teacher teacher = new Teacher();
            teacher.setTno(teacherId);
            teacher.setName(name);
            teacher.setGender(gender);
            teacher.setAge(age);

            teachers.add(teacher);
        }

        return teachers;
    }


    /**
     * 生成课程
     */
    public static List<Course> generateCourses(int count) {
        List<Course> courses = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        // 确保至少生成指定数量的课程，但不超过预设课程名数组长度
        int actualCount = Math.min(count, COURSE_NAMES.length);

        for (int i = 0; i < actualCount; i++) {
            // 生成唯一课程ID，利用HashSet确保每个课程编号唯一
            int courseId;
            do {
                courseId = 100 + random.nextInt(900); // 100-999
            } while (usedIds.contains(courseId));
            usedIds.add(courseId);

            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseName(COURSE_NAMES[i]);

            courses.add(course);
        }

        return courses;
    }

    /**
     * 生成教学班
     */
    public static List<Clazz> generateClazzes(List<Course> courses, List<Teacher> teachers) {
        List<Clazz> clazzes = new ArrayList<>();
        int clazzId = 1001;

        // 每门课程至少有两个教学班（由不同教师授课）
        for (Course course : courses) {
            // 为每门课程选择2个不同的教师
            //为当前课程创建一个教师列表副本，并将其随机打乱。这样可以确保为同一门课程选择的教师是随机的。
            List<Teacher> courseTeachers = new ArrayList<>(teachers);
            Collections.shuffle(courseTeachers);

            int teacherCount = Math.min(2, courseTeachers.size());
            for (int i = 0; i < teacherCount; i++) {
                Teacher teacher = courseTeachers.get(i);

                Clazz clazz = new Clazz();
                clazz.setClazzId(clazzId++);
                clazz.setCourseId(course.getCourseId());
                clazz.setCourseName(course.getCourseName());
                clazz.setTeacherId(Integer.parseInt(teacher.getTno().substring(1))); // 提取数字部分
                clazz.setTeacherName(teacher.getName());
                clazz.setStudentNum(20 + random.nextInt(11)); // 20-30人
                clazz.setSemester(SEMESTERS[random.nextInt(SEMESTERS.length)]);
                clazz.setStartTime(LocalDate.now().plusDays(random.nextInt(30)));

                clazzes.add(clazz);
            }
        }

        return clazzes;
    }

    /**
     * 为教学班分配学生
     */
    public static Map<Integer, List<Integer>> assignStudentsToClazzes(List<Clazz> clazzes, List<Student> students) {
        Map<Integer, List<Integer>> clazzStudentsMap = new HashMap<>();
        if (students.isEmpty()) return clazzStudentsMap;

        // 复制学生列表以便打乱顺序
        List<Student> shuffledStudents = new ArrayList<>(students);
        Collections.shuffle(shuffledStudents);

        for (Clazz clazz : clazzes) {
            List<Integer> studentIds = new ArrayList<>();
            int requiredStudents = Math.min(clazz.getStudentNum(), shuffledStudents.size());
            
            // 轮询分配学生，确保均衡
            for (int i = 0; i < requiredStudents; i++) {
                int studentIdx = i % shuffledStudents.size();
                studentIds.add(Integer.valueOf(shuffledStudents.get(studentIdx).getStudentId().substring(1)));
            }
            
            clazzStudentsMap.put(clazz.getClazzId(), studentIds);
        }

        return clazzStudentsMap;
    }

    /**
     * 生成成绩
     */
    public static List<Grade> generateGrades(List<Clazz> clazzes, Map<Integer, List<Integer>> clazzStudentsMap) {
        List<Grade> grades = new ArrayList<>();

        for (Clazz clazz : clazzes) {
            List<Integer> studentIds = clazzStudentsMap.get(clazz.getClazzId());
            if (studentIds == null) continue;

            for (Integer studentId : studentIds) {
                Grade grade = new Grade();
                grade.setStudentId(studentId);
                grade.setClazzId(clazz.getClazzId());

                // 生成各项成绩（0-100的整数）
                int dailyScore = 60 + random.nextInt(41);       // 平时成绩 60-100
                int midtermScore = 50 + random.nextInt(51);     // 期中成绩 50-100
                int labScore = 60 + random.nextInt(41);         // 实验成绩 60-100
                int finalScore = 50 + random.nextInt(51);       // 期末成绩 50-100

                // 计算综合成绩（平时20%，期中20%，实验20%，期末40%）
                int comprehensiveScore = (int) (dailyScore * 0.2 +
                        midtermScore * 0.2 +
                        labScore * 0.2 +
                        finalScore * 0.4);

                grade.setDailyScore(dailyScore);
                grade.setMidtermScore(midtermScore);
                grade.setLabScore(labScore);
                grade.setFinalScore(finalScore);
                grade.setComprehensiveScore(comprehensiveScore);
                grade.setGradeTime(java.time.LocalDateTime.now().minusDays(random.nextInt(30)));

                grades.add(grade);
            }
        }

        return grades;
    }
}
