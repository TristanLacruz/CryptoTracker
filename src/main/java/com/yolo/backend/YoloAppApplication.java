package com.yolo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
@EnableCaching
public class YoloAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoloAppApplication.class, args);
	}
}
