package com.venvas.pocamarket.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
    // private long accessTokenValidityInMs = 3600000; // 기본값 1시간
    private long accessTokenValidityInMs = 180000; // 테스트용 3분
    
    /**
     * 리프레시 토큰 유효 기간 (밀리초)
     */
    // private long refreshTokenValidityInMs = 2592000000L; // 기본값 30일
    private long refreshTokenValidityInMs = 300000; // 테스트용 5분
}
