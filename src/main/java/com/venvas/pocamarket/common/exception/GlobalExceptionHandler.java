package com.venvas.pocamarket.common.exception;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE) // 도메인별 핸들러가 먼저 처리하도록 낮은 우선순위 설정
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
     * UserException 처리
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<String>> handleUserException(UserException ex) {
        UserErrorCode errorCode = ex.getErrorCode();

        // 에러 코드에 따라 HTTP 상태 코드를 다르게 설정 가능
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (errorCode == UserErrorCode.USER_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        } else if (errorCode == UserErrorCode.INSUFFICIENT_PERMISSION) {
            status = HttpStatus.FORBIDDEN;
        }

        log.error("사용자 관련 예외 발생: {}", ex.getMessage());
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(ex.getMessage(), errorCode.getCode()));
    }

    /**
     * ObjectOptimisticLockingFailureException 처리
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<String>> handleOptimisticLockingException(
            ObjectOptimisticLockingFailureException ex) {
        log.error("낙관적 잠금 예외 발생: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("데이터가 다른 사용자에 의해 변경되었습니다. 다시 시도해주세요.",
                        "OBJECT_OPTIMISTIC_LOCKING_FAILURE_ERROR"));
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("잘못된 인자 예외 발생: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), "ILLEGAL_ARGUMENT_ERROR"));
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