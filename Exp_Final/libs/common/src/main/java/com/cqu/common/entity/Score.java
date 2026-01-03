package com.cqu.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Score extends BaseEntity {
    private Long enrollmentId;
    private Long studentId;
    private Long teachingClassId;
    private BigDecimal usualScore;
    private BigDecimal midtermScore;
    private BigDecimal experimentScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;
    private BigDecimal gradePoint;
    private LocalDateTime usualScoreTime;
    private LocalDateTime midtermScoreTime;
    private LocalDateTime experimentScoreTime;
    private LocalDateTime finalScoreTime;
    private LocalDateTime totalScoreTime;

    // 关联对象（不对应数据库字段）
    @TableField(exist = false)
    private Student student;

    @TableField(exist = false)
    private TeachingClass teachingClass;
}
