package com.cqu.student.service.impl;

import com.cqu.common.entity.Enrollment;
import com.cqu.common.entity.Score;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.User;
import com.cqu.student.client.AuthServiceClient;
import com.cqu.student.client.CourseServiceClient;
import com.cqu.student.client.ScoreServiceClient;
import com.cqu.student.mapper.EnrollmentMapper;
import com.cqu.student.mapper.StudentMapper;
import com.cqu.student.service.StudentService;
import com.cqu.common.vo.Result;
import com.cqu.common.vo.StudentScoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生服务实现类（Phase 2 简化版）
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private ScoreServiceClient scoreServiceClient;

    @Autowired
    private CourseServiceClient courseServiceClient;

    @Autowired(required = false)
    private AuthServiceClient authServiceClient;

    @Override
    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return studentMapper.findByStudentNo(studentNo).orElse(null);
    }

    @Override
    public Map<String, Object> getMyScores(Long studentId) {
        try {
            String authorization = getAuthorizationHeader();
            if (authorization == null) {
                throw new RuntimeException("认证信息缺失");
            }

            Result<Map<String, Object>> result = scoreServiceClient.getStudentScores(studentId, authorization);
            if (result.getCode() == 200) {
                return result.getData();
            } else {
                throw new RuntimeException("获取成绩失败: " + result.getMessage());
            }
        } catch (Exception e) {
            Map<String, Object> fallbackResult = new HashMap<>();
            fallbackResult.put("error", "调用 score-service 失败: " + e.getMessage());
            fallbackResult.put("scores", List.of());
            fallbackResult.put("statistics", Map.of());
            return fallbackResult;
        }
    }

    @Override
    public List<StudentScoreVO> getMyScoreDetails(Long studentId) {
        // 该方法需要调用 score-service
        return List.of();
    }

    @Override
    @Transactional
    public void enrollCourse(Long studentId, Long teachingClassId) {
        // 检查是否已选课
        List<Enrollment> existing = enrollmentMapper.findByStudentId(studentId);
        boolean alreadyEnrolled = existing.stream()
                .anyMatch(e -> e.getTeachingClassId().equals(teachingClassId));

        if (alreadyEnrolled) {
            throw new RuntimeException("已经选过这门课了");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setTeachingClassId(teachingClassId);
        enrollmentMapper.insert(enrollment);
    }

    @Override
    public List<Map<String, Object>> getMyEnrollments(Long studentId) {
        List<Enrollment> enrollments = enrollmentMapper.findByStudentId(studentId);
        String authorization = getAuthorizationHeader();

        return enrollments.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("enrollmentId", e.getId());
            map.put("teachingClassId", e.getTeachingClassId());
            map.put("enrollTime", e.getEnrollTime());

            // 尝试获取教学班详情
            if (authorization != null) {
                try {
                    Result<Map<String, Object>> classDetail = courseServiceClient.getClassDetail(e.getTeachingClassId(), authorization);
                    if (classDetail.getCode() == 200 && classDetail.getData() != null) {
                        Map<String, Object> detail = classDetail.getData();
                        // 提取前端需要的字段
                        map.put("classNo", detail.get("classNo"));
                        map.put("courseName", detail.get("courseName"));
                        map.put("courseNo", detail.get("courseNo"));
                        map.put("credit", detail.get("credit"));
                        map.put("semester", detail.get("semester"));
                        map.put("teacherName", detail.get("teacherName"));
                        map.put("classroom", detail.get("classroom"));
                        map.put("schedule", detail.get("schedule"));
                        map.put("maxStudents", detail.get("maxStudents"));
                        map.put("currentStudents", detail.get("currentStudents"));
                        
                        // 教学班状态
                        Object statusObj = detail.get("status");
                        Integer status = statusObj != null ? ((Number) statusObj).intValue() : 1;
                        map.put("teachingClassStatus", status);
                        
                        String statusText = "未知";
                        if (status == null || status == 1) {
                            statusText = "未开课";
                        } else if (status == 2) {
                            statusText = "已开课";
                        } else if (status == 0) {
                            statusText = "已结课";
                        }
                        map.put("teachingClassStatusText", statusText);
                    }
                } catch (Exception ex) {
                    map.put("classNo", "-");
                    map.put("courseName", "获取失败");
                    map.put("teachingClassStatus", 1);
                    map.put("teachingClassStatusText", "未知");
                }
            }

            // 检查是否已有成绩（简化处理，暂时返回false）
            map.put("hasScore", false);

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void dropCourse(Long studentId, Long enrollmentId) {
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null || !enrollment.getStudentId().equals(studentId)) {
            throw new RuntimeException("选课记录不存在或无权操作");
        }
        enrollmentMapper.deleteById(enrollmentId);
    }

    @Override
    public void updateProfile(Long studentId, String email, String phone) {
        // 获取学生信息以获取 userId
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 调用 auth-service 更新用户信息
        if (authServiceClient != null) {
            Map<String, String> params = new HashMap<>();
            if (email != null) {
                params.put("email", email);
            }
            if (phone != null) {
                params.put("phone", phone);
            }
            
            Result<String> result = authServiceClient.updateUser(student.getUserId(), params);
            if (result.getCode() != 200) {
                throw new RuntimeException(result.getMessage());
            }
        } else {
            throw new RuntimeException("无法连接认证服务");
        }
    }

    /**
     * 获取学生完整信息（包含用户信息）
     */
    public Map<String, Object> getProfileWithUserInfo(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", student.getId());
        profile.put("studentNo", student.getStudentNo());
        profile.put("userId", student.getUserId());
        profile.put("name", student.getName());
        profile.put("gender", student.getGender());
        profile.put("birthDate", student.getBirthDate());
        profile.put("major", student.getMajor());
        profile.put("className", student.getClassName());
        profile.put("grade", student.getGrade());
        profile.put("enrollmentYear", student.getEnrollmentYear());
        profile.put("createTime", student.getCreateTime());
        profile.put("updateTime", student.getUpdateTime());

        // 尝试从 auth-service 获取用户信息
        if (authServiceClient != null && student.getUserId() != null) {
            try {
                Result<Map<String, Object>> userResult = authServiceClient.getUserById(student.getUserId());
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

    @Override
    public String aiConsult(Long studentId, String message) {
        // AI 咨询功能由 ai-service 提供
        return "AI 咨询功能请访问 ai-service";
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
