package com.venvas.pocamarket.service.trade.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 카드 교환 관련 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum TcgTradeErrorCode {
    // 요청 데이터 관련 에러
    INVALID_REQUEST_DATA("TCG_TRADE_001", "요청 데이터가 유효하지 않습니다."),
    MISSING_REQUIRED_FIELD("TCG_TRADE_002", "필수 입력 항목이 누락되었습니다."),
    INVALID_CARD_CODE_FORMAT("TCG_TRADE_003", "카드 코드 형식이 유효하지 않습니다."),
    INVALID_TCG_CODE_FORMAT("TCG_TRADE_004", "친구 코드 형식이 유효하지 않습니다."),
    INVALID_WANT_CARD_LIST("TCG_TRADE_005", "원하는 카드 목록이 유효하지 않습니다."),
    TOO_MANY_WANT_CARDS("TCG_TRADE_006", "원하는 카드는 최대 10개까지 가능합니다."),
    EMPTY_WANT_CARD_LIST("TCG_TRADE_007", "원하는 카드가 최소 1개 이상 있어야 합니다."),
    
    // 비즈니스 로직 관련 에러
    TRADE_NOT_FOUND("TCG_TRADE_100", "교환 요청을 찾을 수 없습니다."),
    UNAUTHORIZED_TRADE_ACCESS("TCG_TRADE_101", "교환 요청에 대한 권한이 없습니다."),
    TRADE_ALREADY_COMPLETED("TCG_TRADE_102", "이미 완료된 교환 요청입니다."),
    TRADE_ALREADY_CANCELLED("TCG_TRADE_103", "이미 취소된 교환 요청입니다."),
    
    // 시스템 에러
    INTERNAL_SERVER_ERROR("TCG_TRADE_500", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;
} 