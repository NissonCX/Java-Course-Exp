package com.cqu.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教学班状态更新请求
 */
@Data
public class TeachingClassStatusUpdateRequest {

    /**
     * 教学班状态：
     * - 1：未开课（可选课）
     * - 2：已开课（不可选课）
     * - 0：已结课（不可选课）
     */
    @NotNull(message = "status不能为空")
    @Min(value = 0, message = "status不合法")
    @Max(value = 2, message = "status不合法")
    private Integer status;
}
