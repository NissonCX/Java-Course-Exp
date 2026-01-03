package com.cqu.ai.controller;

import com.cqu.common.dto.AIConsultRequest;
import com.cqu.ai.service.AIService;
import com.cqu.common.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI咨询控制器
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 学生学业咨询
     */
    @PostMapping("/student/consult")
    public Result<String> studentConsult(@Valid @RequestBody AIConsultRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            Long studentId = (Long) httpRequest.getAttribute("roleId");
            String userRole = (String) httpRequest.getAttribute("userRole");

            // 验证权限
            if (!"STUDENT".equals(userRole)) {
                return Result.error("只有学生可以使用学业咨询功能");
            }

            String response = aiService.studentConsult(studentId, request.getMessage());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("咨询失败: " + e.getMessage());
        }
    }

    /**
     * 教师教学咨询
     */
    @PostMapping("/teacher/consult")
    public Result<String> teacherConsult(@Valid @RequestBody AIConsultRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            Long teacherId = (Long) httpRequest.getAttribute("roleId");
            String userRole = (String) httpRequest.getAttribute("userRole");

            // 验证权限
            if (!"TEACHER".equals(userRole)) {
                return Result.error("只有教师可以使用教学咨询功能");
            }

            String response = aiService.teacherConsult(teacherId, request.getMessage());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("咨询失败: " + e.getMessage());
        }
    }

    /**
     * 通用AI咨询 (不需要特殊权限)
     */
    @PostMapping("/general/consult")
    public Result<String> generalConsult(@Valid @RequestBody AIConsultRequest request) {
        try {
            String response = aiService.generalConsult(request.getMessage());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("咨询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("AI服务运行正常");
    }
}