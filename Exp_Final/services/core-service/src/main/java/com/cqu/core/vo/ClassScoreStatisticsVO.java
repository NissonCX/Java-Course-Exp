package com.cqu.core.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 教学班成绩统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassScoreStatisticsVO {
    private Long teachingClassId;
    private String classNo;
    private String courseName;
    private String semester;
    private Integer totalStudents;
    private Integer scoredStudents;
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;
    private Integer excellentCount; // 90-100
    private Integer goodCount; // 80-89
    private Integer mediumCount; // 70-79
    private Integer passCount; // 60-69
    private Integer failCount; // <60
    private Double passRate;
    private Double excellentRate;
}
