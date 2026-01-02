package com.cqu.exp04.service;

import com.cqu.exp04.dto.AIConsultRequest;
import com.cqu.exp04.dto.StudentRegisterRequest;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.vo.LoginResponse;
import com.cqu.exp04.vo.StudentScoreVO;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {

    /**
     * 学生注册
     */
    LoginResponse register(StudentRegisterRequest request);

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
