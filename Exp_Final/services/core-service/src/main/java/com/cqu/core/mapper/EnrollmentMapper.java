package com.cqu.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.common.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 选课记录Mapper接口
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {

    /**
     * 根据ID查询选课记录
     */
    default Optional<Enrollment> findById(@Param("id") Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 根据学生ID查询选课记录
     */
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据教学班ID查询选课记录
     */
    List<Enrollment> findByTeachingClassId(@Param("teachingClassId") Long teachingClassId);

    /**
     * 查询学生在某教学班的选课记录
     */
    Optional<Enrollment> findByStudentAndClass(
            @Param("studentId") Long studentId,
            @Param("teachingClassId") Long teachingClassId);

    /**
     * 批量插入选课记录
     */
    int batchInsert(@Param("enrollments") List<Enrollment> enrollments);
}
