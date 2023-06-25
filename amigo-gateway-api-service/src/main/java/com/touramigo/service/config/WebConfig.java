package com.touramigo.service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@Order(0)
public class WebConfig extends WebSecurityConfigurerAdapter {


    @Value("${auth-service.path}")
    private String authServerPath;

    @Value("${security.oauth2.client.id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

    private static final String CHECK_TOKEN_PATH = "/oauth/check_token";

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                /*.antMatchers("/api/revoke-token").permitAll()
                .antMatchers("/api/refresh-token").permitAll()*/
                .antMatchers("/swagger-ui.html","/swagger-resources/**","/v2/api-docs","/configuration/ui","/configuration/security").permitAll()
                //.antMatchers("/api/auth/**").permitAll()
                .antMatchers("/info", "/health", "/metrics").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }

    @Primary
    @Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(
                authServerPath+CHECK_TOKEN_PATH);
        tokenService.setClientId(clientId);
        tokenService.setClientSecret(secret);
        return tokenService;
    }
}
