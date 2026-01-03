package com.cqu.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI咨询请求DTO
 */
@Data
public class AIConsultRequest {
    @NotBlank(message = "消息内容不能为空")
    private String message;

    private Long teachingClassId; // 教师查询时使用
}
