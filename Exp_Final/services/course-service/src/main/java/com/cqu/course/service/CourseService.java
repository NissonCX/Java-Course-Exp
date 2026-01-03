package com.cqu.course.service;

import com.cqu.common.entity.Course;
import com.cqu.common.entity.TeachingClass;

import java.util.List;
import java.util.Map;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 查询所有课程
     */
    List<Course> getAllCourses();

    /**
     * 根据ID查询课程
     */
    Course getById(Long id);

    /**
     * 查询所有开课的教学班(用于学生选课)
     */
    List<Map<String, Object>> getAvailableClasses(String semester);

    /**
     * 根据课程ID查询教学班
     */
    List<TeachingClass> getClassesByCourseId(Long courseId);

    /**
     * 查询教学班详情(包含课程和教师信息)
     */
    Map<String, Object> getClassDetail(Long classId);
}