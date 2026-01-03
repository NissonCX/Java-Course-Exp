package com.cqu.exp04.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 教师实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseEntity {
    private String teacherNo;
    private Long userId;
    private String name;
    private Gender gender;
    private String title;
    private String department;
    private String email;
    private String phone;

    public enum Gender {
        MALE, FEMALE
    }
}
