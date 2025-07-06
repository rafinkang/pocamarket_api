package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto;
import com.venvas.pocamarket.service.user.application.dto.SocialLoginResponse;

/**
 * OAuth2 소셜 로그인 공통 인터페이스
 * 구글, 네이버, 카카오톡 등 다양한 OAuth2 제공자를 지원하기 위한 공통 인터페이스
 */
public interface OAuth2UserService {
    
    /**
     * 지원하는 OAuth2 제공자 타입을 반환
     * 
     * @return OAuth2 제공자 타입 (google, naver, kakao 등)
     */
    String getProviderType();
    
    /**
     * OAuth2 인증 코드를 사용하여 액세스 토큰을 획득
     * 
     * @param code OAuth2 인증 코드
     * @param redirectUri 리다이렉트 URI
     * @return 액세스 토큰
     */
    String getAccessToken(String code, String redirectUri);
    
    /**
     * 액세스 토큰을 사용하여 사용자 정보를 획득
     * 
     * @param accessToken OAuth2 액세스 토큰
     * @return 사용자 정보 DTO
     */
    OAuth2UserInfoDto getUserInfo(String accessToken);
    
    /**
     * 소셜 로그인 처리 (사용자 생성 또는 로그인)
     * 
     * @param userInfo 소셜 로그인 사용자 정보
     * @param ipAddress 클라이언트 IP 주소
     * @param userAgent 클라이언트 User-Agent
     * @return 소셜 로그인 응답 DTO
     */
    SocialLoginResponse processSocialLogin(OAuth2UserInfoDto userInfo, String ipAddress, String userAgent);
} 