package com.cqu.core.controller;

import com.cqu.common.entity.User;
import com.cqu.core.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 密码测试控制器 - 用于诊断密码匹配问题
 */
@RestController
@RequestMapping("/api/auth")
public class PasswordTestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试密码加密和匹配
     */
    @GetMapping("/test-password")
    public Map<String, Object> testPassword(
            @RequestParam(defaultValue = "123456") String rawPassword,
            @RequestParam(required = false) String username) {

        Map<String, Object> result = new HashMap<>();

        // 生成新的加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        result.put("rawPassword", rawPassword);
        result.put("newEncodedPassword", encodedPassword);

        // 如果提供了用户名，查询数据库中的密码并验证
        if (username != null && !username.isEmpty()) {
            Optional<User> userOpt = userMapper.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String dbPassword = user.getPassword();
                boolean matches = passwordEncoder.matches(rawPassword, dbPassword);

                result.put("username", username);
                result.put("dbPassword", dbPassword);
                result.put("passwordMatches", matches);
                result.put("dbPasswordLength", dbPassword != null ? dbPassword.length() : 0);
            } else {
                result.put("error", "用户不存在: " + username);
            }
        }

        // 测试 data.sql 中的密码
        String dataPassword = "$2a$10$rWvZ9jQX8pQkqhJJ5qYxZekGP5kGZvXxPzxT7qO5mO.HqPj8Y5Gau";
        boolean matchesDataPassword = passwordEncoder.matches(rawPassword, dataPassword);
        result.put("dataSqlPassword", dataPassword);
        result.put("matchesDataSqlPassword", matchesDataPassword);

        return result;
    }

    /**
     * 生成 BCrypt 加密密码
     */
    @GetMapping("/encode-password")
    public Map<String, String> encodePassword(@RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        result.put("rawPassword", password);
        result.put("encodedPassword", passwordEncoder.encode(password));
        return result;
    }
}
