package com.touramigo.service.exception;

public class BadDataException extends RuntimeException{

    public BadDataException(String message) {
        super(message);
    }
}
