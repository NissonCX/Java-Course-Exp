package com.cqu.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Enrollment extends BaseEntity {
    private Long studentId;
    private Long teachingClassId;
    private LocalDateTime enrollTime;
    private Integer status; // 1-正常, 0-退课

    // 关联对象（不对应数据库字段）
    @TableField(exist = false)
    private Student student;

    @TableField(exist = false)
    private TeachingClass teachingClass;
}
