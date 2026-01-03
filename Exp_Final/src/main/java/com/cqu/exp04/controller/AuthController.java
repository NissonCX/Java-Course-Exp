package com.cqu.exp04.controller;

import com.cqu.exp04.dto.LoginRequest;
import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.service.AuthService;
import com.cqu.exp04.service.StudentService;
import com.cqu.exp04.service.TeacherService;
import com.cqu.exp04.vo.LoginResponse;
import com.cqu.exp04.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("API is working!");
    }
}

