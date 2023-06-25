package com.touramigo.service.service;

import com.touramigo.service.model.UserCredentialsModel;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface AuthService {

    OAuth2AccessToken login(UserCredentialsModel userCredentialsModel);
}
