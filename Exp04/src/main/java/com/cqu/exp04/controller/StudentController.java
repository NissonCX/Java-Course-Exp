package com.cqu.exp04.controller;

import com.cqu.exp04.dto.AIConsultRequest;
import com.cqu.exp04.entity.Student;
import com.cqu.exp04.security.JwtUtil;
import com.cqu.exp04.service.AIService;
import com.cqu.exp04.service.StudentService;
import com.cqu.exp04.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AIService aiService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Student> getProfile(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Student student = studentService.getById(studentId);
            return Result.success(student);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Student student,
                                        HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            studentService.updateProfile(studentId, student);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询学生所有成绩(带统计信息)
     */
    @GetMapping("/scores")
    public Result<Map<String, Object>> getMyScores(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Map<String, Object> result = studentService.getMyScores(studentId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询我的选课列表
     */
    @GetMapping("/enrollments")
    public Result<?> getMyEnrollments(HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            return Result.success(studentService.getMyEnrollments(studentId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 选课
     */
    @PostMapping("/enroll")
    public Result<String> enrollCourse(@RequestBody Map<String, Long> params,
                                       HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            Long teachingClassId = params.get("teachingClassId");
            if (teachingClassId == null) {
                return Result.error("选课失败: 缺少参数 teachingClassId");
            }
            studentService.enrollCourse(studentId, teachingClassId);
            return Result.success("选课成功");
        } catch (Exception e) {
            return Result.error("选课失败: " + e.getMessage());
        }
    }

    /**
     * 退课
     */
    @DeleteMapping("/enroll/{enrollmentId}")
    public Result<String> dropCourse(@PathVariable Long enrollmentId,
                                     HttpServletRequest request) {
        try {
            Long studentId = (Long) request.getAttribute("roleId");
            studentService.dropCourse(studentId, enrollmentId);
            return Result.success("退课成功");
        } catch (Exception e) {
            return Result.error("退课失败: " + e.getMessage());
        }
    }

    /**
     * AI学习建议咨询
     */
    @PostMapping("/ai/consult")
    public Result<String> aiConsult(@Valid @RequestBody AIConsultRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            Long studentId = (Long) httpRequest.getAttribute("roleId");
            String advice = studentService.aiConsult(studentId, request.getMessage());
            return Result.success(advice);
        } catch (Exception e) {
            return Result.error("AI咨询失败: " + e.getMessage());
        }
    }

    /**
     * AI学习建议咨询 - 流式输出
     */
    @GetMapping(value = "/ai/consult/stream", produces = "text/event-stream")
    public SseEmitter aiConsultStream(@RequestParam String message,
                                      @RequestHeader("Authorization") String authHeader) {
        SseEmitter emitter = new SseEmitter(120000L); // 120秒超时

        try {
            // 从Authorization header解析JWT token
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                emitter.completeWithError(new RuntimeException("无效的认证token"));
                return emitter;
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                emitter.completeWithError(new RuntimeException("token已过期"));
                return emitter;
            }

            Long studentId = jwtUtil.getRoleIdFromToken(token);

            // 直接在Controller线程中调用，保持SecurityContext
            aiService.studentConsultStreamingSync(studentId, message, emitter);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
