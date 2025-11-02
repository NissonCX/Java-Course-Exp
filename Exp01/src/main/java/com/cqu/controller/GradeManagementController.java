package com.cqu.controller;

import com.cqu.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class GradeManagementController {
    private List<Student> students;
    private List<Teacher> teachers;
    private List<Course> courses;
    private List<Clazz> clazzes;
    private List<Grade> grades;
    private Map<Integer, List<Integer>> clazzStudentsMap;
    
    public GradeManagementController() {
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.clazzes = new ArrayList<>();
        this.grades = new ArrayList<>();
        this.clazzStudentsMap = new HashMap<>();
    }
    
    /**
     * 初始化数据
     */
    public void initializeData() {
        System.out.println("正在生成初始化数据...");
        
        // 生成学生（不少于100个）
        students = DataGenerator.generateStudents(100);
        System.out.println("已生成 " + students.size() + " 名学生");
        
        // 生成教师（不少于6个）
        teachers = DataGenerator.generateTeachers(8);
        System.out.println("已生成 " + teachers.size() + " 名教师");
        
        // 生成课程（不少于3门）
        courses = DataGenerator.generateCourses(5);
        System.out.println("已生成 " + courses.size() + " 门课程");
        
        // 生成教学班
        clazzes = DataGenerator.generateClazzes(courses, teachers);
        System.out.println("已生成 " + clazzes.size() + " 个教学班");
        
        // 为教学班分配学生
        clazzStudentsMap = DataGenerator.assignStudentsToClazzes(clazzes, students);
        System.out.println("已完成学生选课分配");
        
        // 生成成绩
        grades = DataGenerator.generateGrades(clazzes, clazzStudentsMap);
        System.out.println("已生成 " + grades.size() + " 条成绩记录");
        
        System.out.println("数据初始化完成！");
    }
    
    /**
     * 根据教学班ID获取该班所有学生成绩
     */
    public List<Grade> getGradesByClazzId(int clazzId) {
        return grades.stream()
                .filter(grade -> grade.getClazzId() == clazzId)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据教学班ID获取该班学生信息
     */
    public List<Student> getStudentsByClazzId(int clazzId) {
        List<Integer> studentIds = clazzStudentsMap.get(clazzId);
        if (studentIds == null) return new ArrayList<>();
        
        return students.stream()
                .filter(student -> studentIds.contains(Integer.valueOf(student.getStudentId().substring(1))))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据学号获取学生所有成绩
     */
    public List<Grade> getGradesByStudentId(String studentIdStr) {
        Integer studentId = Integer.valueOf(studentIdStr.substring(1));
        return grades.stream()
                .filter(grade -> grade.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取学生信息
     */
    public Student getStudentById(String studentIdStr) {
        Integer studentId = Integer.valueOf(studentIdStr.substring(1));
        return students.stream()
                .filter(student -> Integer.valueOf(student.getStudentId().substring(1)).equals(studentId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 根据学生姓名获取学生信息
     */
    public List<Student> getStudentsByName(String name) {
        return students.stream()
                .filter(student -> student.getName().contains(name))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有教学班
     */
    public List<Clazz> getAllClazzes() {
        return clazzes;
    }
    
    /**
     * 按学号排序显示教学班成绩
     */
    public List<Grade> getGradesByClazzIdSortedByStudentId(int clazzId) {
        return getGradesByClazzId(clazzId).stream()
                .sorted(Comparator.comparing(Grade::getStudentId))
                .collect(Collectors.toList());
    }
    
    /**
     * 按成绩排序显示教学班成绩
     */
    public List<Grade> getGradesByClazzIdSortedByScore(int clazzId) {
        return getGradesByClazzId(clazzId).stream()
                .sorted(Comparator.comparing(Grade::getComprehensiveScore).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * 统计成绩分布
     */
    public Map<String, Long> getScoreDistribution(int clazzId) {
        List<Grade> clazzGrades = getGradesByClazzId(clazzId);
        
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("90-100分", clazzGrades.stream().filter(g -> g.getComprehensiveScore() >= 90).count());
        distribution.put("80-89分", clazzGrades.stream().filter(g -> g.getComprehensiveScore() >= 80 && g.getComprehensiveScore() < 90).count());
        distribution.put("70-79分", clazzGrades.stream().filter(g -> g.getComprehensiveScore() >= 70 && g.getComprehensiveScore() < 80).count());
        distribution.put("60-69分", clazzGrades.stream().filter(g -> g.getComprehensiveScore() >= 60 && g.getComprehensiveScore() < 70).count());
        distribution.put("60分以下", clazzGrades.stream().filter(g -> g.getComprehensiveScore() < 60).count());
        
        return distribution;
    }
    
    /**
     * 获取所有学生的总成绩排名
     */
    public List<Map.Entry<Student, Double>> getOverallRanking() {
        // 计算每个学生的平均成绩
        Map<Student, Double> averageScores = new HashMap<>();
        
        for (Student student : students) {
            Integer studentId = Integer.valueOf(student.getStudentId().substring(1));
            List<Grade> studentGrades = grades.stream()
                    .filter(g -> g.getStudentId().equals(studentId))
                    .collect(Collectors.toList());
            
            if (!studentGrades.isEmpty()) {
                double avgScore = studentGrades.stream()
                        .mapToInt(Grade::getComprehensiveScore)
                        .average()
                        .orElse(0.0);
                averageScores.put(student, avgScore);
            } else {
                averageScores.put(student, 0.0);
            }
        }
        
        // 按平均成绩排序
        return averageScores.entrySet().stream()
                .sorted(Map.Entry.<Student, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * 根据课程ID获取所有教学班的成绩排名
     */
    public List<Map.Entry<Student, Integer>> getRankingByCourse(int courseId) {
        // 找到该课程的所有教学班
        List<Clazz> courseClazzes = clazzes.stream()
                .filter(c -> c.getCourseId() == courseId)
                .collect(Collectors.toList());
        
        // 收集相关成绩
        Map<Student, Integer> courseScores = new HashMap<>();
        
        for (Clazz clazz : courseClazzes) {
            List<Grade> clazzGrades = getGradesByClazzId(clazz.getClazzId());
            
            for (Grade grade : clazzGrades) {
                // 查找对应的学生
                String studentIdStr = "S" + String.format("%04d", grade.getStudentId());
                Student student = getStudentById(studentIdStr);
                
                if (student != null) {
                    courseScores.put(student, grade.getComprehensiveScore());
                }
            }
        }
        
        // 按成绩排序
        return courseScores.entrySet().stream()
                .sorted(Map.Entry.<Student, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }
}
