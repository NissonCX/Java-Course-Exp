package com.cqu.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;
    private String password;
    private UserRole role;
    private String realName;
    private String email;
    private String phone;
    private Integer status; // 1-正常, 0-禁用

    public enum UserRole {
        STUDENT, TEACHER, ADMIN, SUPER_ADMIN
    }
}
