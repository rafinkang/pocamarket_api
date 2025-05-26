package com.venvas.pocamarket.common.exception;

import com.venvas.pocamarket.common.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        log.warn("Validation 실패: {}", errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }


    /**
     * 기타 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        log.error("예상치 못한 예외 발생: ", ex);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR"));
    }
} 