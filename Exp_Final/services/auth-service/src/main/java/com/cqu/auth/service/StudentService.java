package com.cqu.auth.service;

import com.cqu.common.dto.StudentRegisterRequest;
import com.cqu.common.vo.LoginResponse;

/**
 * 学生服务接口（auth-service 简化版本，只包含注册功能）
 */
public interface StudentService {
    /**
     * 学生注册
     */
    LoginResponse register(StudentRegisterRequest request);
}
