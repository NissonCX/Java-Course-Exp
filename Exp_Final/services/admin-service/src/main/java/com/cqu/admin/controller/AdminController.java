package com.cqu.admin.controller;

import com.cqu.common.dto.AdminCreateCourseRequest;
import com.cqu.common.dto.AdminCreateTeachingClassRequest;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.entity.Course;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.admin.service.AdminService;
import com.cqu.common.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 超级管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 新增学生（创建用户 + 学生档案）
     */
    @PostMapping("/students")
    public Result<Student> createStudent(@Valid @RequestBody StudentRegisterRequest request) {
        try {
            Student student = adminService.createStudent(request);
            return Result.success("创建学生成功", student);
        } catch (Exception e) {
            return Result.error("创建学生失败: " + e.getMessage());
        }
    }

    /**
     * 新增教师（创建用户 + 教师档案）
     */
    @PostMapping("/teachers")
    public Result<Teacher> createTeacher(@Valid @RequestBody TeacherRegisterRequest request) {
        try {
            Teacher teacher = adminService.createTeacher(request);
            return Result.success("创建教师成功", teacher);
        } catch (Exception e) {
            return Result.error("创建教师失败: " + e.getMessage());
        }
    }

    /**
     * 新增课程
     */
    @PostMapping("/courses")
    public Result<Course> createCourse(@Valid @RequestBody AdminCreateCourseRequest request) {
        try {
            Course course = adminService.createCourse(request);
            return Result.success("创建课程成功", course);
        } catch (Exception e) {
            return Result.error("创建课程失败: " + e.getMessage());
        }
    }

    /**
     * 新增教学班（为课程分配授课教师）
     */
    @PostMapping("/teaching-classes")
    public Result<TeachingClass> createTeachingClass(@Valid @RequestBody AdminCreateTeachingClassRequest request) {
        try {
            TeachingClass teachingClass = adminService.createTeachingClass(request);
            return Result.success("创建教学班成功", teachingClass);
        } catch (Exception e) {
            return Result.error("创建教学班失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有课程
     */
    @GetMapping("/courses")
    public Result<List<Course>> listCourses() {
        return Result.success(adminService.listCourses());
    }

    /**
     * 查询所有教师
     */
    @GetMapping("/teachers")
    public Result<List<Teacher>> listTeachers() {
        return Result.success(adminService.listTeachers());
    }

    /**
     * 查询所有学生
     */
    @GetMapping("/students")
    public Result<List<Student>> listStudents() {
        return Result.success(adminService.listStudents());
    }

    /**
     * 查询所有教学班
     */
    @GetMapping("/teaching-classes")
    public Result<List<Map<String, Object>>> listTeachingClasses() {
        return Result.success(adminService.listTeachingClasses());
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/students/{studentId}")
    public Result<String> deleteStudent(@PathVariable Long studentId) {
        try {
            adminService.deleteStudent(studentId);
            return Result.success("删除学生成功");
        } catch (Exception e) {
            return Result.error("删除学生失败: " + e.getMessage());
        }
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/teachers/{teacherId}")
    public Result<String> deleteTeacher(@PathVariable Long teacherId) {
        try {
            adminService.deleteTeacher(teacherId);
            return Result.success("删除教师成功");
        } catch (Exception e) {
            return Result.error("删除教师失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/courses/{courseId}")
    public Result<String> deleteCourse(@PathVariable Long courseId) {
        try {
            adminService.deleteCourse(courseId);
            return Result.success("删除课程成功");
        } catch (Exception e) {
            return Result.error("删除课程失败: " + e.getMessage());
        }
    }
}