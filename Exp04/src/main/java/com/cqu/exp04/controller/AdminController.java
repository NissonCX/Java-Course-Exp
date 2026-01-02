package com.cqu.exp04.controller;

import com.cqu.exp04.dto.AdminCreateCourseRequest;
import com.cqu.exp04.dto.AdminCreateTeachingClassRequest;
import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.entity.Course;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.service.AdminService;
import com.cqu.exp04.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 超级管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('SUPER_ADMIN')")
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

    @GetMapping("/courses")
    public Result<List<Course>> listCourses() {
        return Result.success(adminService.listCourses());
    }

    @GetMapping("/teachers")
    public Result<List<Teacher>> listTeachers() {
        return Result.success(adminService.listTeachers());
    }
}
