package com.cqu.course.service.impl;

import com.cqu.common.entity.Course;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.course.client.TeacherServiceClient;
import com.cqu.course.mapper.CourseMapper;
import com.cqu.course.mapper.TeachingClassMapper;
import com.cqu.course.service.CourseService;
import com.cqu.common.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired(required = false)
    private TeacherServiceClient teacherServiceClient;

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

        // 预加载所有课程信息
        Map<Long, Course> courseMap = new HashMap<>();
        List<Course> allCourses = courseMapper.selectList(null);
        for (Course course : allCourses) {
            courseMap.put(course.getId(), course);
        }

        // 获取Authorization头
        String authorization = getAuthorizationHeader();
        
        // 缓存教师信息
        Map<Long, String> teacherNameCache = new HashMap<>();

        // 构建返回数据
        return filteredClasses.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tc.getId());
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
                    
                    // 状态文本
                    Integer status = tc.getStatus();
                    String statusText = "未知";
                    if (status == null || status == 1) {
                        statusText = "未开课";
                    } else if (status == 2) {
                        statusText = "已开课";
                    } else if (status == 0) {
                        statusText = "已结课";
                    }
                    map.put("statusText", statusText);

                    // 获取课程详情
                    Course course = courseMap.get(tc.getCourseId());
                    if (course != null) {
                        map.put("courseName", course.getCourseName());
                        map.put("courseNo", course.getCourseNo());
                        map.put("credit", course.getCredit());
                        map.put("hours", course.getHours());
                        map.put("courseType", course.getCourseType());
                    } else {
                        map.put("courseName", "未知课程");
                    }

                    // 获取教师名称
                    String teacherName = getTeacherName(tc.getTeacherId(), authorization, teacherNameCache);
                    map.put("teacherName", teacherName);

                    // 判断是否可选课：未开课且有空位
                    boolean canEnroll = (status == null || status == 1)
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

        // 获取课程详情
        if (teachingClass.getCourseId() != null) {
            try {
                Course course = courseMapper.selectById(teachingClass.getCourseId());
                if (course != null) {
                    result.put("courseName", course.getCourseName());
                    result.put("courseNo", course.getCourseNo());
                    result.put("credit", course.getCredit());
                    result.put("hours", course.getHours());
                    result.put("courseType", course.getCourseType());
                    result.put("courseDescription", course.getDescription());
                }
            } catch (Exception e) {
                result.put("courseName", "未知课程");
            }
        }

        // 获取教师名称
        String authorization = getAuthorizationHeader();
        String teacherName = getTeacherName(teachingClass.getTeacherId(), authorization, new HashMap<>());
        result.put("teacherName", teacherName);

        return result;
    }

    /**
     * 获取教师名称
     */
    private String getTeacherName(Long teacherId, String authorization, Map<Long, String> cache) {
        if (teacherId == null) {
            return null;
        }
        
        // 检查缓存
        if (cache.containsKey(teacherId)) {
            return cache.get(teacherId);
        }
        
        String teacherName = null;
        if (teacherServiceClient != null && authorization != null) {
            try {
                Result<Teacher> teacherResult = teacherServiceClient.getTeacherById(teacherId, authorization);
                if (teacherResult.getCode() == 200 && teacherResult.getData() != null) {
                    teacherName = teacherResult.getData().getName();
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }
        
        cache.put(teacherId, teacherName);
        return teacherName;
    }

    /**
     * 从当前请求中获取 Authorization 头
     */
    private String getAuthorizationHeader() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        return null;
    }
}