package com.cqu.exp04.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学生实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {
    private String studentNo;
    private Long userId;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String major;
    private String className;
    private Integer grade;
    private Integer enrollmentYear;

    public enum Gender {
        MALE, FEMALE
    }
}
