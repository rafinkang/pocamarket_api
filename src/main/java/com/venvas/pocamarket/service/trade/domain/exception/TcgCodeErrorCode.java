package com.venvas.pocamarket.service.trade.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TcgCodeErrorCode {
    // 일반적인 사용자 관련 에러
    TCG_CODE_NOT_FOUND("TCG_CODE_001", "친구 코드를 찾을 수 없습니다."),
    TCG_CODE_NOT_EQUALS("TCG_CODE_002", "수정 친구 코드의 ID 값이 같지 않습니다."),
    TCG_CODE_MAX_COUNT_OVER("TCG_CODE_003", "친구 코드 최대 생성 개수를 초과하였습니다."),
    // 권한 관련 에러
    INSUFFICIENT_PERMISSION("TCG_CODE_100", "해당 작업을 수행할 권한이 없습니다.");

    private final String code;
    private final String message;
}
