package com.cqu.score.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * Student Service Feign Client
 * 调用学生服务获取学生信息
 */
@FeignClient(name = "student-service")
public interface StudentServiceClient {

    /**
     * 根据学生ID获取学生信息
     * @param studentId 学生ID
     * @param authorization JWT token
     * @return 学生信息
     */
    @GetMapping("/api/student/{studentId}")
    Result<Map<String, Object>> getStudentById(@PathVariable("studentId") Long studentId,
                                               @RequestHeader("Authorization") String authorization);
}
