package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 选课记录Mapper接口
 */
@Mapper
public interface EnrollmentMapper {

    /**
     * 根据ID查询选课记录
     */
    Optional<Enrollment> findById(@Param("id") Long id);

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
     * 插入选课记录
     */
    int insert(Enrollment enrollment);

    /**
     * 批量插入选课记录
     */
    int batchInsert(@Param("enrollments") List<Enrollment> enrollments);

    /**
     * 更新选课记录
     */
    int update(Enrollment enrollment);

    /**
     * 删除选课记录
     */
    int deleteById(@Param("id") Long id);
}
