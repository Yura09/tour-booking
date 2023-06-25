package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordEmailRequestModel implements Serializable {

    private String sendTo;
    private String name;
    private String token;

}
