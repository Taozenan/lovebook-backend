package com.tzn.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tzn.common.mapper")
public class TznAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(TznAuthApplication.class, args);
    }
}
