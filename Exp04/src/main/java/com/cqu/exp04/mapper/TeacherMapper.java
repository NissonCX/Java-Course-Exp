package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper {

    /**
     * 根据教工号查询教师
     */
    Optional<Teacher> findByTeacherNo(@Param("teacherNo") String teacherNo);

    /**
     * 根据用户ID查询教师
     */
    Optional<Teacher> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID查询教师
     */
    Optional<Teacher> findById(@Param("id") Long id);

    /**
     * 查询所有教师
     */
    List<Teacher> findAll();

    /**
     * 插入教师
     */
    int insert(Teacher teacher);

    /**
     * 批量插入教师
     */
    int batchInsert(@Param("teachers") List<Teacher> teachers);

    /**
     * 更新教师
     */
    int update(Teacher teacher);

    /**
     * 删除教师
     */
    int deleteById(@Param("id") Long id);
}
