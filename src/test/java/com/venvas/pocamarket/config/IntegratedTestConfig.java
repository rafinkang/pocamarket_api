package com.venvas.pocamarket.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.venvas.pocamarket.infrastructure.config.JwtProperties;

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
}
