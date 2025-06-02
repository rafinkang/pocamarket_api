package com.venvas.pocamarket.service.pokemon.domain.exception;

import lombok.Getter;

/**
 * 포켓몬 도메인에서 발생하는 비즈니스 예외
 */
@Getter
public class PokemonException extends RuntimeException {
    
    /**
     * 에러 코드 엔티티
     */
    private final PokemonErrorCode errorCode;
    
    /**
     * 에러 코드로 예외 생성
     * 
     * @param errorCode 에러 코드 엔티티
     */
    public PokemonException(PokemonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 예외 원인과 함께 예외 생성
     * 
     * @param errorCode 에러 코드 엔티티
     * @param cause 예외 원인
     */
    public PokemonException(PokemonErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 추가 정보를 포함한 메시지로 예외 생성
     * 
     * @param errorCode 에러 코드 엔티티
     * @param additionalMessage 추가 메시지
     */
    public PokemonException(PokemonErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
    }
}
