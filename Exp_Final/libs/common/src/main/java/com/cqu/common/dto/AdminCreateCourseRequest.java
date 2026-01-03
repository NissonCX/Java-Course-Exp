package com.cqu.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 超级管理员 - 新增课程请求DTO
 */
@Data
public class AdminCreateCourseRequest {

    @NotBlank(message = "课程编号不能为空")
    private String courseNo;

    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    @NotNull(message = "学分不能为空")
    private BigDecimal credit;

    @NotNull(message = "学时不能为空")
    @Min(value = 1, message = "学时必须大于0")
    private Integer hours;

    private String courseType;

    private String description;
}
