package com.cqu.core.config;

import com.cqu.common.entity.User;
import com.cqu.core.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 可选：启动时自动创建超级管理员账号（仅当配置了 app.super-admin.username/password）
 */
@Component
public class SuperAdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminBootstrap.class);

    @Value("${app.super-admin.username:}")
    private String username;

    @Value("${app.super-admin.password:}")
    private String password;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return;
        }

        if (userMapper.findByUsername(username).isPresent()) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(User.UserRole.SUPER_ADMIN)
                .realName("超级管理员")
                .status(1)
                .build();

        try {
            userMapper.insert(user);
            log.info("Created SUPER_ADMIN user: {}", username);
        } catch (DataAccessException e) {
            // Common cause: column `role` is ENUM('STUDENT','TEACHER','ADMIN') or too short for 'SUPER_ADMIN'
            log.error("Failed to create SUPER_ADMIN user ({}). Your DB schema likely doesn't accept role 'SUPER_ADMIN'. ", username, e);
            log.error("Please migrate MySQL column user.role, e.g.: ALTER TABLE user MODIFY COLUMN role VARCHAR(32) NOT NULL; (or extend ENUM to include SUPER_ADMIN)");
            // Do not block application startup.
        }
    }
}
