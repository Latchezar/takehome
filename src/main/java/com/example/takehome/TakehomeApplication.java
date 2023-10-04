package com.example.takehome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableJpaAuditing
@EnableTransactionManagement
@SpringBootApplication
public class TakehomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakehomeApplication.class, args);
    }

}
