package com.touramigo.service.exception;

public class UnexistedDataException extends RuntimeException {

    public UnexistedDataException(String message) {
        super(message);
    }
}
