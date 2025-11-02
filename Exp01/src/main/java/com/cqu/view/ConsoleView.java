package com.cqu.view;

import com.cqu.model.Clazz;
import com.cqu.model.Grade;
import com.cqu.model.Student;
import com.cqu.controller.GradeManagementController;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {
    // ANSI颜色代码
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    
    private GradeManagementController service;
    private Scanner scanner;
    
    public ConsoleView() {
        this.service = new GradeManagementController();
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        System.out.println(BLUE + "==============================================" + RESET);
        System.out.println(BOLD + BLUE + "         欢迎使用学生成绩管理系统" + RESET);
        System.out.println(BLUE + "==============================================" + RESET);
        
        while (true) {
            showMainMenu();
            int choice = getIntInput(CYAN + "请选择操作(0-6): " + RESET);
            
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
        System.out.println(BOLD + BLUE + "\n============ 主菜单 ============" + RESET);
        System.out.println(GREEN + "1. 初始化数据");
        System.out.println("2. 查看教学班成绩");
        System.out.println("3. 查询学生所有科目成绩");
        System.out.println("4. 统计成绩分布");
        System.out.println("5. 学生总成绩排名");
        System.out.println("6. 课程成绩排名");
        System.out.println(RED + "0. 退出系统" + RESET);
        System.out.println(BOLD + BLUE + "==============================" + RESET);
    }
    
    private void showClazzGrades() {
        List<Clazz> clazzes = service.getAllClazzes();
        if (clazzes.isEmpty()) {
            System.out.println(RED + "⚠ 暂无教学班数据，请先初始化数据！" + RESET);
            return;
        }
        
        System.out.println(BOLD + BLUE + "\n╔════════════════════════════════╗");
        System.out.println("║          教学班列表         ║");
        System.out.println("╚════════════════════════════════╝" + RESET);
        for (int i = 0; i < clazzes.size(); i++) {
            Clazz clazz = clazzes.get(i);
            System.out.printf(BOLD + "%2d. " + RESET + "%s (%s) - 教师: " + YELLOW + "%s\n" + RESET,
                            i+1, clazz.getCourseName(), clazz.getSemester(), clazz.getTeacherName());
        }
        
        int clazzChoice = getIntInput("请选择教学班:") - 1;
        if (clazzChoice < 0 || clazzChoice >= clazzes.size()) {
            System.out.println("无效选择！");
            return;
        }
        
        Clazz selectedClazz = clazzes.get(clazzChoice);
        
        System.out.println(BOLD + PURPLE + "\n╔══════════════╗");
        System.out.println("║   排序方式   ║");
        System.out.println("╚══════════════╝" + RESET);
        System.out.println("1. 按学号排序");
        System.out.println("2. 按成绩排序");
        int sortChoice = getIntInput(CYAN + "请选择排序方式(1-2): " + RESET);
        
        List<Grade> grades;
        if (sortChoice == 1) {
            grades = service.getGradesByClazzIdSortedByStudentId(selectedClazz.getClazzId());
        } else {
            grades = service.getGradesByClazzIdSortedByScore(selectedClazz.getClazzId());
        }
        
        System.out.println(BOLD + GREEN + "\n========== " + selectedClazz.getCourseName() + " 成绩单 ==========" + RESET);
        System.out.printf(BOLD + "%-8s %-10s %-6s %-6s %-6s %-6s %-6s\n" + RESET,
                         "学号", "姓名", "平时", "期中", "实验", "期末", "综合");
        System.out.println(BLUE + "------------------------------------------------" + RESET);
        
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
        System.out.println(BOLD + PURPLE + "\n╔════════════════════════════════╗");
        System.out.println("║      查询学生所有科目成绩     ║");
        System.out.println("╚════════════════════════════════╝" + RESET);
        System.out.println("1. 按学号查询");
        System.out.println("2. 按姓名查询");
        int searchType = getIntInput(CYAN + "请选择查询方式(1-2): " + RESET);
        
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
            List<Grade> studentGrades = service.getGradesByStudentId(student.getStudentId());
            if (studentGrades.isEmpty()) {
                System.out.println(RED + "⚠ 该学生暂无成绩记录！" + RESET);
                continue;
            }
            
            System.out.println(BOLD + GREEN + "\n========== " + student.getName() + " 的成绩单 ==========" + RESET);
            System.out.printf(BOLD + "%-15s %-10s %-6s %-6s %-6s %-6s %-6s\n" + RESET, 
                             "课程名", "教师", "平时", "期中", "实验", "期末", "综合");
            System.out.println(BLUE + "------------------------------------------------" + RESET);
            
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
            System.out.println(RED + "⚠ 暂无教学班数据，请先初始化数据！" + RESET);
            return;
        }
        
        System.out.println(BOLD + BLUE + "\n╔════════════════════════════════╗");
        System.out.println("║          教学班列表         ║");
        System.out.println("╚════════════════════════════════╝" + RESET);
        for (int i = 0; i < clazzes.size(); i++) {
            Clazz clazz = clazzes.get(i);
            System.out.printf(BOLD + "%2d. " + RESET + "%s (%s) - 教师: " + YELLOW + "%s\n" + RESET,
                            i+1, clazz.getCourseName(), clazz.getSemester(), clazz.getTeacherName());
        }
        
        int clazzChoice = getIntInput("请选择教学班:") - 1;
        if (clazzChoice < 0 || clazzChoice >= clazzes.size()) {
            System.out.println("无效选择！");
            return;
        }
        
        Clazz selectedClazz = clazzes.get(clazzChoice);
        Map<String, Long> distribution = service.getScoreDistribution(selectedClazz.getClazzId());
        
        System.out.println(BOLD + GREEN + "\n╔════════════════════════════════╗");
        System.out.printf("║ %-30s ║\n", selectedClazz.getCourseName() + " 成绩分布");
        System.out.println("╚════════════════════════════════╝" + RESET);
        distribution.forEach((range, count) -> 
            System.out.printf("%-10s: " + YELLOW + "%d人\n" + RESET, range, count)
        );
    }
    
    private void showOverallRanking() {
        List<Map.Entry<Student, Double>> ranking = service.getOverallRanking();
        if (ranking.isEmpty()) {
            System.out.println(RED + "⚠ 暂无排名数据！" + RESET);
            return;
        }
        
        System.out.println(BOLD + GREEN + "\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                          学生总成绩排名                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣" + RESET);
        System.out.printf(BOLD + "%-8s %-10s %-12s %-8s %-10s\n" + RESET, "排名", "学号", "姓名", "性别", "平均成绩");
        System.out.println(BLUE + "--------------------------------------------------------------------------" + RESET);
        
        for (int i = 0; i < Math.min(ranking.size(), 20); i++) { // 只显示前20名
            Map.Entry<Student, Double> entry = ranking.get(i);
            Student student = entry.getKey();
            Double avgScore = entry.getValue();
            
            System.out.printf("%-8d %-10s %-12s %-8s %-10.2f\n",
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
            System.out.println(RED + "⚠ 暂无课程数据，请先初始化数据！" + RESET);
            return;
        }
        
        // 获取唯一的课程列表
        Map<Integer, String> uniqueCourses = new java.util.LinkedHashMap<>();
        for (Clazz clazz : clazzes) {
            uniqueCourses.put(clazz.getCourseId(), clazz.getCourseName());
        }
        
        System.out.println(BOLD + BLUE + "\n╔════════════════════════════════╗");
        System.out.println("║          课程列表         ║");
        System.out.println("╚════════════════════════════════╝" + RESET);
        int index = 1;
        for (Map.Entry<Integer, String> entry : uniqueCourses.entrySet()) {
            System.out.printf(BOLD + "%2d. " + RESET + "%s\n", index++, entry.getValue());
        }
        
        int courseChoice = getIntInput(CYAN + "请选择课程(1-" + uniqueCourses.size() + "): " + RESET) - 1;
        if (courseChoice < 0 || courseChoice >= uniqueCourses.size()) {
            System.out.println(RED + "⚠ 无效选择！" + RESET);
            return;
        }
        
        Integer selectedCourseId = (Integer) uniqueCourses.keySet().toArray()[courseChoice];
        List<Map.Entry<Student, Integer>> ranking = service.getRankingByCourse(selectedCourseId);
        
        System.out.println(BOLD + GREEN + "\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-66s ║\n", uniqueCourses.get(selectedCourseId) + " 成绩排名");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣" + RESET);
        System.out.printf(BOLD + "%-8s %-10s %-12s %-8s %-8s\n" + RESET, "排名", "学号", "姓名", "性别", "成绩");
        System.out.println(BLUE + "--------------------------------------------------------------------------" + RESET);
        
        for (int i = 0; i < Math.min(ranking.size(), 20); i++) { // 只显示前20名
            Map.Entry<Student, Integer> entry = ranking.get(i);
            Student student = entry.getKey();
            Integer score = entry.getValue();
            
            System.out.printf("%-8d %-10s %-12s %-8s %-8d\n",
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
