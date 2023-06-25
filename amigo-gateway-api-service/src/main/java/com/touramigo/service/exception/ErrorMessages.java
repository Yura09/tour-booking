package com.touramigo.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessages {

    CREDENTIALS_EXPIRED("Admin forced you to change password, please request a new password."),
    DISABLED_USER("Your account is disabled. Please contact an administrator."),
    BAD_CREDENTIALS("Bad credentials.");

    private String message;


}
