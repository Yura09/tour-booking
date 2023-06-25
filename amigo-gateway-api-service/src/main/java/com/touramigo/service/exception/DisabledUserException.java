package com.touramigo.service.exception;

public class DisabledUserException extends RuntimeException {

    public DisabledUserException(String message) {
        super(message);
    }
}
