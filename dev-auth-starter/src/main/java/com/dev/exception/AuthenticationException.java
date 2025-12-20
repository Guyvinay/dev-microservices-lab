package com.dev.exception;


public class AuthenticationException  extends RuntimeException {
    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Exception ex) {
        super(msg, ex);
    }
}