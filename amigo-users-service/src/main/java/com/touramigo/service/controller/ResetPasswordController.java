package com.touramigo.service.controller;

import com.touramigo.service.model.EmailResponseModel;
import com.touramigo.service.model.ResetPasswordModel;
import com.touramigo.service.model.ResetPasswordRequestModel;
import com.touramigo.service.service.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reset-password")
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetPasswordService;

    /**
     * For Gaia
     * Sends email with link to Gaia password reset form
     */
    @PostMapping("/request")
    public EmailResponseModel resetPasswordRequest(@RequestBody ResetPasswordRequestModel resetPasswordRequestModel) {
        return resetPasswordService.resetPasswordRequest(resetPasswordRequestModel.getEmail());
    }

    @PostMapping
    public void resetPassword(@RequestBody ResetPasswordModel resetPasswordModel) {
        resetPasswordService.resetPassword(resetPasswordModel);
    }

    /**
     * For Plutus
     * Sends email with link to Plutus password reset form
     */
    @PostMapping("/forgot-password")
    public EmailResponseModel forgotPasswordRequest(@RequestBody ResetPasswordRequestModel resetPasswordRequestModel){
        return resetPasswordService.forgotPasswordRequest(resetPasswordRequestModel.getEmail());
    }


    @PutMapping("/user/{id}/force-change-password")
    public EmailResponseModel forceUserToChangePassword(@PathVariable UUID id) {
        return resetPasswordService.forceUserToChangePassword(id);
    }

}
