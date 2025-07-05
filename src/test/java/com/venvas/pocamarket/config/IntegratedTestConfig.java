package com.venvas.pocamarket.config;

import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import com.venvas.pocamarket.service.user.application.dto.OAuth2UserInfoDto;
import com.venvas.pocamarket.service.user.domain.entity.SocialUser;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import com.venvas.pocamarket.service.user.domain.enums.UserStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 통합 테스트 설정
 * - H2 인메모리 DB 사용
 * - 시큐리티 비활성화
 * - main의 빈들 사용 가능
 */
@TestConfiguration
@Profile("test")
public class IntegratedTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 테스트용 JPAQueryFactory
     */
    @Bean
    @Primary
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    /**
     * 테스트용 JWT 설정
     */
    @Bean
    @Primary
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
        jwtProperties.setAccessTokenValidityInMs(3600000L);
        jwtProperties.setRefreshTokenValidityInMs(86400000L);
        return jwtProperties;
    }

    // ================== 테스트용 RestTemplate Mock ==================
    
    /**
     * 테스트용 RestTemplate Bean
     * 외부 API 호출을 위한 Mock (OAuth2 API 호출 등)
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // ================== 테스트 유틸리티 메서드들 ==================

    /**
     * 테스트용 사용자 생성
     */
    public static User createTestUser(String uuid, String loginId, String nickname, String email) {
        User user = User.builder()
                .uuid(uuid)
                .loginId(loginId)
                .nickname(nickname)
                .email(email)
                .password("test123")
                .emailVerified(true)
                .build();
        
        user.setStatus(UserStatus.ACTIVE);
        user.setGrade(UserGrade.LV01);
        
        return user;
    }

    /**
     * 테스트용 소셜 사용자 생성
     */
    public static SocialUser createTestSocialUser(String uuid, String provider, String providerId) {
        // OAuth2UserInfoDto 생성
        OAuth2UserInfoDto userInfoDto = OAuth2UserInfoDto.builder()
                .provider(provider)
                .providerId(providerId)
                .email("test@example.com")
                .name("테스트 사용자")
                .nickname("테스트닉네임")
                .profileImageUrl("https://example.com/profile.jpg")
                .emailVerified(true)
                .build();
        
        // SocialUser 생성
        return SocialUser.createFromOAuth2UserInfo(uuid, userInfoDto);
    }

    /**
     * 테스트용 OAuth2 사용자 생성
     */
    public static User createTestOAuth2User(String provider) {
        String uuid = "test-uuid-" + System.currentTimeMillis();
        String loginId = provider + "_test_user";
        String nickname = "테스트사용자";
        String email = "test@" + provider + ".com";
        
        return createTestUser(uuid, loginId, nickname, email);
    }
}
