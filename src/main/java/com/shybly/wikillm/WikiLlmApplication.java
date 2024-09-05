package com.shybly.wikillm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WikiLlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikiLlmApplication.class, args);
    }

}
