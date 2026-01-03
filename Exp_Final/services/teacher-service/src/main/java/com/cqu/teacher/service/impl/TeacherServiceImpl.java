package com.cqu.teacher.service.impl;

import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.teacher.client.AuthServiceClient;
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
import java.util.ArrayList;
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

    @Autowired(required = false)
    private AuthServiceClient authServiceClient;

    @Override
    public Teacher getProfile(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }
        return teacher;
    }

    /**
     * 获取教师完整信息（包含用户信息）
     */
    public Map<String, Object> getProfileWithUserInfo(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", teacher.getId());
        profile.put("teacherNo", teacher.getTeacherNo());
        profile.put("userId", teacher.getUserId());
        profile.put("name", teacher.getName());
        profile.put("title", teacher.getTitle());
        profile.put("department", teacher.getDepartment());
        profile.put("createTime", teacher.getCreateTime());
        profile.put("updateTime", teacher.getUpdateTime());

        // 尝试从 auth-service 获取用户信息
        if (authServiceClient != null && teacher.getUserId() != null) {
            try {
                Result<Map<String, Object>> userResult = authServiceClient.getUserById(teacher.getUserId());
                if (userResult.getCode() == 200 && userResult.getData() != null) {
                    Map<String, Object> userInfo = userResult.getData();
                    profile.put("email", userInfo.get("email"));
                    profile.put("phone", userInfo.get("phone"));
                    profile.put("username", userInfo.get("username"));
                }
            } catch (Exception e) {
                profile.put("email", null);
                profile.put("phone", null);
                profile.put("_userInfoError", "获取用户信息失败: " + e.getMessage());
            }
        }

        return profile;
    }

    /**
     * 更新教师个人信息（包括用户信息）
     */
    public void updateProfileWithUserInfo(Long teacherId, String email, String phone) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }

        // 调用 auth-service 更新用户信息
        if (authServiceClient != null && teacher.getUserId() != null) {
            Map<String, String> params = new HashMap<>();
            if (email != null) {
                params.put("email", email);
            }
            if (phone != null) {
                params.put("phone", phone);
            }
            
            Result<String> result = authServiceClient.updateUser(teacher.getUserId(), params);
            if (result.getCode() != 200) {
                throw new RuntimeException(result.getMessage());
            }
        } else {
            throw new RuntimeException("无法连接认证服务");
        }
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
                    map.put("id", tc.getId());
                    map.put("teachingClassId", tc.getId());
                    map.put("classNo", tc.getClassNo());
                    map.put("courseId", tc.getCourseId());
                    map.put("semester", tc.getSemester());
                    map.put("classroom", tc.getClassroom());
                    map.put("schedule", tc.getSchedule());
                    map.put("maxStudents", tc.getMaxStudents());
                    map.put("currentStudents", tc.getCurrentStudents());
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

                    // 尝试通过 course-service 获取课程详情
                    if (authorization != null) {
                        try {
                            Result<Map<String, Object>> classDetail = courseServiceClient.getClassDetail(tc.getId(), authorization);
                            if (classDetail.getCode() == 200 && classDetail.getData() != null) {
                                Map<String, Object> detail = classDetail.getData();
                                // 构建course对象供前端使用
                                Map<String, Object> course = new HashMap<>();
                                course.put("courseName", detail.get("courseName"));
                                course.put("courseNo", detail.get("courseNo"));
                                course.put("credit", detail.get("credit"));
                                course.put("hours", detail.get("hours"));
                                course.put("courseType", detail.get("courseType"));
                                map.put("course", course);
                                map.put("courseName", detail.get("courseName"));
                            }
                        } catch (Exception e) {
                            // 获取失败时使用默认值
                            Map<String, Object> course = new HashMap<>();
                            course.put("courseName", "课程信息获取失败");
                            map.put("course", course);
                        }
                    }

                    // 如果没有获取到课程信息，提供默认值
                    if (!map.containsKey("course")) {
                        Map<String, Object> course = new HashMap<>();
                        course.put("courseName", "未知课程");
                        map.put("course", course);
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
        List<Map<String, Object>> result = new ArrayList<>();

        // 尝试通过 score-service 获取学生成绩信息（包含学生详情）
        if (authorization != null) {
            try {
                Result<List<Map<String, Object>>> studentsResult = scoreServiceClient.getClassStudentsWithScores(classId, teacherId, authorization);
                if (studentsResult.getCode() == 200 && studentsResult.getData() != null) {
                    result = studentsResult.getData();
                }
            } catch (Exception e) {
                // 调用失败时返回空列表，让前端显示"暂无数据"
                // 可以在日志中记录错误
                System.err.println("调用成绩服务失败: " + e.getMessage());
            }
        }

        // 返回结果（可能为空列表）
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

    @Override
    public void updateClassStatus(Long teacherId, Long classId, Integer status) {
        // 验证教学班是否属于该教师
        TeachingClass teachingClass = teachingClassMapper.selectById(classId);
        if (teachingClass == null) {
            throw new RuntimeException("教学班不存在");
        }
        if (!teacherId.equals(teachingClass.getTeacherId())) {
            throw new RuntimeException("无权修改该教学班状态");
        }
        
        // 验证状态值
        if (status == null || (status != 0 && status != 1 && status != 2)) {
            throw new RuntimeException("无效的状态值");
        }
        
        // 已结课的教学班不能重新开课
        if (teachingClass.getStatus() != null && teachingClass.getStatus() == 0 && status != 0) {
            throw new RuntimeException("已结课的教学班不可重新开课");
        }
        
        // 更新状态
        teachingClass.setStatus(status);
        teachingClassMapper.updateById(teachingClass);
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