package com.venvas.pocamarket.service.pokemon.domain.exception;

import com.venvas.pocamarket.common.util.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 포켓몬 도메인 관련 예외를 처리하는 핸들러
 * 포켓몬 서비스 패키지에서 발생하는 예외를 처리한다
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.venvas.pocamarket.service.pokemon")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PokemonExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("입력값 검증에 실패했습니다.");

        log.error("검증 실패: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    /**
     * PokemonException 처리
     */
    @ExceptionHandler(PokemonException.class)
    public ResponseEntity<ApiResponse<String>> handlePokemonException(PokemonException ex) {
        PokemonErrorCode errorCode = ex.getErrorCode();
        
        // 에러 코드에 따라 HTTP 상태 코드를 다르게 설정 가능
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (errorCode == PokemonErrorCode.POKEMON_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        } else if (errorCode == PokemonErrorCode.INSUFFICIENT_POINTS) {
            status = HttpStatus.PAYMENT_REQUIRED; // 402 Payment Required
        }
        
        log.error("포켓몬 관련 예외 발생: {}, 코드: {}", ex.getMessage(), errorCode.getCode());
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(ex.getMessage(), errorCode.getCode()));
    }
}
