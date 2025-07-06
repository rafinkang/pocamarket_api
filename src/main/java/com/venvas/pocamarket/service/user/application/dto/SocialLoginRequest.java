package com.venvas.pocamarket.service.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 소셜 로그인 요청 DTO
 * OAuth2 인증 코드를 통한 소셜 로그인 처리를 위한 요청 데이터
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    
    /**
     * OAuth2 제공자 타입 (google, naver, kakao 등)
     */
    @NotBlank(message = "OAuth2 제공자는 필수입니다.")
    private String provider;
    
    /**
     * OAuth2 인증 코드
     */
    @NotBlank(message = "OAuth2 인증 코드는 필수입니다.")
    private String code;
    
    /**
     * OAuth2 리다이렉트 URI
     */
    @NotBlank(message = "리다이렉트 URI는 필수입니다.")
    private String redirectUri;
    
    /**
     * 클라이언트 정보 (자동 설정)
     */
    private String ipAddress;
    private String userAgent;
} 