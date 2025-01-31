package com.dev.auth.exception;

import java.time.ZonedDateTime;

public class JWTTokenExpiredException extends RuntimeException {

    public JWTTokenExpiredException(String message) {
        super(message);
    }

    public JWTTokenExpiredException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
