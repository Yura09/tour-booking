package com.touramigo.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResetPasswordModel {

    @NotNull
    private String password;

    @NotNull
    private String token;
}
