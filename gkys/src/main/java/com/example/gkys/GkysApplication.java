package com.example.gkys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.gkys")
public class GkysApplication {

    public static void main(String[] args) {
        SpringApplication.run(GkysApplication.class, args);
    }
}
