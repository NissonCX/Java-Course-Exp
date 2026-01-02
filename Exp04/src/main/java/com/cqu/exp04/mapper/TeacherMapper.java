package com.cqu.exp04.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.exp04.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据教工号查询教师
     */
    default Optional<Teacher> findByTeacherNo(@Param("teacherNo") String teacherNo) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Teacher>()
                .eq("teacher_no", teacherNo)
        ));
    }

    /**
     * 根据用户ID查询教师
     */
    default Optional<Teacher> findByUserId(@Param("userId") Long userId) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Teacher>()
                .eq("user_id", userId)
        ));
    }

    /**
     * 根据ID查询教师
     */
    default Optional<Teacher> findById(@Param("id") Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 查询所有教师
     */
    default List<Teacher> findAll() {
        return selectList(null);
    }

    /**
     * 批量插入教师
     */
    int batchInsert(@Param("teachers") List<Teacher> teachers);
}
