package com.cqu.student.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Auth Service Feign Client
 * 调用认证服务获取用户信息
 */
@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/api/auth/user/{userId}")
    Result<Map<String, Object>> getUserById(@PathVariable("userId") Long userId);

    /**
     * 更新用户信息
     */
    @PutMapping("/api/auth/user/{userId}")
    Result<String> updateUser(@PathVariable("userId") Long userId, @RequestBody Map<String, String> params);
}
