package com.cqu.teacher.client;

import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Course Service Feign Client for Teacher Service
 * 教师服务调用课程服务的接口
 */
@FeignClient(name = "course-service")
public interface CourseServiceClient {

    /**
     * 获取所有可选教学班（教师用于查看任教班级）
     * @param semester 学期（可选）
     * @param authorization JWT token
     * @return 教学班列表
     */
    @GetMapping("/api/course/classes")
    Result<List<Map<String, Object>>> getAvailableClasses(@RequestParam(required = false) String semester,
                                                          @RequestHeader("Authorization") String authorization);

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