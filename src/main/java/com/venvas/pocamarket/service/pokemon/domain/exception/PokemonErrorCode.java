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
    
    // 기타
    UNKNOWN_ERROR("POKEMON_999", "알 수 없는 포켓몬 관련 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
}
