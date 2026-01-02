package com.cqu.exp04.service;

import com.cqu.exp04.dto.ScoreInputRequest;
import com.cqu.exp04.dto.TeacherRegisterRequest;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import com.cqu.exp04.vo.LoginResponse;

import java.util.List;
import java.util.Map;

/**
 * 教师服务接口
 */
public interface TeacherService {

    /**
     * 教师注册
     */
    LoginResponse register(TeacherRegisterRequest request);

    /**
     * 根据教工号查询教师
     */
    Teacher getByTeacherNo(String teacherNo);

    /**
     * 根据ID查询教师
     */
    Teacher getById(Long id);

    /**
     * 查询教师的所有教学班
     */
    List<TeachingClass> getMyClasses(Long teacherId);

    /**
     * 查询教学班学生列表
     */
    List<Map<String, Object>> getClassStudents(Long teacherId, Long teachingClassId);

    /**
     * 查询教学班成绩列表
     */
    List<Score> getClassScores(Long teacherId, Long teachingClassId);

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
    ClassScoreStatisticsVO getClassStatistics(Long teacherId, Long teachingClassId);

    /**
     * AI教学分析咨询
     */
    String aiConsult(Long teacherId, Long teachingClassId, String message);

    /**
     * 更新教学班状态（未开课/已开课/已结课）
     */
    void updateTeachingClassStatus(Long teacherId, Long teachingClassId, Integer status);

    /**
     * 更新教师信息
     */
    void updateProfile(Long teacherId, String email, String phone);
}
