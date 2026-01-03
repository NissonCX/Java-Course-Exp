package com.cqu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}