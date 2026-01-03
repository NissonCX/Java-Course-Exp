package com.cqu.core.controller;

import com.cqu.common.dto.AIConsultRequest;
import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.entity.Score;
import com.cqu.common.entity.Teacher;
import com.cqu.common.entity.TeachingClass;
import com.cqu.common.entity.User;
import com.cqu.core.mapper.UserMapper;
import com.cqu.security.JwtUtil;
import com.cqu.core.service.AIService;
import com.cqu.core.service.TeacherService;
import com.cqu.common.vo.ClassScoreStatisticsVO;
import com.cqu.common.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AIService aiService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Teacher teacher = teacherService.getById(teacherId);
            
            // 获取对应的用户信息以获取邮箱和电话
            User user = userMapper.findById(teacher.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 合并教师和用户信息
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", teacher.getId());
            profile.put("teacherNo", teacher.getTeacherNo());
            profile.put("userId", teacher.getUserId());
            profile.put("name", teacher.getName());
            profile.put("gender", teacher.getGender());
            profile.put("title", teacher.getTitle());
            profile.put("department", teacher.getDepartment());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("createTime", teacher.getCreateTime());
            profile.put("updateTime", teacher.getUpdateTime());
            
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, String> profileData,
                                        HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            String email = profileData.get("email");
            String phone = profileData.get("phone");
            
            teacherService.updateProfile(teacherId, email, phone);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询教师的所有教学班
     */
    @GetMapping("/classes")
    public Result<List<TeachingClass>> getMyClasses(HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            List<TeachingClass> classes = teacherService.getMyClasses(teacherId);
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班学生列表
     */
    @GetMapping("/class/{classId}/students")
    public Result<List<Map<String, Object>>> getClassStudents(@PathVariable Long classId,
                                                               HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            List<Map<String, Object>> students = teacherService.getClassStudents(teacherId, classId);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新教学班状态（未开课/已开课/已结课）
     */
    @PutMapping("/class/{classId}/status")
    public Result<String> updateClassStatus(@PathVariable Long classId,
                                            @Valid @RequestBody com.cqu.common.dto.TeachingClassStatusUpdateRequest body,
                                            HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            teacherService.updateTeachingClassStatus(teacherId, classId, body.getStatus());
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班学生成绩列表
     */
    @GetMapping("/class/{classId}/scores")
    public Result<List<Score>> getClassScores(@PathVariable Long classId,
                                               HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            List<Score> scores = teacherService.getClassScores(teacherId, classId);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 录入/更新学生成绩
     */
    @PostMapping("/score/input")
    public Result<String> inputScore(@Valid @RequestBody ScoreInputRequest request,
                                      HttpServletRequest httpRequest) {
        try {
            Long teacherId = (Long) httpRequest.getAttribute("roleId");
            teacherService.inputScore(teacherId, request);
            return Result.success("成绩录入成功");
        } catch (Exception e) {
            return Result.error("成绩录入失败: " + e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/score/batch")
    public Result<String> batchInputScores(@RequestBody Map<String, Object> params,
                                           HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Long teachingClassId = Long.valueOf(params.get("teachingClassId").toString());
            @SuppressWarnings("unchecked")
            List<ScoreInputRequest> requests = (List<ScoreInputRequest>) params.get("scores");
            teacherService.batchInputScores(teacherId, teachingClassId, requests);
            return Result.success("批量录入成功");
        } catch (Exception e) {
            return Result.error("批量录入失败: " + e.getMessage());
        }
    }

    /**
     * 查询教学班成绩统计
     */
    @GetMapping("/class/{classId}/statistics")
    public Result<ClassScoreStatisticsVO> getClassStatistics(@PathVariable Long classId,
                                                              HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            ClassScoreStatisticsVO statistics = teacherService.getClassStatistics(teacherId, classId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * AI教学分析咨询
     */
    @PostMapping("/ai/consult")
    public Result<String> aiConsult(@Valid @RequestBody AIConsultRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            Long teacherId = (Long) httpRequest.getAttribute("roleId");

            if (request.getTeachingClassId() == null) {
                return Result.error("请指定教学班ID");
            }

            String analysis = teacherService.aiConsult(teacherId, request.getTeachingClassId(), request.getMessage());
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("AI咨询失败: " + e.getMessage());
        }
    }

    /**
     * AI教学分析咨询 - 流式输出
     */
    @GetMapping(value = "/ai/consult/stream", produces = "text/event-stream")
    public SseEmitter aiConsultStream(@RequestParam Long teachingClassId,
                                      @RequestParam String message,
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

            Long teacherId = jwtUtil.getRoleIdFromToken(token);

            // 直接在Controller线程中调用，保持SecurityContext
            aiService.teacherConsultStreamingSync(teacherId, teachingClassId, message, emitter);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
