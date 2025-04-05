package com.example.gkys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.gkys")
@EnableScheduling
public class GkysApplication {

    public static void main(String[] args) {
        SpringApplication.run(GkysApplication.class, args);
    }
}
