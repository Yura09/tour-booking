package com.touramigo.service.controller;

import com.touramigo.service.client.UsersClient;
import com.touramigo.service.exception.AuthException;
import com.touramigo.service.exception.BadDataException;
import com.touramigo.service.model.LoggedUserInfoModel;
import com.touramigo.service.model.UserCredentialsModel;
import com.touramigo.service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping("/api")
public class AuthController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private UsersClient usersClient;

    @Autowired
    private AuthService authService;

    @Value("${security.oauth2.client.id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    @PostMapping("/login")
    private LoggedUserInfoModel login(@RequestBody @Valid UserCredentialsModel userCredentialsModel) {
        OAuth2AccessToken token = authService.login(userCredentialsModel);
        try {
            LoggedUserInfoModel loggedUserInfo = usersClient.getLoggedUserInfo("Bearer " + token.getValue());
            loggedUserInfo.setTokenInfo(token);
            return loggedUserInfo;
        }catch (Exception e) {
            throw new AuthException(e.getCause().getMessage());
        }
    }

    @PostMapping("/revoke-token")
    @ResponseStatus(HttpStatus.OK)
    private void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && !authHeader.equals("")) {
            try{
                usersClient.logout(authHeader);
            } catch (Exception e) {
                throw new AuthException(e.getMessage());
            }

        } else {
            throw new BadDataException("Authorization token does not exists");
        }
    }


    @PostMapping("/refresh-token")
    private OAuth2AccessToken refreshToken(HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION_HEADER);

        if(token != null && !token.equals("")) {
            final String refreshToken = token.replace("Bearer ", "");
            byte[] encodedBytes = Base64Utils.encode((clientId + ":" + secret).getBytes());
            String authHeader = "Basic " + new String(encodedBytes);
            try {
                return usersClient.refreshToken("refresh_token", refreshToken, authHeader);
            }catch (Exception e) {
                throw new AuthException(e.getCause().getMessage());
            }
        } else {
            throw new BadDataException("Authorization token does not exists");
        }
    }

}
