package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponseModel {

    private int statusCode;

    private String message;


    public static ErrorResponseModel internal(String message) {
        return new ErrorResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static ErrorResponseModel notFound(String message) {
        return new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), message);
    }

    public static ErrorResponseModel forbidden(String message) {
        return new ErrorResponseModel(HttpStatus.FORBIDDEN.value(), message);
    }

    public static ErrorResponseModel badRequest(String message) {
        return new ErrorResponseModel(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static ErrorResponseModel conflict(String message) {
        return new ErrorResponseModel(HttpStatus.CONFLICT.value(), message);
    }

    public static ErrorResponseModel unauthorized(String message) {
        return new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
