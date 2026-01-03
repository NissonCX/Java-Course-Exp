package com.cqu.exp04.service;

import com.cqu.exp04.dto.LoginRequest;
import com.cqu.exp04.vo.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
