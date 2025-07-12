package com.yurdan.ascService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AscServiceApplication {
    public static void main(String[] args) {

        SpringApplication.run(AscServiceApplication.class, args);

    }
}
