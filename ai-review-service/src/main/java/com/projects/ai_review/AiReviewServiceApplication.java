package com.projects.ai_review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiReviewServiceApplication.class, args);
        System.out.println("AI Review Service is up on port 8081");
    }

}
