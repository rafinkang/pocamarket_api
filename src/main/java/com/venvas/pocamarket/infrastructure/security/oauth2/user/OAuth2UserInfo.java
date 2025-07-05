package com.venvas.pocamarket.infrastructure.security.oauth2.user;

import java.util.Map;

/**
 * OAuth2 사용자 정보 추상화 인터페이스
 * 각 OAuth2 제공자별로 다른 사용자 정보 구조를 통일
 */
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getProviderId();

    public abstract String getProvider();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getNickname();

    public abstract String getProfileImageUrl();
} 