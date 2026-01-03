package com.cqu.ai.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * Score Service Feign Client for AI Service
 * AI服务调用成绩服务的接口
 */
@FeignClient(name = "score-service")
public interface ScoreServiceClient {

    /**
     * 获取学生成绩（用于AI分析）
     * @param studentId 学生ID
     * @param authorization JWT token
     * @return 学生成绩数据
     */
    @GetMapping("/api/score/student/{studentId}")
    Result<Map<String, Object>> getStudentScores(@PathVariable("studentId") Long studentId,
                                                  @RequestHeader("Authorization") String authorization);
}