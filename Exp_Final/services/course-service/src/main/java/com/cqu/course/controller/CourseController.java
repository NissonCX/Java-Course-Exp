package com.cqu.course.controller;

import com.cqu.common.entity.Course;
import com.cqu.common.entity.TeachingClass;
import com.cqu.course.service.CourseService;
import com.cqu.common.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程控制器 - 公共接口
 */
@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 查询所有课程
     */
    @GetMapping("/list")
    public Result<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询课程详情
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.getById(id);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有可选教学班
     */
    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> getAvailableClasses(
            @RequestParam(required = false) String semester) {
        try {
            List<Map<String, Object>> classes = courseService.getAvailableClasses(semester);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据课程ID查询教学班列表
     */
    @GetMapping("/{courseId}/classes")
    public Result<List<TeachingClass>> getClassesByCourseId(@PathVariable Long courseId) {
        try {
            List<TeachingClass> classes = courseService.getClassesByCourseId(courseId);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班详情
     */
    @GetMapping("/class/{classId}")
    public Result<Map<String, Object>> getClassDetail(@PathVariable Long classId) {
        try {
            Map<String, Object> detail = courseService.getClassDetail(classId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}