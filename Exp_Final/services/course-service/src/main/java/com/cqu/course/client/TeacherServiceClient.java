package com.cqu.course.client;

import com.cqu.common.entity.Teacher;
import com.cqu.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Teacher Service Feign Client
 * 调用教师服务获取教师信息
 */
@FeignClient(name = "teacher-service")
public interface TeacherServiceClient {

    /**
     * 根据教师ID获取教师信息
     * @param teacherId 教师ID
     * @param authorization JWT token
     * @return 教师信息
     */
    @GetMapping("/api/teacher/{teacherId}")
    Result<Teacher> getTeacherById(@PathVariable("teacherId") Long teacherId,
                                   @RequestHeader("Authorization") String authorization);
}
