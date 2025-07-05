package com.venvas.pocamarket.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth2 관련 설정 클래스
 * RestTemplate, ObjectMapper 등 OAuth2 처리에 필요한 빈들을 설정
 */
@Configuration
public class OAuth2Config {
    
    /**
     * HTTP 통신을 위한 RestTemplate 빈 생성
     * OAuth2 제공자와의 API 통신에 사용
     * 
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * JSON 파싱을 위한 ObjectMapper 빈 생성
     * OAuth2 응답 데이터 파싱에 사용
     * 
     * @return ObjectMapper 인스턴스
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
} 