package com.cqu.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学生实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
