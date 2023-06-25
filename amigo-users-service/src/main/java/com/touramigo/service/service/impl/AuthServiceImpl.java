package com.touramigo.service.service.impl;

import com.touramigo.service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private TokenStore tokenStore;

    private static final String BEARER_PREFIX = "Bearer";

    @Override
    public void logout(String token) {
        log.info(String.format("Auth: Removing token %s from TokenStore", token));
        String tokenValue = token.replace(BEARER_PREFIX, "").trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if(accessToken !=null) {
            tokenStore.removeAccessToken(accessToken);
            log.info(String.format("Auth: Token %s successfully removed from TokenStore", token));
        } else {
            log.info(String.format("Auth: Token %s has been already removed from TokenStore", token));
        }
    }
}
