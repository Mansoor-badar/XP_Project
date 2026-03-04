package com.codeshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeShieldApplication {
    public static void main(String[] args) {
        // this starts the Web Server (Tomcat) on port 8080
        SpringApplication.run(CodeShieldApplication.class, args);
    }
}
