package com.venvas.pocamarket.service.user.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    // 일반적인 사용자 관련 에러
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID("USER_002", "이미 사용 중인 로그인 ID입니다."),
    DUPLICATE_EMAIL("USER_003", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD("USER_004", "비밀번호가 올바르지 않습니다."),
    ACCOUNT_LOCKED("USER_005", "계정이 잠겨 있습니다."),
    INVALID_LOGIN_ID_FORMAT("USER_006", "로그인 ID 형식이 올바르지 않습니다."),
    INVALID_NICKNAME("USER_007", "사용할 수 없는 닉네임입니다."),
    EMAIL_REQUIRED("USER_008", "이메일은 필수 입니다."),
    CURRENT_PASSWORD_REQUIRED("USER_009", "현재 비밀번호를 입력해야 합니다."),
    
    // 권한 관련 에러
    INSUFFICIENT_PERMISSION("USER_101", "해당 작업을 수행할 권한이 없습니다."),
    
    // 기타
    UNKNOWN_ERROR("USER_999", "알 수 없는 사용자 관련 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
}