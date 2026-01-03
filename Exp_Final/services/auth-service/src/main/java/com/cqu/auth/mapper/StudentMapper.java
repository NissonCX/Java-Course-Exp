package com.cqu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据学号查询学生
     */
    default Optional<Student> findByStudentNo(@Param("studentNo") String studentNo) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student>()
                .eq("student_no", studentNo)
        ));
    }

    /**
     * 根据用户ID查询学生
     */
    default Optional<Student> findByUserId(@Param("userId") Long userId) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student>()
                .eq("user_id", userId)
        ));
    }

    /**
     * 根据ID查询学生
     */
    default Optional<Student> findById(@Param("id") Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 查询所有学生
     */
    default List<Student> findAll() {
        return selectList(null);
    }

    /**
     * 根据专业查询学生
     */
    default List<Student> findByMajor(@Param("major") String major) {
        return selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student>()
                .eq("major", major)
        );
    }

    /**
     * 批量插入学生
     */
    int batchInsert(@Param("students") List<Student> students);
}
