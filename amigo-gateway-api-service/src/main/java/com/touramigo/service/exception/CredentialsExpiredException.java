package com.touramigo.service.exception;

public class CredentialsExpiredException extends RuntimeException {

    public CredentialsExpiredException(String message) {
        super(message);
    }
}
