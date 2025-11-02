package com.cqu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clazz {
    private Integer clazzId;      // 教学班号
    private Integer courseId;     // 课程ID
    private String courseName;    // 课程名字
    private Integer teacherId;    // 教师ID
    private String teacherName;   // 教师姓名
    private Integer studentNum;   // 总人数
    private String semester;      // 开课学期
    private LocalDate startTime;  // 开始时间
}
