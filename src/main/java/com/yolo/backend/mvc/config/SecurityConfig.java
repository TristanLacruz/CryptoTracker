package com.yolo.backend.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desactiva CSRF para pruebas
            .authorizeHttpRequests()
                .requestMatchers("/api/**").authenticated() // Protege las rutas /api/**
                .anyRequest().permitAll()
            .and()
            .httpBasic(); // Usa Basic Auth

        return http.build();
    }
}
