package com.touramigo.service.controller.handler;


import com.touramigo.service.exception.AuthException;
import com.touramigo.service.exception.BadDataException;
import com.touramigo.service.exception.CredentialsExpiredException;
import com.touramigo.service.exception.DisabledUserException;
import com.touramigo.service.model.ErrorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.net.ConnectException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthException.class)
    public ErrorResponseModel handleAuthException(final AuthException e) {
        return ErrorResponseModel.forbidden(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadDataException.class)
    public ErrorResponseModel handleBadDataException(final BadDataException e) {
        return ErrorResponseModel.badRequest(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponseModel handleServiceException(final Exception ex) {
        return ErrorResponseModel.internal(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConnectException.class)
    public ErrorResponseModel handleForwardingError(ConnectException ex) {
        return ErrorResponseModel.internal(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(CredentialsExpiredException.class)
    public ErrorResponseModel handleCredentialsExpiredError(CredentialsExpiredException e) {
        return new ErrorResponseModel(HttpStatus.NOT_ACCEPTABLE.value(),e.getMessage());
    }

    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(DisabledUserException.class)
    public ErrorResponseModel handleDisabledUserError(DisabledUserException e){
        return new ErrorResponseModel(HttpStatus.LOCKED.value(), e.getMessage());
    }

}
