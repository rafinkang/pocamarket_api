package com.venvas.pocamarket.common.exception.data;

public class JsonParsingException extends ParentException {

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause, "JSON_PARSING_FAIL");
    }
}
