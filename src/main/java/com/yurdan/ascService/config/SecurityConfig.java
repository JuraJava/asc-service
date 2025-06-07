//package com.yurdan.ascService.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable() // Отключить CSRF (для POST из Postman, если без формы)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/asc/**").permitAll() // Разрешить доступ к /asc/**
//                        .anyRequest().authenticated()
//                )
//                .httpBasic().disable() // можно включить, если хочешь Basic-авторизацию
//                .formLogin().disable(); // отключаем форму логина, если она не нужна
//
//        return http.build();
//    }
//}