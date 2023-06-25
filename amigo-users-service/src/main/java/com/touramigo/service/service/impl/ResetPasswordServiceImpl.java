package com.touramigo.service.service.impl;

import com.touramigo.service.entity.enumeration.TemporalLinkType;
import com.touramigo.service.entity.user.TemporalLink;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.model.*;
import com.touramigo.service.repository.TemporalLinkRepository;
import com.touramigo.service.service.ResetPasswordService;
import com.touramigo.service.service.TemporalLinkService;
import com.touramigo.service.service.UsersService;
import com.touramigo.service.service.client.EmailClient;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    @Autowired
    private UsersService usersService;

    @Autowired
    private TemporalLinkService temporalLinkService;

    @Autowired
    private EmailClient emailClient;

    @Value("${front-end.url}")
    private String frontEndUrl;

    private final static String RESET_PASSWORD_PAGE_URL = "pages/auth/reset-password/";

    private final static String CHANGE_PASSWORD_URL = "/change-password?token=";

    @Override
    @Transactional
    public EmailResponseModel resetPasswordRequest(String email) {
        final UserModel user = usersService.getUserByEmail(email);

        ResetPasswordEmailRequestModel resetPasswordRequest = new ResetPasswordEmailRequestModel();
        resetPasswordRequest.setName(user.getFullName());
        resetPasswordRequest.setSendTo(user.getEmail());
        resetPasswordRequest.setToken(frontEndUrl + RESET_PASSWORD_PAGE_URL + getToken(user, TemporalLinkType.RESET_PASSWORD));

        return emailClient.sendUserChangePasswordEmail(resetPasswordRequest);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordModel resetPasswordModel) {

        TemporalLink temporalLink = temporalLinkService.findNotExpiredByToken(resetPasswordModel.getToken())
                .orElseThrow(() -> new UnexistedDataException("Token not found. Please request change password again."));

        usersService.changePassword(temporalLink.getUser().getId(), resetPasswordModel.getPassword());
        temporalLinkService.deleteById(temporalLink.getId());
    }

    @Override
    @Transactional
    public EmailResponseModel forceUserToChangePassword(UUID id) {
        final String newPassword = RandomStringUtils.random(6);
        final UserModel user = usersService.changePassword(id, newPassword);

        ResetPasswordEmailRequestModel resetPasswordRequest = new ResetPasswordEmailRequestModel();
        resetPasswordRequest.setName(user.getFullName());
        resetPasswordRequest.setSendTo(user.getEmail());
        resetPasswordRequest.setToken(frontEndUrl + getToken(user, TemporalLinkType.FORCE_USER_TO_CHANGE_PASSWORD));

        return emailClient.sendForceUserToChangePassword(resetPasswordRequest);
    }

    @Override
    public EmailResponseModel forgotPasswordRequest(String email) {
        final UserModel user = usersService.getUserByEmail(email);

        ForgotPasswordEmailRequestModel forgotPasswordEmailRequestModel = new ForgotPasswordEmailRequestModel();
        forgotPasswordEmailRequestModel.setName(user.getFullName());
        forgotPasswordEmailRequestModel.setSendTo(user.getEmail());
        forgotPasswordEmailRequestModel.setChangePasswordUrl(CHANGE_PASSWORD_URL + getToken(user, TemporalLinkType.RESET_PASSWORD));

        return emailClient.sendUserForgotPasswordEmail(forgotPasswordEmailRequestModel);
    }

    private String getToken(UserModel user, TemporalLinkType type) {
        Optional<TemporalLink> temporalLinkOptional = temporalLinkService.findNotExpiredByUserAndType(user.getId(), type);

        TemporalLink temporalLink;

        if (temporalLinkOptional.isPresent()) {
            temporalLink = temporalLinkService.updateTemporalLink(temporalLinkOptional.get().getId());
        } else {
            temporalLink = temporalLinkService.createTemporalLink(user.getId(), type);
        }
        return temporalLink.getToken();
    }
}
