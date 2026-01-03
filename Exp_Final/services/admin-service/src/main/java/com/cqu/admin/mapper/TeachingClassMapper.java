package com.cqu.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.TeachingClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学班Mapper接口 (Admin Service 中的版本)
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}