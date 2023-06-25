package com.touramigo.service.config.auth;

import com.touramigo.service.exception.AuthException;
import com.touramigo.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration implements AuthorizationServerConfigurer {

    private static final String IS_AUTHENTICATED = "isAuthenticated()";

    private static final String PERMIT_ALL = "permitAll()";


    private static final String CHECK_TOKEN_API = "/oauth/check_token";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String ROLE_OPERATOR = "ROLE_OPERATOR";

    private static final String WHITESPACE_SPLITERATOR = " ";

    private static final int SECONDS_600 = 60;


    @Value("${security.oauth2.client.id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    @Value("${security.oauth2.client.scope}")
    private String scopes;

    @Value("${security.oauth2.client.authorized-grant-types}")
    private String grandTypes;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Bean
    TokenStore jdbcTokenStore(RedisConnectionFactory redisConnectionFactory) {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .allowFormAuthenticationForClients()
                .checkTokenAccess(IS_AUTHENTICATED)
                .tokenKeyAccess(PERMIT_ALL)
                .addTokenEndpointAuthenticationFilter(checkTokenEndpointFilter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .authorizedGrantTypes(grandTypes.split(WHITESPACE_SPLITERATOR))
                .authorities(ROLE_ADMIN, ROLE_OPERATOR)
                .scopes(scopes.split(WHITESPACE_SPLITERATOR))
                .secret(secret)
                .accessTokenValiditySeconds(SECONDS_600);
    }

    @Override
    @Transactional
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(jdbcTokenStore(redisConnectionFactory));
        endpoints.userDetailsService(userDetailsService);
        endpoints.authenticationManager(authenticationManager);
        endpoints.exceptionTranslator(e -> {

            // handling spring security credentialsExpired exception
            if (e instanceof InternalAuthenticationServiceException && e.getCause() instanceof CredentialsExpiredException) {
                return new ResponseEntity<OAuth2Exception>(new OAuth2Exception(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);

            } else if (e instanceof InternalAuthenticationServiceException && e.getCause() instanceof DisabledException) {
                // handling spring security disabled user exception
                return new ResponseEntity<OAuth2Exception>(new OAuth2Exception(e.getMessage()), HttpStatus.LOCKED);

            } else if(e instanceof InvalidGrantException) {
                return new ResponseEntity<OAuth2Exception>(new OAuth2Exception(e.getMessage()), HttpStatus.FORBIDDEN);
            }else {
                throw e;
            }
        });
    }


    @Bean
    public ClientCredentialsTokenEndpointFilter checkTokenEndpointFilter() {
        ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(CHECK_TOKEN_API);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAllowOnlyPost(true);
        return filter;
    }
}
