package com.example.API_Zalo_OA.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()  // Thay vì authorizeRequests()
                .requestMatchers("/webhook").permitAll()  // Cho phép truy cập vào /webhook mà không cần xác thực
                .anyRequest().permitAll()  // Cho phép tất cả các yêu cầu khác mà không cần xác thực
                .and()
                .csrf().disable();  // Tắt CSRF nếu không cần thiết
        return http.build();
    }
}