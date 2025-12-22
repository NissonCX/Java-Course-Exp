package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper {

    /**
     * 根据学号查询学生
     */
    Optional<Student> findByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据用户ID查询学生
     */
    Optional<Student> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID查询学生
     */
    Optional<Student> findById(@Param("id") Long id);

    /**
     * 查询所有学生
     */
    List<Student> findAll();

    /**
     * 根据专业查询学生
     */
    List<Student> findByMajor(@Param("major") String major);

    /**
     * 插入学生
     */
    int insert(Student student);

    /**
     * 批量插入学生
     */
    int batchInsert(@Param("students") List<Student> students);

    /**
     * 更新学生
     */
    int update(Student student);

    /**
     * 删除学生
     */
    int deleteById(@Param("id") Long id);
}
