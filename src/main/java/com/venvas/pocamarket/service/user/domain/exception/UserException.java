package com.venvas.pocamarket.service.user.domain.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;
    
    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public UserException(UserErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    // 추가 정보를 포함한 메시지를 생성하는 경우
    public UserException(UserErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
    }
}