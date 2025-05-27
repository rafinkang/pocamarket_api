package com.venvas.pocamarket.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT 관련 설정을 관리하는 클래스
 */
@ConfigurationProperties(prefix = "jwt")
@Component
@Getter
@Setter
public class JwtProperties {
    /**
     * JWT 서명에 사용할 비밀 키
     */
    private String secretKey;
    
    /**
     * 액세스 토큰 유효 기간 (밀리초)
     */
    private long accessTokenValidityInMs = 3600000; // 기본값 1시간
    
    /**
     * 리프레시 토큰 유효 기간 (밀리초)
     */
    private long refreshTokenValidityInMs = 2592000000L; // 기본값 30일
}
