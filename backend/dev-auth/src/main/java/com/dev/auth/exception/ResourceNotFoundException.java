package com.dev.auth.exception;

public class ResourceNotFoundException  extends RuntimeException {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}