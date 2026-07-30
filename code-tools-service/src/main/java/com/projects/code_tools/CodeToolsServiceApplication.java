package com.projects.code_tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeToolsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeToolsServiceApplication.class, args);
        System.out.println("Code Tools Service is up on port 8082");
    }

}
