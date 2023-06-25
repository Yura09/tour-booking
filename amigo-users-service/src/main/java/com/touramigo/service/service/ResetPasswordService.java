package com.touramigo.service.service;

import com.touramigo.service.model.EmailResponseModel;
import com.touramigo.service.model.ResetPasswordModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ResetPasswordService {

    @Transactional
    EmailResponseModel resetPasswordRequest(String email);

    @Transactional
    void resetPassword(ResetPasswordModel resetPasswordModel);

    @Transactional
    EmailResponseModel forceUserToChangePassword(UUID id);
    @Transactional
    EmailResponseModel forgotPasswordRequest(String email);

}
