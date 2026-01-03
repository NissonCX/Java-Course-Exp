package com.cqu.teacher.client;

import com.cqu.common.dto.ScoreInputRequest;
import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Score Service Feign Client for Teacher Service
 * 教师服务调用成绩服务的接口
 */
@FeignClient(name = "score-service")
public interface ScoreServiceClient {

    /**
     * 录入/更新学生成绩
     * @param request 成绩录入请求
     * @param teacherId 教师ID
     * @param authorization JWT token
     * @return 录入结果
     */
    @PostMapping("/api/score/input")
    Result<String> inputScore(@Valid @RequestBody ScoreInputRequest request,
                             @RequestParam("teacherId") Long teacherId,
                             @RequestHeader("Authorization") String authorization);

    /**
     * 批量录入成绩
     * @param params 批量录入参数
     * @param teacherId 教师ID
     * @param authorization JWT token
     * @return 录入结果
     */
    @PostMapping("/api/score/batch")
    Result<String> batchInputScores(@RequestBody Map<String, Object> params,
                                   @RequestParam("teacherId") Long teacherId,
                                   @RequestHeader("Authorization") String authorization);

    /**
     * 查询教学班学生成绩列表
     * @param classId 教学班ID
     * @param teacherId 教师ID
     * @param authorization JWT token
     * @return 成绩列表
     */
    @GetMapping("/api/score/class/{classId}")
    Result<List<Object>> getClassScores(@PathVariable("classId") Long classId,
                                       @RequestParam(required = false) Long teacherId,
                                       @RequestHeader("Authorization") String authorization);

    /**
     * 查询教学班学生成绩列表（包含学生信息）
     * @param classId 教学班ID
     * @param teacherId 教师ID
     * @param authorization JWT token
     * @return 学生成绩列表
     */
    @GetMapping("/api/score/class/{classId}/students")
    Result<List<Map<String, Object>>> getClassStudentsWithScores(@PathVariable("classId") Long classId,
                                                                 @RequestParam(required = false) Long teacherId,
                                                                 @RequestHeader("Authorization") String authorization);
}