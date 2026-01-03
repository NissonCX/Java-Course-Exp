package com.cqu.student.controller;

import com.cqu.common.entity.Student;
import com.cqu.student.service.StudentService;
import com.cqu.common.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生控制器（Phase 2 简化版）
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Student student = studentService.getById(studentId);

            // 简化版：暂不获取用户信息（需要调用 auth-service）
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("studentNo", student.getStudentNo());
            profile.put("userId", student.getUserId());
            profile.put("name", student.getName());
            profile.put("gender", student.getGender());
            profile.put("birthDate", student.getBirthDate());
            profile.put("major", student.getMajor());
            profile.put("className", student.getClassName());
            profile.put("grade", student.getGrade());
            profile.put("enrollmentYear", student.getEnrollmentYear());
            profile.put("createTime", student.getCreateTime());
            profile.put("updateTime", student.getUpdateTime());
            profile.put("_note", "邮箱和电话需要通过 auth-service 获取");

            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, String> profileData,
                                        HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            String email = profileData.get("email");
            String phone = profileData.get("phone");

            studentService.updateProfile(studentId, email, phone);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询学生所有成绩(带统计信息)
     */
    @GetMapping("/scores")
    public Result<Map<String, Object>> getMyScores(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Map<String, Object> result = studentService.getMyScores(studentId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询我的选课列表
     */
    @GetMapping("/enrollments")
    public Result<?> getMyEnrollments(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            return Result.success(studentService.getMyEnrollments(studentId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 选课
     */
    @PostMapping("/enroll")
    public Result<String> enrollCourse(@RequestBody Map<String, Long> params,
                                       HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Long teachingClassId = params.get("teachingClassId");
            if (teachingClassId == null) {
                return Result.error("选课失败: 缺少参数 teachingClassId");
            }
            studentService.enrollCourse(studentId, teachingClassId);
            return Result.success("选课成功");
        } catch (Exception e) {
            return Result.error("选课失败: " + e.getMessage());
        }
    }

    /**
     * 退课
     */
    @DeleteMapping("/enroll/{enrollmentId}")
    public Result<String> dropCourse(@PathVariable Long enrollmentId,
                                     HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            studentService.dropCourse(studentId, enrollmentId);
            return Result.success("退课成功");
        } catch (Exception e) {
            return Result.error("退课失败: " + e.getMessage());
        }
    }

    /**
     * AI学习建议咨询 - Phase 2 重定向到 ai-service
     */
    @PostMapping("/ai/consult")
    public Result<String> aiConsult(@RequestBody Map<String, String> params,
                                     HttpServletRequest httpRequest) {
        return Result.error("AI 咨询功能请访问 /api/ai/consult");
    }
}
