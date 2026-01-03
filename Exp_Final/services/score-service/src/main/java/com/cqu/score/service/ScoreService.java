package com.cqu.score.service;

import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.entity.Score;
import com.cqu.common.vo.ClassScoreStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 */
public interface ScoreService {

    /**
     * 查询学生所有成绩(带统计信息)
     */
    Map<String, Object> getStudentScores(Long studentId);

    /**
     * 查询教学班学生成绩列表
     */
    List<Score> getClassScores(Long classId, Long teacherId);

    /**
     * 获取班级学生成绩列表（包含学生信息）
     * 用于教师端成绩管理页面
     */
    List<Map<String, Object>> getClassStudentsWithScores(Long classId, Long teacherId);

    /**
     * 录入/更新学生成绩
     */
    void inputScore(Long teacherId, ScoreInputRequest request);

    /**
     * 批量录入成绩
     */
    void batchInputScores(Long teacherId, Long teachingClassId, List<ScoreInputRequest> requests);

    /**
     * 查询教学班成绩统计
     */
    ClassScoreStatisticsVO getClassStatistics(Long classId, Long teacherId);

    /**
     * 计算单个成绩的总分和绩点
     */
    void calculateScore(Long scoreId);

    /**
     * 批量计算教学班所有成绩
     */
    void calculateClassScores(Long classId);
}