package com.cqu.exp04.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教学班实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingClass extends BaseEntity {
    private String classNo;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private Integer maxStudents;
    private Integer currentStudents;
    private String classroom;
    private String schedule;
    private Integer status; // 1-开课中, 0-已结课

    // 关联对象
    private Course course;
    private Teacher teacher;
}
