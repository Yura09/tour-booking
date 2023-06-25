package com.touramigo.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.touramigo.service.entity.global_config.AssignedProduct;
import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {

    private UUID id;

    private UUID partnerId;

    private UUID supplierId;

    private String fullName;

    private String userName;

    private String jobTitle;

    private String contactNumber;

    private String email;

    private boolean enabled;

    private ImageModel image;

    private Set<String> assignedSupplierCodes;

    private String managedSupplierCode;

    private List<String> permissions;

    private boolean credentialsNotExpired;

    public static UserModel create(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        if (Objects.nonNull(user.getSupplier())) {
            userModel.setSupplierId(user.getSupplier().getId());
        }
        if (Objects.nonNull(user.getPartner())) {
            userModel.setPartnerId(user.getPartner().getId());
        }
        userModel.setEmail(user.getEmail());
        userModel.setUserName(user.getUserName());
        userModel.setFullName(user.getFullName());
        userModel.setJobTitle(user.getJobTitle());
        userModel.setContactNumber(user.getContactNumber());
        userModel.setEnabled(user.isEnabled());
        userModel.setCredentialsNotExpired(user.isCredentialsNotExpired());

        if(Objects.nonNull(user.getImage())) {
            userModel.setImage(ImageModel.create(user.getImage()));
        } else {
            userModel.setImage(new ImageModel());
        }
        if (user.getPartner() != null && user.getPartner().getSuppliers() != null) {
            userModel.setAssignedSupplierCodes(user.getPartner().getSuppliers().stream().filter(Supplier::isEnabled).map(Supplier::getCodeVariable).collect(Collectors.toSet()));
        }

        if (Objects.nonNull(user.getSupplier())) {
            userModel.setManagedSupplierCode(user.getSupplier().getCodeVariable());
        }

        if (Objects.nonNull(user.getPartner()) && Objects.nonNull(user.getPartner().getPartnerProducts()) && Objects.nonNull(user.getUserProducts())) {
            List<String> permissions = SecurityUtil.getUsersPermissionsCodeKeys(user);
            userModel.setPermissions(permissions);
        } else {
            userModel.setPermissions(new ArrayList<>());
        }

        return userModel;
    }
}
