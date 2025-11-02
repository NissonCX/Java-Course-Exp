package com.cqu.view;

import com.cqu.model.Clazz;
import com.cqu.model.Grade;
import com.cqu.model.Student;
import com.cqu.controller.GradeManagementController;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {
    private GradeManagementController service;
    private Scanner scanner;
    
    public ConsoleView() {
        this.service = new GradeManagementController();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println("=====================================");
        System.out.println("    欢迎使用学生成绩管理系统😊");
        System.out.println("=====================================");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("请选择操作:");
            
            switch (choice) {
                case 1:
                    service.initializeData();
                    break;
                case 2:
                    showClazzGrades();
                    break;
                case 3:
                    showStudentGrades();
                    break;
                case 4:
                    showScoreDistribution();
                    break;
                case 5:
                    showOverallRanking();
                    break;
                case 6:
                    showCourseRanking();
                    break;
                case 0:
                    System.out.println("感谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
            
            System.out.println("\n按回车键继续...");
            scanner.nextLine();
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n========== 主菜单 ==========");
        System.out.println("1. 初始化数据");
        System.out.println("2. 查看教学班成绩");
        System.out.println("3. 查询学生所有科目成绩");
        System.out.println("4. 统计成绩分布");
        System.out.println("5. 学生总成绩排名");
        System.out.println("6. 课程成绩排名");
        System.out.println("0. 退出系统");
        System.out.println("============================");
    }
    
    private void showClazzGrades() {
        List<Clazz> clazzes = service.getAllClazzes();
        if (clazzes.isEmpty()) {
            System.out.println("暂无教学班数据，请先初始化数据！");
            return;
        }
        
        System.out.println("\n========== 教学班列表 ==========");
        for (int i = 0; i < clazzes.size(); i++) {
            Clazz clazz = clazzes.get(i);
            System.out.printf("%d. %s (%s) - 教师: %s\n", 
                            i+1, clazz.getCourseName(), clazz.getSemester(), clazz.getTeacherName());
        }
        
        int clazzChoice = getIntInput("请选择教学班:") - 1;
        if (clazzChoice < 0 || clazzChoice >= clazzes.size()) {
            System.out.println("无效选择！");
            return;
        }
        
        Clazz selectedClazz = clazzes.get(clazzChoice);
        
        System.out.println("\n========== 排序方式 ==========");
        System.out.println("1. 按学号排序");
        System.out.println("2. 按成绩排序");
        int sortChoice = getIntInput("请选择排序方式:");
        
        List<Grade> grades;
        if (sortChoice == 1) {
            grades = service.getGradesByClazzIdSortedByStudentId(selectedClazz.getClazzId());
        } else {
            grades = service.getGradesByClazzIdSortedByScore(selectedClazz.getClazzId());
        }
        
        System.out.println("\n========== " + selectedClazz.getCourseName() + " 成绩单 ==========");
        System.out.printf("%-8s %-10s %-6s %-6s %-6s %-6s %-6s\n", 
                         "学号", "姓名", "平时", "期中", "实验", "期末", "综合");
        System.out.println("------------------------------------------------------------");
        
        for (Grade grade : grades) {
            String studentId = "S" + String.format("%04d", grade.getStudentId());
            Student student = service.getStudentById(studentId);
            String studentName = student != null ? student.getName() : "未知";
            
            System.out.printf("%-8s %-10s %-6d %-6d %-6d %-6d %-6d\n",
                            studentId,
                            studentName,
                            grade.getDailyScore(),
                            grade.getMidtermScore(),
                            grade.getLabScore(),
                            grade.getFinalScore(),
                            grade.getComprehensiveScore());
        }
    }
    
    private void showStudentGrades() {
        System.out.println("\n========== 查询学生所有科目成绩 ==========");
        System.out.println("1. 按学号查询");
        System.out.println("2. 按姓名查询");
        int searchType = getIntInput("请选择查询方式:");
        
        List<Student> targetStudents = null;
        if (searchType == 1) {
            String studentId = getStringInput("请输入学号:");
            Student student = service.getStudentById(studentId);
            if (student != null) {
                targetStudents = List.of(student);
            }
        } else {
            String name = getStringInput("请输入姓名:");
            targetStudents = service.getStudentsByName(name);
        }
        
        if (targetStudents == null || targetStudents.isEmpty()) {
            System.out.println("未找到符合条件的学生！");
            return;
        }
        
        for (Student student : targetStudents) {
            System.out.println("\n========== " + student.getName() + " 的成绩单 ==========");
            List<Grade> studentGrades = service.getGradesByStudentId(student.getStudentId());
            
            System.out.printf("%-15s %-10s %-6s %-6s %-6s %-6s %-6s\n", 
                             "课程名", "教师", "平时", "期中", "实验", "期末", "综合");
            System.out.println("------------------------------------------------------------------");
            
            int totalScore = 0;
            for (Grade grade : studentGrades) {
                // 查找对应的班级信息
                Clazz clazz = service.getAllClazzes().stream()
                        .filter(c -> c.getClazzId().equals(grade.getClazzId()))
                        .findFirst()
                        .orElse(null);
                
                String courseName = clazz != null ? clazz.getCourseName() : "未知课程";
                String teacherName = clazz != null ? clazz.getTeacherName() : "未知教师";
                
                System.out.printf("%-15s %-10s %-6d %-6d %-6d %-6d %-6d\n",
                                courseName,
                                teacherName,
                                grade.getDailyScore(),
                                grade.getMidtermScore(),
                                grade.getLabScore(),
                                grade.getFinalScore(),
                                grade.getComprehensiveScore());
                
                totalScore += grade.getComprehensiveScore();
            }
            
            System.out.printf("\n总成绩: %d\t平均成绩: %.2f\n", 
                            totalScore, 
                            studentGrades.isEmpty() ? 0 : (double) totalScore / studentGrades.size());
        }
    }
    
    private void showScoreDistribution() {
        List<Clazz> clazzes = service.getAllClazzes();
        if (clazzes.isEmpty()) {
            System.out.println("暂无教学班数据，请先初始化数据！");
            return;
        }
        
        System.out.println("\n========== 教学班列表 ==========");
        for (int i = 0; i < clazzes.size(); i++) {
            Clazz clazz = clazzes.get(i);
            System.out.printf("%d. %s (%s) - 教师: %s\n", 
                            i+1, clazz.getCourseName(), clazz.getSemester(), clazz.getTeacherName());
        }
        
        int clazzChoice = getIntInput("请选择教学班:") - 1;
        if (clazzChoice < 0 || clazzChoice >= clazzes.size()) {
            System.out.println("无效选择！");
            return;
        }
        
        Clazz selectedClazz = clazzes.get(clazzChoice);
        Map<String, Long> distribution = service.getScoreDistribution(selectedClazz.getClazzId());
        
        System.out.println("\n========== " + selectedClazz.getCourseName() + " 成绩分布 ==========");
        distribution.forEach((range, count) -> 
            System.out.printf("%s: %d人\n", range, count)
        );
    }
    
    private void showOverallRanking() {
        List<Map.Entry<Student, Double>> ranking = service.getOverallRanking();
        if (ranking.isEmpty()) {
            System.out.println("暂无排名数据！");
            return;
        }
        
        System.out.println("\n========== 学生总成绩排名 ==========");
        System.out.printf("%-6s %-8s %-10s %-6s %-8s\n", "排名", "学号", "姓名", "性别", "平均成绩");
        System.out.println("----------------------------------------------");
        
        for (int i = 0; i < Math.min(ranking.size(), 20); i++) { // 只显示前20名
            Map.Entry<Student, Double> entry = ranking.get(i);
            Student student = entry.getKey();
            Double avgScore = entry.getValue();
            
            System.out.printf("%-6d %-8s %-10s %-6s %-8.2f\n",
                            i+1,
                            student.getStudentId(),
                            student.getName(),
                            student.getGender(),
                            avgScore);
        }
    }
    
    private void showCourseRanking() {
        List<Clazz> clazzes = service.getAllClazzes();
        if (clazzes.isEmpty()) {
            System.out.println("暂无课程数据，请先初始化数据！");
            return;
        }
        
        // 获取唯一的课程列表
        Map<Integer, String> uniqueCourses = new java.util.LinkedHashMap<>();
        for (Clazz clazz : clazzes) {
            uniqueCourses.put(clazz.getCourseId(), clazz.getCourseName());
        }
        
        System.out.println("\n========== 课程列表 ==========");
        int index = 1;
        for (Map.Entry<Integer, String> entry : uniqueCourses.entrySet()) {
            System.out.printf("%d. %s\n", index++, entry.getValue());
        }
        
        int courseChoice = getIntInput("请选择课程:") - 1;
        if (courseChoice < 0 || courseChoice >= uniqueCourses.size()) {
            System.out.println("无效选择！");
            return;
        }
        
        Integer selectedCourseId = (Integer) uniqueCourses.keySet().toArray()[courseChoice];
        List<Map.Entry<Student, Integer>> ranking = service.getRankingByCourse(selectedCourseId);
        
        System.out.println("\n========== " + uniqueCourses.get(selectedCourseId) + " 成绩排名 ==========");
        System.out.printf("%-6s %-8s %-10s %-6s %-6s\n", "排名", "学号", "姓名", "性别", "成绩");
        System.out.println("------------------------------------------------");
        
        for (int i = 0; i < Math.min(ranking.size(), 20); i++) { // 只显示前20名
            Map.Entry<Student, Integer> entry = ranking.get(i);
            Student student = entry.getKey();
            Integer score = entry.getValue();
            
            System.out.printf("%-6d %-8s %-10s %-6s %-6d\n",
                            i+1,
                            student.getStudentId(),
                            student.getName(),
                            student.getGender(),
                            score);
        }
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
