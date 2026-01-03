package com.cqu.student.service;

import com.cqu.common.entity.Student;
import com.cqu.common.vo.StudentScoreVO;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口（Phase 2 简化版）
 */
public interface StudentService {

    /**
     * 根据学号查询学生
     */
    Student getByStudentNo(String studentNo);

    /**
     * 根据ID查询学生
     */
    Student getById(Long id);

    /**
     * 查询学生所有成绩(带统计信息)
     */
    Map<String, Object> getMyScores(Long studentId);

    /**
     * 查询学生成绩详情列表
     */
    List<StudentScoreVO> getMyScoreDetails(Long studentId);

    /**
     * AI学习建议咨询
     */
    String aiConsult(Long studentId, String message);

    /**
     * 学生选课
     */
    void enrollCourse(Long studentId, Long teachingClassId);

    /**
     * 查询学生的选课列表
     */
    List<Map<String, Object>> getMyEnrollments(Long studentId);

    /**
     * 退课
     */
    void dropCourse(Long studentId, Long enrollmentId);

    /**
     * 更新学生信息
     */
    void updateProfile(Long studentId, String email, String phone);
}
