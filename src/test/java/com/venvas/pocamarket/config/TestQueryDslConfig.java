package com.venvas.pocamarket.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestQueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    @Primary
    public JPAQueryFactory testJpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
} 