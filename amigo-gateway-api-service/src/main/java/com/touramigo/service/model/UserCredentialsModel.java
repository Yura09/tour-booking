package com.touramigo.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserCredentialsModel {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
