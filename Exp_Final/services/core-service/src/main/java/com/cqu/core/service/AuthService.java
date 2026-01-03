package com.cqu.core.service;

import com.cqu.common.dto.LoginRequest;
import com.cqu.common.vo.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
}
