package com.touramigo.service.client;

import com.touramigo.service.model.LoggedUserInfoModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "amigo-users-service")
public interface UsersClient {

    String AUTHORIZATION_HEADER = "Authorization";

    @RequestMapping(value = "/auth/revoke-token", method = RequestMethod.GET)
    String logout(@RequestHeader(AUTHORIZATION_HEADER) String token);

    @RequestMapping(value = "/users/current", method = RequestMethod.GET)
    LoggedUserInfoModel getLoggedUserInfo (@RequestHeader(AUTHORIZATION_HEADER) String token);

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    OAuth2AccessToken refreshToken(@RequestParam("grant_type") String grantType,
                                   @RequestParam("refresh_token") String refresh_token,
                                   @RequestHeader(AUTHORIZATION_HEADER) String clientIdAndClientSecretEncoded);

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    OAuth2AccessToken login(@RequestHeader(AUTHORIZATION_HEADER) String clientIdAndClientSecretEncoded,
                                            @RequestParam("grant_type") String grant_type,
                                            @RequestParam("username") String username,
                                            @RequestParam("password") String password,
                                            @RequestHeader("Content-Type") String content);


}
