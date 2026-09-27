package com.example.practice_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Practice3Application {

    public static void main(String[] args) {
        SpringApplication.run(Practice3Application.class, args);
    }

}
