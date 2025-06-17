package com.venvas.pocamarket.service.trade.domain.exception;

import lombok.Getter;

/**
 * 카드 교환 관련 커스텀 예외
 */
@Getter
public class TcgTradeException extends RuntimeException {
    private final TcgTradeErrorCode errorCode;

    public TcgTradeException(TcgTradeErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TcgTradeException(TcgTradeErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public TcgTradeException(TcgTradeErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
    }
} 