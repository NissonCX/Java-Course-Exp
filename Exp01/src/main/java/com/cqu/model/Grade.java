package com.cqu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    private Integer studentId;           // 学生ID
    private Integer clazzId;             // 教学班ID
    private Integer dailyScore;          // 平时成绩
    private Integer midtermScore;        // 期中成绩
    private Integer labScore;            // 实验成绩
    private Integer finalScore;          // 期末成绩
    private Integer comprehensiveScore;  // 综合成绩
    private LocalDateTime gradeTime;     // 成绩记录时间
}
