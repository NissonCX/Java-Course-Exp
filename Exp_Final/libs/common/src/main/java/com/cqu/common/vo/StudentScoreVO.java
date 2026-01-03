package com.cqu.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生成绩详情VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScoreVO {
    private Long scoreId;
    private String courseNo;
    private String courseName;
    private BigDecimal credit;
    private String teacherName;
    private String classNo;
    private String semester;
    private BigDecimal usualScore;
    private BigDecimal midtermScore;
    private BigDecimal experimentScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;
    private BigDecimal gradePoint;
    private LocalDateTime totalScoreTime;
}
