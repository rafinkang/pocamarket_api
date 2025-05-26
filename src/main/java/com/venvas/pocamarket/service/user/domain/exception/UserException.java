package com.venvas.pocamarket.service.user.domain.exception;

import com.venvas.pocamarket.common.exception.data.ParentException;

public class UserException extends ParentException {
    public UserException(String message, Throwable cause, String errCode) {
        super(message, cause, errCode);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause, "USER_001");
    }

}
