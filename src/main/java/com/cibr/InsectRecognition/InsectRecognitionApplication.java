package com.cibr.InsectRecognition;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cibr.InsectRecognition.dao")
public class InsectRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsectRecognitionApplication.class, args);
    }

}
