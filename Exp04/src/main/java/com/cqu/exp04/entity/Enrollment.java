package com.cqu.exp04.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseEntity {
    private Long studentId;
    private Long teachingClassId;
    private LocalDateTime enrollTime;
    private Integer status; // 1-正常, 0-退课

    // 关联对象
    private Student student;
    private TeachingClass teachingClass;
}
