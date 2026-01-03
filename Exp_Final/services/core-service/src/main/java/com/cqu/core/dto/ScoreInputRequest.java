package com.cqu.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 成绩录入请求DTO
 */
@Data
public class ScoreInputRequest {
    @NotNull(message = "教学班ID不能为空")
    private Long teachingClassId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    private BigDecimal usualScore;
    private BigDecimal midtermScore;
    private BigDecimal experimentScore;
    private BigDecimal finalScore;
}
