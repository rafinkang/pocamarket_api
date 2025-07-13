package com.venvas.pocamarket.infrastructure.security.oauth2.user;

import java.util.Map;

/**
 * Naver OAuth2 사용자 정보 구현체
 */
public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    private final Map<String, Object> response;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) response.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getNickname() {
        return (String) response.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) response.get("profile_image");
    }
} 