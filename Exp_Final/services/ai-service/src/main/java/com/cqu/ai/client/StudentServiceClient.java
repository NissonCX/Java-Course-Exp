package com.cqu.ai.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * Student Service Feign Client for AI Service
 * AI服务调用学生服务的接口
 */
@FeignClient(name = "student-service")
public interface StudentServiceClient {

    /**
     * 获取学生信息
     * @param studentId 学生ID
     * @param authorization JWT token
     * @return 学生信息
     */
    @GetMapping("/api/student/profile")
    Result<Map<String, Object>> getStudentProfile(@RequestHeader("Authorization") String authorization);
}