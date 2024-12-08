package com.example.API_Zalo_OA.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/webhook")
                .allowedOrigins("https://oa.zalo.me")
                .allowedMethods("GET", "POST");
    }
}