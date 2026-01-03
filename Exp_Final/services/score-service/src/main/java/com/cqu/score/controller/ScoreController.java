package com.cqu.score.controller;

import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.entity.Score;
import com.cqu.score.service.ScoreService;
import com.cqu.common.vo.ClassScoreStatisticsVO;
import com.cqu.common.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/api/score")
@CrossOrigin
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询学生所有成绩(带统计信息)
     */
    @GetMapping("/student/{studentId}")
    public Result<Map<String, Object>> getStudentScores(@PathVariable Long studentId,
                                                         HttpServletRequest request) {
        try {
            // 验证权限：只有学生本人或授权的教师/管理员可查看
            Map<String, Object> result = scoreService.getStudentScores(studentId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班学生成绩列表
     */
    @GetMapping("/class/{classId}")
    public Result<List<Score>> getClassScores(@PathVariable Long classId,
                                             @RequestParam(required = false) Long teacherId) {
        try {
            List<Score> scores = scoreService.getClassScores(classId, teacherId);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询教学班学生成绩列表（包含学生信息）
     * 用于教师端成绩管理页面
     */
    @GetMapping("/class/{classId}/students")
    public Result<List<Map<String, Object>>> getClassStudentsWithScores(@PathVariable Long classId,
                                                                        @RequestParam(required = false) Long teacherId) {
        try {
            List<Map<String, Object>> students = scoreService.getClassStudentsWithScores(classId, teacherId);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 录入/更新学生成绩
     */
    @PostMapping("/input")
    public Result<String> inputScore(@Valid @RequestBody ScoreInputRequest request,
                                   @RequestParam Long teacherId) {
        try {
            scoreService.inputScore(teacherId, request);
            return Result.success("成绩录入成功");
        } catch (Exception e) {
            return Result.error("成绩录入失败: " + e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/batch")
    public Result<String> batchInputScores(@RequestBody Map<String, Object> params,
                                         @RequestParam Long teacherId) {
        try {
            Long teachingClassId = Long.valueOf(params.get("teachingClassId").toString());
            
            // 手动转换 List<Map> 为 List<ScoreInputRequest>
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> scoresList = (List<Map<String, Object>>) params.get("scores");
            List<ScoreInputRequest> requests = new ArrayList<>();
            
            for (Map<String, Object> scoreMap : scoresList) {
                ScoreInputRequest request = new ScoreInputRequest();
                request.setTeachingClassId(teachingClassId);
                
                if (scoreMap.get("studentId") != null) {
                    request.setStudentId(Long.valueOf(scoreMap.get("studentId").toString()));
                }
                if (scoreMap.get("usualScore") != null) {
                    request.setUsualScore(new BigDecimal(scoreMap.get("usualScore").toString()));
                }
                if (scoreMap.get("midtermScore") != null) {
                    request.setMidtermScore(new BigDecimal(scoreMap.get("midtermScore").toString()));
                }
                if (scoreMap.get("experimentScore") != null) {
                    request.setExperimentScore(new BigDecimal(scoreMap.get("experimentScore").toString()));
                }
                if (scoreMap.get("finalScore") != null) {
                    request.setFinalScore(new BigDecimal(scoreMap.get("finalScore").toString()));
                }
                
                requests.add(request);
            }
            
            scoreService.batchInputScores(teacherId, teachingClassId, requests);
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
                                                            @RequestParam(required = false) Long teacherId) {
        try {
            ClassScoreStatisticsVO statistics = scoreService.getClassStatistics(classId, teacherId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 计算总分和绩点 (手动触发计算)
     */
    @PostMapping("/calculate/{scoreId}")
    public Result<String> calculateScore(@PathVariable Long scoreId) {
        try {
            scoreService.calculateScore(scoreId);
            return Result.success("成绩计算完成");
        } catch (Exception e) {
            return Result.error("成绩计算失败: " + e.getMessage());
        }
    }

    /**
     * 批量计算教学班所有成绩
     */
    @PostMapping("/calculate/class/{classId}")
    public Result<String> calculateClassScores(@PathVariable Long classId) {
        try {
            scoreService.calculateClassScores(classId);
            return Result.success("批量计算完成");
        } catch (Exception e) {
            return Result.error("批量计算失败: " + e.getMessage());
        }
    }
}