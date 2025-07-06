package com.venvas.pocamarket.infrastructure.security.oauth2.user;

import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;

import java.util.Map;

/**
 * OAuth2 사용자 정보 팩토리
 * 제공자별로 적절한 OAuth2UserInfo 구현체를 생성
 */
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "naver":
                return new NaverOAuth2UserInfo(attributes);
            default:
                throw new UserException(UserErrorCode.UNKNOWN_ERROR, 
                        "지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }
} 