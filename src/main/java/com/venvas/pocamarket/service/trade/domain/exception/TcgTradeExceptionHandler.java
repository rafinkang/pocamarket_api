package com.venvas.pocamarket.service.trade.domain.exception;

import com.venvas.pocamarket.common.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 카드 교환 도메인 관련 예외를 처리하는 핸들러
 * trade 서비스 패키지에서 발생하는 예외를 처리한다
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.venvas.pocamarket.service.trade")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TcgTradeExceptionHandler {

    /**
     * TcgTradeException 처리
     */
    @ExceptionHandler(TcgTradeException.class)
    public ResponseEntity<ApiResponse<String>> handleTcgTradeException(TcgTradeException ex) {
        TcgTradeErrorCode errorCode = ex.getErrorCode();
        
        // 에러 코드에 따라 HTTP 상태 코드를 다르게 설정 가능
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (errorCode == TcgTradeErrorCode.TRADE_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        } else if (errorCode == TcgTradeErrorCode.UNAUTHORIZED_TRADE_ACCESS) {
            status = HttpStatus.FORBIDDEN;
        } else if (errorCode == TcgTradeErrorCode.INTERNAL_SERVER_ERROR) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        log.error("카드 교환 관련 예외 발생: {}, 코드: {}", ex.getMessage(), errorCode.getCode());
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(ex.getMessage(), errorCode.getCode()));
    }

    /**
     * TcgCodeException 처리 (기존 TcgCode 예외도 함께 처리)
     */
    @ExceptionHandler(TcgCodeException.class)
    public ResponseEntity<ApiResponse<String>> handleTcgCodeException(TcgCodeException ex) {
        TcgCodeErrorCode errorCode = ex.getErrorCode();
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (errorCode == TcgCodeErrorCode.TCG_CODE_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        } else if (errorCode == TcgCodeErrorCode.INSUFFICIENT_PERMISSION) {
            status = HttpStatus.FORBIDDEN;
        }
        
        log.error("친구 코드 관련 예외 발생: {}, 코드: {}", ex.getMessage(), errorCode.getCode());
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(ex.getMessage(), errorCode.getCode()));
    }
} 