package com.cqu.exp04.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.exp04.entity.TeachingClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 教学班Mapper接口
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {

    /**
     * 根据ID查询教学班(包含课程和教师信息)
     */
    Optional<TeachingClass> findByIdWithDetails(@Param("id") Long id);

    /**
     * 根据教师ID查询教学班列表
     */
    List<TeachingClass> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据课程ID查询教学班列表
     */
    List<TeachingClass> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询所有教学班
     */
    List<TeachingClass> findAll();

    /**
     * 更新学生数量
     */
    int updateCurrentStudents(@Param("id") Long id, @Param("currentStudents") Integer currentStudents);

    /**
     * 学生数 +1（若未满员则更新成功；用于并发选课防超额）
     */
    int incrementCurrentStudentsIfNotFull(@Param("id") Long id);

    /**
     * 批量插入教学班
     */
    int batchInsert(@Param("classes") List<TeachingClass> classes);
}
