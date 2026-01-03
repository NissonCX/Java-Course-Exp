package com.cqu.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 使用 MyBatis-Plus 的基础方法即可
    // selectById, selectList, selectByMap 等
}