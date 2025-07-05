package com.venvas.pocamarket.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 기본 테스트 설정을 위한 메타 어노테이션
 * - SpringBootTest로 전체 컨텍스트 로드
 * - test 프로필 활성화
 * - 트랜잭션 적용 (테스트 후 롤백)
 * - 통합 테스트 설정 import
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
@Import(IntegratedTestConfig.class)
public @interface BaseTestAnnotations {
} 