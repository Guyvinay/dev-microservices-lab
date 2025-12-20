package com.dev.exception;

import com.nimbusds.jose.JOSEException;

public class JWTTokenException extends JOSEException {

    public JWTTokenException(String message) {
        super(message);
    }

    public JWTTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
