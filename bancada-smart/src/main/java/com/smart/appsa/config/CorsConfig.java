package com.smart.appsa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final ComunicacaoClpInterceptor comunicacaoClpInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
/* 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(comunicacaoClpInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/smart/start");
    }*/
}
