package com.touramigo.service.controller.handler;

import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnauthorizedException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.model.ErrorResponseModel;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.lang.String.format;

@Slf4j
@RestControllerAdvice
public class UsersServiceApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponseModel handleServiceException(final Exception ex) {
        toLog(ex);
        return ErrorResponseModel.internal(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnexistedDataException.class)
    public ErrorResponseModel handleEmptyResultDataAccessException(final UnexistedDataException ex) {
        toLog(ex);
        return ErrorResponseModel.notFound(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDataException.class)
    public ErrorResponseModel handleInvalidDataEx(final InvalidDataException ex) {
        toLog(ex);
        return ErrorResponseModel.badRequest(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponseModel handleAccessDeniedException(final AccessDeniedException ex) {
        toLog(ex);
        return ErrorResponseModel.forbidden(UserServiceErrorMessages.ACCESS_DENIED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponseModel handleUserNotFound(final UsernameNotFoundException ex) {
        toLog(ex);
        return ErrorResponseModel.forbidden(ex.getMessage());
    }

/*    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ErrorResponseModel handleInternalAuthenticationServiceException(final InternalAuthenticationServiceException ex) {
        toLog(ex);
        return ErrorResponseModel.forbidden("Account disabled please contact the Administrator");
    }*/

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponseModel handleMissingReqParamException(final MissingServletRequestParameterException ex) {
        toLog(ex);
        return ErrorResponseModel.badRequest(format("Required %s parameter is missing", ex.getParameterName()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponseModel handleConstraintException(final DataIntegrityViolationException ex) {
        toLog(ex);
        return ErrorResponseModel.conflict(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FeignException.class)
    public ErrorResponseModel handleFeignException(final FeignException ex){
        toLog(ex);
        return ErrorResponseModel.internal(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponseModel handleUnauthorizedException(final UnauthorizedException ex){
        return ErrorResponseModel.unauthorized(ex.getMessage());
    }

    private void toLog(final Exception ex) {
        log.error("Exception handled:", ex);
    }

}

