package com.venvas.pocamarket.service.trade.domain.exception;

import com.venvas.pocamarket.service.user.domain.exception.UserErrorCode;
import lombok.Getter;

@Getter
public class TcgCodeException extends RuntimeException {
    private final TcgCodeErrorCode errorCode;

    public TcgCodeException(TcgCodeErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TcgCodeException(TcgCodeErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    // 추가 정보를 포함한 메시지를 생성하는 경우
    public TcgCodeException(TcgCodeErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
    }
}
