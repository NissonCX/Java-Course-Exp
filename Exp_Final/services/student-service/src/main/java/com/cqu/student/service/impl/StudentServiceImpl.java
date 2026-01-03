package com.cqu.student.service.impl;

import com.cqu.common.entity.Enrollment;
import com.cqu.common.entity.Score;
import com.cqu.common.entity.Student;
import com.cqu.common.entity.User;
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
                    if (classDetail.getCode() == 200) {
                        map.put("classDetail", classDetail.getData());
                    }
                } catch (Exception ex) {
                    map.put("classDetailError", "获取教学班信息失败: " + ex.getMessage());
                }
            }

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
        // 更新用户信息需要调用 auth-service 或直接更新 user 表
        // Phase 2.1 暂不实现
        throw new RuntimeException("请联系管理员更新个人信息");
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
