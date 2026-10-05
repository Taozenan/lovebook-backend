package com.tzn.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tzn.common.mapper")
public class TznShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TznShopApplication.class, args);
    }
}
