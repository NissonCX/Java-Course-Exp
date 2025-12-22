package com.cqu.exp04.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
