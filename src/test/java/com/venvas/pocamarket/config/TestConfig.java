package com.venvas.pocamarket.config;

import com.venvas.pocamarket.infrastructure.config.JwtProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.venvas.pocamarket")
public class TestConfig {

    @Bean
    @Primary
    public JwtProperties testJwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
        jwtProperties.setAccessTokenValidityInMs(3600000L);
        jwtProperties.setRefreshTokenValidityInMs(86400000L);
        return jwtProperties;
    }
}