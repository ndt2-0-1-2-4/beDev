package com.example.doan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.doan.Repository")
@EntityScan(basePackages = "com.example.doan.Model")
@ComponentScan(basePackages = "com.example.doan")
public class DoanApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoanApplication.class, args);
    }
}