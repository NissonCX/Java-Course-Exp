package com.cqu.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 成绩权重配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScoreWeight extends BaseEntity {
    private Long courseId;
    private BigDecimal usualWeight;
    private BigDecimal midtermWeight;
    private BigDecimal experimentWeight;
    private BigDecimal finalWeight;
}
