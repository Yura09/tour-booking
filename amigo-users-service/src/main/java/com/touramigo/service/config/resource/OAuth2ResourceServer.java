package com.touramigo.service.config.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/oauth/authorize").permitAll()
                .antMatchers("/oauth/check_token").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/reset-password/**").permitAll()
                .antMatchers("/info", "/health", "/metrics").permitAll()
                .antMatchers("/auth/revoke-token").permitAll()
                .antMatchers("/users/**").authenticated()
                .antMatchers("/partners/**").authenticated()
                .antMatchers("/suppliers/**").authenticated()
                .antMatchers("/permissions/**").authenticated()
                .antMatchers("/account-managers/**").authenticated()
                .antMatchers("/products/**").authenticated()
                .antMatchers("/roles/**").authenticated()
                .antMatchers("/internal/**").permitAll()
                .antMatchers("/").permitAll()
                .and().csrf().disable();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
