package com.cqu.exp04.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.exp04.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    default Optional<User> findByUsername(@Param("username") String username) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", username)
        ));
    }

    /**
     * 根据ID查询用户
     */
    default Optional<User> findById(@Param("id") Long id) {
        return Optional.ofNullable(selectById(id));
    }

}

