package com.cqu.exp04.mapper;

import com.cqu.exp04.entity.Score;
import com.cqu.exp04.vo.StudentScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface ScoreMapper {

    /**
     * 根据ID查询成绩
     */
    Optional<Score> findById(@Param("id") Long id);

    /**
     * 根据选课记录ID查询成绩
     */
    Optional<Score> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    /**
     * 根据学生ID查询所有成绩
     */
    List<Score> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据教学班ID查询所有成绩
     */
    List<Score> findByTeachingClassId(@Param("teachingClassId") Long teachingClassId);

    /**
     * 查询学生成绩详情(包含关联信息)
     */
    List<StudentScoreVO> findStudentScoreDetails(@Param("studentId") Long studentId);

    /**
     * 查询教学班成绩列表(包含学生信息)
     */
    List<Score> findClassScoresWithStudent(@Param("teachingClassId") Long teachingClassId);

    /**
     * 插入成绩
     */
    int insert(Score score);

    /**
     * 批量插入成绩
     */
    int batchInsert(@Param("scores") List<Score> scores);

    /**
     * 更新成绩
     */
    int update(Score score);

    /**
     * 删除成绩
     */
    int deleteById(@Param("id") Long id);

    /**
     * 计算学生平均分
     */
    Double calculateStudentAverageScore(@Param("studentId") Long studentId);

    /**
     * 统计教学班成绩分布
     */
    List<Integer> countScoreDistribution(@Param("teachingClassId") Long teachingClassId);
}
