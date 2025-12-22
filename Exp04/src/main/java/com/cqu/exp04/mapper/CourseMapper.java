package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper {

    /**
     * 根据ID查询课程
     */
    Optional<Course> findById(@Param("id") Long id);

    /**
     * 根据课程编号查询课程
     */
    Optional<Course> findByCourseNo(@Param("courseNo") String courseNo);

    /**
     * 查询所有课程
     */
    List<Course> findAll();

    /**
     * 插入课程
     */
    int insert(Course course);

    /**
     * 批量插入课程
     */
    int batchInsert(@Param("courses") List<Course> courses);

    /**
     * 更新课程
     */
    int update(Course course);

    /**
     * 删除课程
     */
    int deleteById(@Param("id") Long id);
}
