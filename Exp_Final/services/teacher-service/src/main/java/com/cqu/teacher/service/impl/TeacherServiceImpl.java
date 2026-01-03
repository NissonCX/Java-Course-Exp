package com.cqu.teacher.service.impl;

import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.teacher.client.CourseServiceClient;
import com.cqu.teacher.client.ScoreServiceClient;
import com.cqu.teacher.mapper.TeacherMapper;
import com.cqu.teacher.mapper.TeachingClassMapper;
import com.cqu.teacher.service.TeacherService;
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
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private TeachingClassMapper teachingClassMapper;

    @Autowired
    private CourseServiceClient courseServiceClient;

    @Autowired
    private ScoreServiceClient scoreServiceClient;

    @Override
    public Teacher getProfile(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }
        return teacher;
    }

    @Override
    public void updateProfile(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        teacherMapper.updateById(teacher);
    }

    @Override
    public List<Map<String, Object>> getMyClasses(Long teacherId) {
        // 查询该教师的所有教学班
        Map<String, Object> params = new HashMap<>();
        params.put("teacher_id", teacherId);
        List<TeachingClass> classes = teachingClassMapper.selectByMap(params);
        String authorization = getAuthorizationHeader();

        // 转换为包含基本信息的Map，尝试获取课程详情
        return classes.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("teachingClassId", tc.getId());
                    map.put("classNo", tc.getClassNo());
                    map.put("courseId", tc.getCourseId());
                    map.put("semester", tc.getSemester());
                    map.put("classroom", tc.getClassroom());
                    map.put("schedule", tc.getSchedule());
                    map.put("maxStudents", tc.getMaxStudents());
                    map.put("currentStudents", tc.getCurrentStudents());
                    map.put("status", tc.getStatus());

                    // 尝试通过 course-service 获取课程详情
                    if (authorization != null) {
                        try {
                            Result<List<Map<String, Object>>> courseResult = courseServiceClient.getAvailableClasses(tc.getSemester(), authorization);
                            if (courseResult.getCode() == 200) {
                                // 简化处理：从课程列表中找到匹配的课程
                                List<Map<String, Object>> courses = courseResult.getData();
                                courses.stream()
                                        .filter(course -> course.get("courseId").equals(tc.getCourseId()))
                                        .findFirst()
                                        .ifPresent(course -> {
                                            map.put("courseName", course.get("courseName"));
                                            map.put("courseInfo", course);
                                        });
                            }
                        } catch (Exception e) {
                            map.put("courseError", "获取课程信息失败: " + e.getMessage());
                        }
                    }

                    // 如果没有获取到课程信息，提供默认值
                    if (!map.containsKey("courseName")) {
                        map.put("courseName", "课程信息获取失败");
                        map.put("courseInfo", Map.of("error", "无法连接到课程服务"));
                    }

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getClassDetail(Long teacherId, Long classId) {
        // 验证教学班是否属于该教师
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        if (teachingClass == null || !teacherId.equals(teachingClass.getTeacherId())) {
            throw new RuntimeException("无权访问该教学班信息");
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
        result.put("status", teachingClass.getStatus());

        String authorization = getAuthorizationHeader();

        // 尝试获取班级成绩信息
        if (authorization != null) {
            try {
                Result<List<Object>> scoresResult = scoreServiceClient.getClassScores(classId, teacherId, authorization);
                if (scoresResult.getCode() == 200) {
                    result.put("studentsScores", scoresResult.getData());
                } else {
                    result.put("scoresError", "获取成绩失败: " + scoresResult.getMessage());
                }
            } catch (Exception e) {
                result.put("scoresError", "调用成绩服务失败: " + e.getMessage());
            }
        }

        // 如果没有成绩信息，提供默认值
        if (!result.containsKey("studentsScores")) {
            result.put("studentsScores", List.of());
            result.put("scoresNote", "请手动调用成绩服务获取学生成绩");
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getClassStudents(Long teacherId, Long classId) {
        // 验证教学班是否属于该教师
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        if (teachingClass == null || !teacherId.equals(teachingClass.getTeacherId())) {
            throw new RuntimeException("无权访问该教学班学生信息");
        }

        String authorization = getAuthorizationHeader();
        List<Map<String, Object>> result = List.of();

        // 尝试通过 score-service 获取学生信息
        if (authorization != null) {
            try {
                Result<List<Object>> scoresResult = scoreServiceClient.getClassScores(classId, teacherId, authorization);
                if (scoresResult.getCode() == 200) {
                    // 将成绩数据转换为学生信息（简化处理）
                    result = scoresResult.getData().stream()
                            .map(score -> {
                                Map<String, Object> student = new HashMap<>();
                                student.put("scoreData", score);
                                student.put("note", "此数据来自成绩服务，包含学生和成绩信息");
                                return student;
                            })
                            .collect(Collectors.toList());
                }
            } catch (Exception e) {
                // 返回错误信息
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("error", "调用成绩服务失败: " + e.getMessage());
                errorInfo.put("teachingClassId", classId);
                result = List.of(errorInfo);
            }
        }

        // 如果没有数据，返回提示信息
        if (result.isEmpty()) {
            Map<String, Object> info = new HashMap<>();
            info.put("message", "无法获取学生信息，请检查服务连接");
            info.put("teachingClassId", classId);
            result = List.of(info);
        }

        return result;
    }

    @Override
    public Teacher getById(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        return teacher;
    }

    @Override
    public List<Teacher> getByIds(List<Long> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) {
            return List.of();
        }
        return teacherMapper.selectBatchIds(teacherIds);
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