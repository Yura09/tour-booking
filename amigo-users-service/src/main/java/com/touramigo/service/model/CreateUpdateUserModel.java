package com.touramigo.service.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateUserModel {

    @NotNull(message = "User name is required")
    private String userName;

    @NotNull(message = "Full name is required")
    private String fullName;

    private String jobTitle;

    private String contactNumber;

    @NotNull(message = "Email address is required")
    private String email;

    @NotNull(message = "Partner id is required")
    private UUID partnerId;

    private UUID supplierId;

    private boolean credentialsNotExpired;

}
