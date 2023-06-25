package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoggedUserInfoModel {

    private UUID id;

    private UUID partnerId;

    private String email;

    private String fullName;

    private String jobTitle;

    private String contactNumber;

    private boolean enabled;

    private UserImageModel image;

    private List<String> permissions;

    private List<String> assignedSupplierCodes;

    private String managedSupplierCode;

    private OAuth2AccessToken tokenInfo;

}
