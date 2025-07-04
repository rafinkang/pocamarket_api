package com.venvas.pocamarket.service.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuth2 소셜 로그인 사용자 정보 DTO
 * 다양한 OAuth2 제공자(구글, 네이버, 카카오톡 등)의 사용자 정보를 통합적으로 관리
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfoDto {
    
    /**
     * OAuth2 제공자 타입 (google, naver, kakao 등)
     */
    private String provider;
    
    /**
     * OAuth2 제공자에서 제공하는 사용자 고유 ID
     */
    private String providerId;
    
    /**
     * 사용자 이메일
     */
    private String email;
    
    /**
     * 사용자 이름
     */
    private String name;
    
    /**
     * 사용자 닉네임 (제공자별로 다를 수 있음)
     */
    private String nickname;
    
    /**
     * 프로필 이미지 URL
     */
    private String profileImageUrl;
    
    /**
     * 이메일 인증 여부
     */
    private Boolean emailVerified;
    
    /**
     * 소셜 로그인 제공자에서 제공하는 추가 정보
     * JSON 형태로 저장하여 제공자별 특별한 정보를 보관
     */
    private String rawData;
    
    /**
     * 소셜 로그인 고유 식별자 생성
     * provider + providerId 조합으로 유니크한 식별자 생성
     * 
     * @return 소셜 로그인 고유 식별자
     */
    public String getSocialLoginId() {
        return provider + "_" + providerId;
    }
    
    /**
     * 이메일 인증 여부를 안전하게 반환
     * null인 경우 false 반환
     * 
     * @return 이메일 인증 여부
     */
    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }
} 