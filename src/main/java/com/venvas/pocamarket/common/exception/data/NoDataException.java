package com.venvas.pocamarket.common.exception.data;

public class NoDataException extends ParentException {

    public NoDataException(String message) {
        super(message, "NO_DATA");
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause, "NO_DATA");
    }
}
