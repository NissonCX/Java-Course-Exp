package com.cqu.score.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * Course Service Feign Client
 * 调用课程服务获取课程和教学班信息
 */
@FeignClient(name = "course-service")
public interface CourseServiceClient {

    /**
     * 获取教学班详情
     * @param classId 教学班ID
     * @param authorization JWT token
     * @return 教学班详情
     */
    @GetMapping("/api/course/class/{classId}")
    Result<Map<String, Object>> getClassDetail(@PathVariable("classId") Long classId,
                                              @RequestHeader("Authorization") String authorization);
}
