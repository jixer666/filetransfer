package com.abc.filetransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 设置允许的域名，明确指定前端地址
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8081"));
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("*"));
        // 预检请求的有效期
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}