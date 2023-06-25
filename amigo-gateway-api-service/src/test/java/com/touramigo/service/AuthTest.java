//package com.touramigo.service;
//
//import com.touramigo.service.model.UserCredentialsModel;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.springframework.http.*;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class AuthTest {
//
//
//    private static final String LOGIN_URL = "/api/login";
//    private static final String AMIGO_ADMIN_USERNAME = "amigo-admin";
//    private static final String AMIGO_ADMIN_PASSWORD = "amigo-admin123";
//    private static final String AUTHORIZATION = "Authorization";
//    private static final String BEARER_PREFIX = "Bearer ";
//    private static final String USERS_URL = "/users/api/users";
//    private static final String REVOKE_TOKEN_URL = "/api/revoke-token";
//    private static final String UNAUTHORIZED = "401 Unauthorized";
//    private static String authToken;
//
//
//    private static final String URL_TO_STAGING_SERVER = "http://gaia.beta.touramigo.com";
//
//    @Test
//    public void aTestAuthLogin() {
//        RestTemplate restTemplate = new RestTemplate();
//
//        UserCredentialsModel userCredentialsModel = new UserCredentialsModel();
//        userCredentialsModel.setUsername(AMIGO_ADMIN_USERNAME);
//        userCredentialsModel.setPassword(AMIGO_ADMIN_PASSWORD);
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        ResponseEntity<OAuth2AccessToken> result = restTemplate.exchange(URL_TO_STAGING_SERVER+ LOGIN_URL,
//                HttpMethod.POST, new HttpEntity(userCredentialsModel, httpHeaders), OAuth2AccessToken.class);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertNotNull(result.getBody().getValue());
//
//        authToken = result.getBody().getValue();
//    }
//
//
//    @Test
//    public void bTestAccessBeforeLogout() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(AUTHORIZATION, BEARER_PREFIX +authToken);
//
//        ResponseEntity<String> result = restTemplate.exchange(URL_TO_STAGING_SERVER+ USERS_URL,
//                HttpMethod.GET, new HttpEntity(httpHeaders), String.class);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//    }
//
//    @Test
//    public void cTestLogout() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(AUTHORIZATION, BEARER_PREFIX+authToken);
//
//        ResponseEntity<String> result = restTemplate.exchange(URL_TO_STAGING_SERVER+ REVOKE_TOKEN_URL,
//                HttpMethod.POST, new HttpEntity(httpHeaders), String.class);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//    }
//
//    @Test
//    public void dTestAccessAfterLogout() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(AUTHORIZATION, BEARER_PREFIX+authToken);
//        ResponseEntity<String> result = null;
//        try {
//            result = restTemplate.exchange(URL_TO_STAGING_SERVER+USERS_URL,
//                    HttpMethod.GET, new HttpEntity(httpHeaders), String.class);
//        } catch (HttpClientErrorException e) {
//            assertEquals(e.getMessage(), UNAUTHORIZED);
//        }
//    }
//
//
//}
