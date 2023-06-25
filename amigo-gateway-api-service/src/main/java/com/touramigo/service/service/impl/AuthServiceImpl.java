package com.touramigo.service.service.impl;

import com.touramigo.service.client.UsersClient;
import com.touramigo.service.exception.AuthException;
import com.touramigo.service.exception.CredentialsExpiredException;
import com.touramigo.service.exception.DisabledUserException;
import com.touramigo.service.exception.ErrorMessages;
import com.touramigo.service.model.UserCredentialsModel;
import com.touramigo.service.service.AuthService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

@Service
public class AuthServiceImpl implements AuthService {


    @Value("${security.oauth2.client.access-token-uri}")
    private String tokenUrl;

    @Value("${security.oauth2.client.id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    @Value("${security.oauth2.client.scope}")
    private String scopes;

    @Value("${security.oauth2.client.authorized-grant-types}")
    private String grantType;

    @Value("${auth-service.path}")
    private String authServerPath;

    @Autowired
    private UsersClient usersClient;

    @Override
    public OAuth2AccessToken login(UserCredentialsModel userCredentialsModel) {
        byte[] encodedBytes = Base64Utils.encode((clientId + ":" + secret).getBytes());
        String authHeader = "Basic " + new String(encodedBytes);
        try {
            return usersClient.login(authHeader, grantType, userCredentialsModel.getEmail(), userCredentialsModel.getPassword(), "application/x-www-form-urlencoded");
        } catch (FeignException e) {

            // handling spring security credentialsExpired exception
            if(e.status() == HttpStatus.NOT_ACCEPTABLE.value()) {
                throw new CredentialsExpiredException(ErrorMessages.CREDENTIALS_EXPIRED.getMessage());
            } else if(e.status() == HttpStatus.LOCKED.value()) {
                //handling spring security user disabled exception

                throw new DisabledUserException(ErrorMessages.DISABLED_USER.getMessage());
            }else if (e.status() == HttpStatus.FORBIDDEN.value()) {
                throw new AuthException(ErrorMessages.BAD_CREDENTIALS.getMessage());
            }
            throw new AuthException(e.getMessage());
        }
    }

}
