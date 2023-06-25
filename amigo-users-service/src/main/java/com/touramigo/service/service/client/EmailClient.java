package com.touramigo.service.service.client;


import com.touramigo.service.model.EmailResponseModel;
import com.touramigo.service.model.ForgotPasswordEmailRequestModel;
import com.touramigo.service.model.ResetPasswordEmailRequestModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("amigo-email-service")
public interface EmailClient {

    @RequestMapping(method = RequestMethod.POST, path = "/users/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponseModel sendUserChangePasswordEmail(@RequestBody ResetPasswordEmailRequestModel resetPasswordRequest);

    @RequestMapping(method = RequestMethod.POST, path = "/users/force-change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponseModel sendForceUserToChangePassword(@RequestBody ResetPasswordEmailRequestModel resetPasswordRequest);

    @RequestMapping(method = RequestMethod.POST, path = "/users/welcome", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponseModel sendWelcomeUserEmail(@RequestBody ResetPasswordEmailRequestModel resetPasswordRequest);

    @RequestMapping(method = RequestMethod.POST, path = "/user/forgotPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponseModel sendUserForgotPasswordEmail(@RequestBody ForgotPasswordEmailRequestModel forgotPasswordEmailRequest);
}
