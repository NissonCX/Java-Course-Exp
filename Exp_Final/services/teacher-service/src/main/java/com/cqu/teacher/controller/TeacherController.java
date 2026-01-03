package com.cqu.teacher.controller;

import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.teacher.service.TeacherService;
import com.cqu.common.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 获取教师个人信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Map<String, Object> profile = ((com.cqu.teacher.service.impl.TeacherServiceImpl) teacherService).getProfileWithUserInfo(teacherId);
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新教师个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, String> params,
                                       HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            String email = params.get("email");
            String phone = params.get("phone");
            ((com.cqu.teacher.service.impl.TeacherServiceImpl) teacherService).updateProfileWithUserInfo(teacherId, email, phone);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询教师的所有教学班
     */
    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> getMyClasses(HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            List<Map<String, Object>> classes = teacherService.getMyClasses(teacherId);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询指定教学班详情
     */
    @GetMapping("/class/{classId}")
    public Result<Map<String, Object>> getClassDetail(@PathVariable Long classId,
                                                      HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Map<String, Object> detail = teacherService.getClassDetail(teacherId, classId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班学生列表
     */
    @GetMapping("/class/{classId}/students")
    public Result<List<Map<String, Object>>> getClassStudents(@PathVariable Long classId,
                                                             HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            List<Map<String, Object>> students = teacherService.getClassStudents(teacherId, classId);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新教学班状态
     */
    @PutMapping("/class/{classId}/status")
    public Result<String> updateClassStatus(@PathVariable Long classId,
                                           @RequestBody Map<String, Object> params,
                                           HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Integer status = Integer.valueOf(params.get("status").toString());
            teacherService.updateClassStatus(teacherId, classId, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error("状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 根据教师ID查询基本信息 (用于其他服务调用)
     */
    @GetMapping("/{teacherId}")
    public Result<Teacher> getTeacherById(@PathVariable Long teacherId) {
        try {
            Teacher teacher = teacherService.getById(teacherId);
            return Result.success(teacher);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量查询教师信息 (用于其他服务调用)
     */
    @PostMapping("/batch")
    public Result<List<Teacher>> getTeachersByIds(@RequestBody List<Long> teacherIds) {
        try {
            List<Teacher> teachers = teacherService.getByIds(teacherIds);
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}