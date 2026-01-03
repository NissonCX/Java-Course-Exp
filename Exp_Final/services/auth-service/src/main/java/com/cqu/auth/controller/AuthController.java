package com.cqu.auth.controller;

import com.cqu.common.dto.LoginRequest;
import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.auth.service.AuthService;
import com.cqu.auth.service.StudentService;
import com.cqu.auth.service.TeacherService;
import com.cqu.common.vo.LoginResponse;
import com.cqu.common.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 学生注册
     */
    @PostMapping("/register/student")
    public Result<LoginResponse> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        try {
            LoginResponse response = studentService.register(request);
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 教师注册
     */
    @PostMapping("/register/teacher")
    public Result<LoginResponse> registerTeacher(@Valid @RequestBody TeacherRegisterRequest request) {
        try {
            LoginResponse response = teacherService.register(request);
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户信息（供其他服务调用）
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserById(@PathVariable Long userId) {
        try {
            Map<String, Object> userInfo = authService.getUserById(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息（邮箱、电话）
     */
    @PutMapping("/user/{userId}")
    public Result<String> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String phone = params.get("phone");
            authService.updateUserInfo(userId, email, phone);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("API is working!");
    }
}

