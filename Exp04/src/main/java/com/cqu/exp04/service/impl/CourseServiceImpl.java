package com.cqu.exp04.service.impl;

import com.cqu.exp04.entity.Course;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.mapper.CourseMapper;
import com.cqu.exp04.mapper.TeachingClassMapper;
import com.cqu.exp04.service.CourseService;
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
        return courseMapper.findAll();
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    public List<Map<String, Object>> getAvailableClasses(String semester) {
        // 获取所有教学班
        List<TeachingClass> allClasses = teachingClassMapper.findAll();

        // 过滤指定学期的教学班(如果提供了学期参数)
        List<TeachingClass> filteredClasses = semester != null ?
                allClasses.stream()
                        .filter(tc -> semester.equals(tc.getSemester()))
                        .collect(Collectors.toList()) :
                allClasses;

        // 构建返回数据
        return filteredClasses.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("teachingClassId", tc.getId());
                    map.put("classNo", tc.getClassNo());
                    map.put("courseId", tc.getCourseId());
                    map.put("courseName", tc.getCourse().getCourseName());
                    map.put("courseNo", tc.getCourse().getCourseNo());
                    map.put("credit", tc.getCourse().getCredit());
                    map.put("hours", tc.getCourse().getHours());
                    map.put("courseType", tc.getCourse().getCourseType());
                    map.put("teacherId", tc.getTeacherId());
                    map.put("teacherName", tc.getTeacher().getName());
                    map.put("teacherTitle", tc.getTeacher().getTitle());
                    map.put("semester", tc.getSemester());
                    map.put("classroom", tc.getClassroom());
                    map.put("schedule", tc.getSchedule());
                    map.put("maxStudents", tc.getMaxStudents());
                    map.put("currentStudents", tc.getCurrentStudents());
                    map.put("availableSeats", tc.getMaxStudents() - tc.getCurrentStudents());
                    map.put("isFull", tc.getCurrentStudents() >= tc.getMaxStudents());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TeachingClass> getClassesByCourseId(Long courseId) {
        return teachingClassMapper.findByCourseId(courseId);
    }

    @Override
    public Map<String, Object> getClassDetail(Long classId) {
        TeachingClass teachingClass = teachingClassMapper.findByIdWithDetails(classId)
                .orElseThrow(() -> new RuntimeException("教学班不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("teachingClassId", teachingClass.getId());
        result.put("classNo", teachingClass.getClassNo());

        // 课程信息
        Map<String, Object> courseInfo = new HashMap<>();
        courseInfo.put("courseId", teachingClass.getCourse().getId());
        courseInfo.put("courseName", teachingClass.getCourse().getCourseName());
        courseInfo.put("courseNo", teachingClass.getCourse().getCourseNo());
        courseInfo.put("credit", teachingClass.getCourse().getCredit());
        courseInfo.put("hours", teachingClass.getCourse().getHours());
        courseInfo.put("courseType", teachingClass.getCourse().getCourseType());
        courseInfo.put("description", teachingClass.getCourse().getDescription());
        result.put("course", courseInfo);

        // 教师信息
        Map<String, Object> teacherInfo = new HashMap<>();
        teacherInfo.put("teacherId", teachingClass.getTeacher().getId());
        teacherInfo.put("teacherName", teachingClass.getTeacher().getName());
        teacherInfo.put("teacherNo", teachingClass.getTeacher().getTeacherNo());
        teacherInfo.put("title", teachingClass.getTeacher().getTitle());
        teacherInfo.put("department", teachingClass.getTeacher().getDepartment());
        result.put("teacher", teacherInfo);

        // 教学班信息
        result.put("semester", teachingClass.getSemester());
        result.put("classroom", teachingClass.getClassroom());
        result.put("schedule", teachingClass.getSchedule());
        result.put("maxStudents", teachingClass.getMaxStudents());
        result.put("currentStudents", teachingClass.getCurrentStudents());
        result.put("availableSeats", teachingClass.getMaxStudents() - teachingClass.getCurrentStudents());
        result.put("isFull", teachingClass.getCurrentStudents() >= teachingClass.getMaxStudents());

        return result;
    }
}
