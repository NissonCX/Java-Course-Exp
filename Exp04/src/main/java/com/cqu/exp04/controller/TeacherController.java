package com.cqu.exp04.controller;

import com.cqu.exp04.dto.AIConsultRequest;
import com.cqu.exp04.dto.ScoreInputRequest;
import com.cqu.exp04.entity.Score;
import com.cqu.exp04.entity.Teacher;
import com.cqu.exp04.entity.TeachingClass;
import com.cqu.exp04.service.TeacherService;
import com.cqu.exp04.vo.ClassScoreStatisticsVO;
import com.cqu.exp04.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Teacher> getProfile(HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            Teacher teacher = teacherService.getById(teacherId);
            return Result.success(teacher);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Teacher teacher,
                                        HttpServletRequest request) {
        try {
            Long teacherId = (Long) request.getAttribute("roleId");
            teacherService.updateProfile(teacherId, teacher);
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
}
