package com.venvas.pocamarket.common.exception.data;

import lombok.Getter;

public class ParentException extends RuntimeException {

    @Getter
    private String errorCode;

    public ParentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ParentException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
