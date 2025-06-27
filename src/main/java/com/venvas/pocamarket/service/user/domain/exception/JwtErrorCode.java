package com.venvas.pocamarket.service.user.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode {

    // 인증 관련 예러
    FAIL_AUTHENTICATION("JWT_401", "권한 없는 요청"),
    TOKEN_EXPIRED("JWT_402", "토큰 만료"),
    INVALID_INFO("JWT_403", "유효하지 않는 정보입니다.");

    private final String code;
    private final String message;
}
