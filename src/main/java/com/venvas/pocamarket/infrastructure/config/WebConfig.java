package com.venvas.pocamarket.infrastructure.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Spring MVC의 일반적인 웹 요청에 대한 CORS 설정을 담당, SecurityConfig 에서 cors 설정 적용 중
// @Configuration
 public class WebConfig implements WebMvcConfigurer {

     @Override
     public void addCorsMappings(@NonNull CorsRegistry registry) {
         registry.addMapping("/api/**") // CORS를 적용할 경로 패턴
                 .allowedOrigins("http://localhost:3000") // 허용할 프론트엔드 출처
                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // 허용할 HTTP 메서드
                 .allowedHeaders("*") // 모든 헤더 허용 (필요에 따라 특정 헤더만 명시 가능)
                 .allowCredentials(true) // 쿠키나 인증 헤더를 포함한 요청을 허용할 경우 true
                 .maxAge(3600); // preflight 요청 결과를 캐시할 시간 (초 단위)
     }
 }
