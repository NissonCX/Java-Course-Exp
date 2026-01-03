package com.cqu.course.service.impl;

import com.cqu.common.entity.Course;
import com.cqu.common.entity.TeachingClass;
import com.cqu.course.mapper.CourseMapper;
import com.cqu.course.mapper.TeachingClassMapper;
import com.cqu.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Override
    public List<Course> getAllCourses() {
        return courseMapper.selectList(null);
    }

    @Override
    public Course getById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        return course;
    }

    @Override
    public List<Map<String, Object>> getAvailableClasses(String semester) {
        // 简化版本：获取所有教学班的基本信息
        List<TeachingClass> allClasses = teachingClassMapper.selectList(null);

        // 过滤指定学期的教学班(如果提供了学期参数)
        List<TeachingClass> filteredClasses = semester != null ?
                allClasses.stream()
                        .filter(tc -> semester.equals(tc.getSemester()))
                        .collect(Collectors.toList()) :
                allClasses;

        // 构建返回数据（简化版本，不包含课程和教师详情）
        return filteredClasses.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("teachingClassId", tc.getId());
                    map.put("classNo", tc.getClassNo());
                    map.put("courseId", tc.getCourseId());
                    map.put("teacherId", tc.getTeacherId());
                    map.put("semester", tc.getSemester());
                    map.put("classroom", tc.getClassroom());
                    map.put("schedule", tc.getSchedule());
                    map.put("maxStudents", tc.getMaxStudents());
                    map.put("currentStudents", tc.getCurrentStudents());
                    map.put("availableSeats", tc.getMaxStudents() - tc.getCurrentStudents());
                    map.put("isFull", tc.getCurrentStudents() >= tc.getMaxStudents());
                    map.put("status", tc.getStatus());

                    // 课程和教师详情需要调用其他服务获取
                    map.put("courseName", "请调用 course-service 获取课程详情");
                    map.put("teacherName", "请调用 teacher-service 获取教师详情");

                    boolean canEnroll = "OPEN".equals(tc.getStatus())
                            && tc.getCurrentStudents() < tc.getMaxStudents();
                    map.put("canEnroll", canEnroll);

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TeachingClass> getClassesByCourseId(Long courseId) {
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", courseId);
        return teachingClassMapper.selectByMap(params);
    }

    @Override
    public Map<String, Object> getClassDetail(Long classId) {
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        if (teachingClass == null) {
            throw new RuntimeException("教学班不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("teachingClassId", teachingClass.getId());
        result.put("classNo", teachingClass.getClassNo());
        result.put("courseId", teachingClass.getCourseId());
        result.put("teacherId", teachingClass.getTeacherId());
        result.put("semester", teachingClass.getSemester());
        result.put("classroom", teachingClass.getClassroom());
        result.put("schedule", teachingClass.getSchedule());
        result.put("maxStudents", teachingClass.getMaxStudents());
        result.put("currentStudents", teachingClass.getCurrentStudents());
        result.put("availableSeats", teachingClass.getMaxStudents() - teachingClass.getCurrentStudents());
        result.put("isFull", teachingClass.getCurrentStudents() >= teachingClass.getMaxStudents());
        result.put("status", teachingClass.getStatus());

        // 课程和教师详情需要通过其他服务获取
        result.put("courseInfo", "请调用 course-service 获取课程详情");
        result.put("teacherInfo", "请调用 teacher-service 获取教师详情");

        return result;
    }
}