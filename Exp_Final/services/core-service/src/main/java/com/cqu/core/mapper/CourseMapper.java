package com.cqu.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据ID查询课程
     */
    default Optional<Course> findById(@Param("id") Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 根据课程编号查询课程
     */
    default Optional<Course> findByCourseNo(@Param("courseNo") String courseNo) {
        return Optional.ofNullable(selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Course>()
                .eq("course_no", courseNo)
        ));
    }

    /**
     * 查询所有课程
     */
    default List<Course> findAll() {
        return selectList(null);
    }

    /**
     * 批量插入课程
     */
    int batchInsert(@Param("courses") List<Course> courses);
}
