package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 根据ID查询用户
     */
    Optional<User> findById(@Param("id") Long id);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);
}
