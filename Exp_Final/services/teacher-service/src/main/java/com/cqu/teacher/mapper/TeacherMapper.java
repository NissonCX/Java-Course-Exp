package com.cqu.teacher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}