package com.cqu.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 教学班实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeachingClass extends BaseEntity {
    /**
     * 未开课：允许学生选课
     */
    public static final int STATUS_NOT_STARTED = 1;

    /**
     * 已开课：不允许学生选课
     */
    public static final int STATUS_IN_PROGRESS = 2;

    /**
     * 已结课：不允许学生选课
     */
    public static final int STATUS_FINISHED = 0;

    private String classNo;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private Integer maxStudents;
    private Integer currentStudents;
    private String classroom;
    private String schedule;

    /**
     * 教学班状态：
     * - 1：未开课（可选课）
     * - 2：已开课（不可选课）
     * - 0：已结课（不可选课）
     *
     * 兼容历史数据：若为 null，按"未开课"处理。
     */
    private Integer status;

    public boolean isSelectableForEnrollment() {
        return status == null || status == STATUS_NOT_STARTED;
    }

    public String getStatusText() {
        int s = status == null ? STATUS_NOT_STARTED : status;
        return switch (s) {
            case STATUS_NOT_STARTED -> "未开课";
            case STATUS_IN_PROGRESS -> "已开课";
            case STATUS_FINISHED -> "已结课";
            default -> "未知";
        };
    }

    // 关联对象（不对应数据库字段）
    @TableField(exist = false)
    private Course course;

    @TableField(exist = false)
    private Teacher teacher;
}
