package com.cqu.exp04;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cqu.exp04.mapper")
public class Exp04Application {

    public static void main(String[] args) {
        SpringApplication.run(Exp04Application.class, args);
    }

}
