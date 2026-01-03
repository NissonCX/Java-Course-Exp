package com.cqu.teacher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.cqu.teacher.mapper")
@ComponentScan(basePackages = {"com.cqu.teacher", "com.cqu.security", "com.cqu.common"})
public class TeacherApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeacherApplication.class, args);
    }
}