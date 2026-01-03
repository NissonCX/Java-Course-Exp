package com.cqu.teacher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.TeachingClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学班Mapper接口 (Teacher Service 中的简化版本)
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}