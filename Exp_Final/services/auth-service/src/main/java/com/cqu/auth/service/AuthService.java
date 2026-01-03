package com.cqu.auth.service;

import com.cqu.common.dto.LoginRequest;
import com.cqu.common.vo.LoginResponse;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户ID获取用户信息
     */
    Map<String, Object> getUserById(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, String email, String phone);
}
