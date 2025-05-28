package com.venvas.pocamarket.service.pokemon.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포켓몬 도메인에서 발생하는 에러 코드 관리
 */
@Getter
@RequiredArgsConstructor
public enum PokemonErrorCode {
    // 포켓몬 조회 관련 에러
    POKEMON_NOT_FOUND("POKEMON_001", "포켓몬을 찾을 수 없습니다."),
    POKEMON_LIST_EMPTY("POKEMON_002", "포켓몬 목록이 비어있습니다."),
    
    // 포켓몬 등록 관련 에러
    DUPLICATE_POKEMON_NUMBER("POKEMON_101", "이미 존재하는 포켓몬 번호입니다."),
    DUPLICATE_POKEMON_NAME("POKEMON_102", "이미 존재하는 포켓몬 이름입니다."),
    INVALID_POKEMON_TYPE("POKEMON_103", "유효하지 않은 포켓몬 타입입니다."),
    
    // 포켓몬 거래 관련 에러
    POKEMON_NOT_TRADABLE("POKEMON_201", "거래할 수 없는 포켓몬입니다."),
    INSUFFICIENT_POINTS("POKEMON_202", "포인트가 부족하여 거래할 수 없습니다."),

    // 컨트롤러 인자값 검증 에러
    POKEMON_CODE_INVALID("POKEMON_301", "포켓몬 카드 코드 형식이 올바르지 않습니다."),
    POKEMON_PARAM_EMPTY("POKEMON_302", "필수 파라미터가 누락되었습니다."),
    POKEMON_FILE_NAME_INVALID("POKEMON_303", "파일명 형식이 올바르지 않습니다."),

    // 페이징 관련
    POKEMON_PAGE_SIZE_INVALID("POKEMON_310", "페이지 크기는 1-100 사이여야 합니다."),
    POKEMON_PAGE_NUMBER_INVALID("POKEMON_311", "페이지 번호는 0 이상이어야 합니다."),

    // 정렬 관련
    POKEMON_SORT_FIELD_INVALID("POKEMON_320", "지원하지 않는 정렬 필드입니다."),
    POKEMON_SORT_DIRECTION_INVALID("POKEMON_321", "정렬 방향은 asc 또는 desc여야 합니다."),

    // 검색 조건 관련
    POKEMON_SEARCH_KEYWORD_INVALID("POKEMON_330", "검색어는 2글자 이상이어야 합니다."),
    POKEMON_DATE_RANGE_INVALID("POKEMON_331", "날짜 범위가 올바르지 않습니다."),

    // 일반 데이터 형식
    POKEMON_NUMBER_FORMAT_INVALID("POKEMON_350", "숫자 형식이 올바르지 않습니다."),
    POKEMON_DATE_FORMAT_INVALID("POKEMON_351", "날짜 형식이 올바르지 않습니다.(yyyy-MM-dd)"),

    // 포켓몬 json 데이터 업데이트
    READ_JSON_FILE_FAIL("POKEMON_901", "json 파일 읽기에 실패했습니다."),
    JSON_DATA_EMPTY("POKEMON_902", "읽은 json 파일에 데이터가 없습니다"),
    
    // 기타
    UNKNOWN_ERROR("POKEMON_999", "알 수 없는 포켓몬 관련 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
}
