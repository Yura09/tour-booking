package com.touramigo.service.controller;

import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final String AUTHORIZATION_HEADER = "Authorization";


    @ApiIgnore
    @GetMapping("/revoke-token")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null) {
            authService.logout(authHeader);
        } else {
            throw new InvalidDataException("Authorization token is not present.");
        }
    }
}
