package com.venvas.pocamarket.common.exception.data;

public class NoDataException extends ParentException {

    public NoDataException(String message) {
        super(message, "102");
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause, "102");
    }
}
