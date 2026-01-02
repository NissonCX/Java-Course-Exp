package com.cqu.exp04.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 超级管理员 - 新增教学班 / 分配教师请求DTO
 */
@Data
public class AdminCreateTeachingClassRequest {

    @NotBlank(message = "教学班编号不能为空")
    private String classNo;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @NotBlank(message = "学期不能为空")
    private String semester;

    @NotNull(message = "容量不能为空")
    @Min(value = 1, message = "容量必须大于0")
    private Integer maxStudents;

    private String classroom;

    private String schedule;

    /**
     * 1-未开课(默认), 2-已开课, 0-已结课
     */
    private Integer status;
}
