package com.tzn.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tzn.system", "com.tzn.common"})
@MapperScan("com.tzn.system.**.mapper")
public class TznSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(TznSystemApplication.class, args);
  }
}
