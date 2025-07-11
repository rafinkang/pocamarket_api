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
    TOO_MANY_WANT_CARDS("TCG_TRADE_006", "원하는 카드는 최대 3개까지 가능합니다."),
    EMPTY_WANT_CARD_LIST("TCG_TRADE_007", "원하는 카드가 최소 1개 이상 있어야 합니다."),
    INVALID_SEARCH_STATUS("TCG_TRADE_008", "검색 상태 값이 유효하지 않습니다."),
    DUPLICATE_TRADE_REQUEST("TCG_TRADE_009", "중복된 카드 요청 입니다."),
    REFRESH_INTERVAL_ERROR("TCG_TRADE_010", "갱신은 최소 1시간마다 가능합니다."),

    // 비즈니스 로직 관련 에러
    TRADE_NOT_FOUND("TCG_TRADE_100", "교환 요청을 찾을 수 없습니다."),
    UNAUTHORIZED_TRADE_ACCESS("TCG_TRADE_101", "교환 요청에 대한 권한이 없습니다."),
    TRADE_ALREADY_COMPLETED("TCG_TRADE_102", "이미 완료된 교환 요청입니다."),
    TRADE_ALREADY_CANCELLED("TCG_TRADE_103", "이미 취소된 교환 요청입니다."),
    TRADE_ALREADY_PROCESS("TCG_TRADE_104", "이미 진행중인 교환 요청입니다."),
    INVALID_TRADE_STATUS("TCG_TRADE_105", "변경될 교환의 상태 값이 유효하지 않습니다."),
    TRADE_ALREADY_IN_PROGRESS("TCG_TRADE_106", "다른 요청글과 교환 진행 또는 완료 됐습니다."),
    
    TRADE_REQUEST_NOT_FOUND("TCG_TRADE_200", "교환 요청을 찾을 수 없습니다."),
    TRADE_REQUEST_ALREADY_DELETED("TCG_TRADE_201", "이미 삭제된 교환 요청입니다."),
    TRADE_REQUEST_ALREADY_COMPLETED("TCG_TRADE_202", "이미 완료된 교환 요청은 수정할 수 없습니다."),
    TRADE_REQUEST_NOT_EDITABLE("TCG_TRADE_203", "교환 요청을 수정할 수 없는 상태입니다."),
    
    // 시스템 에러
    INTERNAL_SERVER_ERROR("TCG_TRADE_500", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;
} 