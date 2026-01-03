package com.cqu.teacher.service;

import com.cqu.common.entity.Teacher;

import java.util.List;
import java.util.Map;

/**
 * 教师服务接口
 */
public interface TeacherService {

    /**
     * 获取教师个人信息
     */
    Teacher getProfile(Long teacherId);

    /**
     * 更新教师个人信息
     */
    void updateProfile(Teacher teacher);

    /**
     * 查询教师的所有教学班
     */
    List<Map<String, Object>> getMyClasses(Long teacherId);

    /**
     * 查询指定教学班详情
     */
    Map<String, Object> getClassDetail(Long teacherId, Long classId);

    /**
     * 查询教学班学生列表
     */
    List<Map<String, Object>> getClassStudents(Long teacherId, Long classId);

    /**
     * 根据ID查询教师
     */
    Teacher getById(Long teacherId);

    /**
     * 批量查询教师
     */
    List<Teacher> getByIds(List<Long> teacherIds);
}