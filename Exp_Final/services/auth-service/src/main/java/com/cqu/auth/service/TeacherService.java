package com.cqu.auth.service;

import com.cqu.common.dto.TeacherRegisterRequest;
import com.cqu.common.vo.LoginResponse;

/**
 * 教师服务接口（auth-service 简化版本，只包含注册功能）
 */
public interface TeacherService {
    /**
     * 教师注册
     */
    LoginResponse register(TeacherRegisterRequest request);
}
