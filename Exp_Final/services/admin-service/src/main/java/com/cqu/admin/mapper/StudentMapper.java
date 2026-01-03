package com.cqu.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口 (Admin Service 中的版本)
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}