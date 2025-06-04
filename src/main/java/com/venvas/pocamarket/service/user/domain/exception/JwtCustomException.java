package com.venvas.pocamarket.service.user.domain.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class JwtCustomException extends JwtException {

    private final JwtErrorCode errorCode;

    public JwtCustomException(JwtErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public JwtCustomException(Throwable cause, JwtErrorCode code) {
        super(code.getMessage(), cause);
        this.errorCode = code;
    }
}
