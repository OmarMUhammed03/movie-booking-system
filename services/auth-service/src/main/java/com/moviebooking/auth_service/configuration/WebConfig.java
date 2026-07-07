package com.moviebooking.auth_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
<<<<<<< Updated upstream
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
=======
                .allowedOrigins("http://localhost:3000" , "http://localhost:4200")// Your Angular URL
>>>>>>> Stashed changes
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}