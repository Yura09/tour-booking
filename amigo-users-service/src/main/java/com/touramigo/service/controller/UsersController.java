package com.touramigo.service.controller;

import com.touramigo.service.entity.enumeration.Permission;
import com.touramigo.service.exception.DataConflictException;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.model.*;
import com.touramigo.service.security.AccessGuard;
import com.touramigo.service.service.AuthService;
import com.touramigo.service.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.*;

import static com.touramigo.service.entity.enumeration.Permission.Authority.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private AccessGuard accessGuard;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @GetMapping
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<UserModel> getUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/partner")
    @PreAuthorize(MANAGE_USERS)
    public List<UserModel> getPartnerUsers(Principal principal) {
        String userEmail = principal.getName();
        UserModel user = this.usersService.getUserByEmail(userEmail);
        if (Objects.nonNull(user) && Objects.nonNull(user.getPartnerId())) {
            return usersService.getPartnerUsers(user.getPartnerId());
        }
        return new ArrayList<>();
    }



    @GetMapping("/current")
    public UserModel getCurrentUserWithToken(Principal principal, HttpServletRequest request) {
        if (request.getHeader(AUTHORIZATION_HEADER) != null) {
            String userEmail = principal.getName();
            UserModel user = this.usersService.getUserByEmail(userEmail);
            return user;
        } else {
            throw new InvalidDataException("Authorization token is not present.");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public UserModel getUserById(@PathVariable UUID id) {
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return  user;
    }

    @PutMapping("/{id}")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public UserModel updateUser(@PathVariable UUID id, @RequestBody @Valid CreateUpdateUserModel model) {
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        try {
            return usersService.updateUser(model, id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataConflictException("User email was not unique");
        }
    }

    @PostMapping
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public UserModel createUser(@RequestBody @Valid CreateUpdateUserModel model) {
        this.accessGuard.verifyUserCanAccessResource(model.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        try {
            return usersService.createUser(model);
        } catch (DataIntegrityViolationException ex) {
            throw new DataConflictException("User email was not unique");
        }
    }

    @PutMapping("/{id}/change-password")
    public UserModel changeUsersPassword(@RequestBody @NotNull(message = "Password is required") String password, @PathVariable UUID id) {
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return usersService.changePassword(id, password);
    }

    @PostMapping(value = "/upload-image")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public ImageModel uploadUsersImage(@RequestParam MultipartFile file, @RequestParam UUID userId) {
        UserModel user = usersService.getUserById(userId);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return usersService.uploadUserImage(userId, file);
    }

    @PutMapping("/{id}/enable-disable")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public UserModel enableOrDisableUser(@PathVariable UUID id) {
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return usersService.enableOrDisableUser(id);
    }

    @GetMapping("/{id}/products")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public Set<AssignedProductModel> getAllAssignedProducts(@PathVariable UUID id){
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return usersService.getAllAssignedProducts(id);
    }

    @PutMapping("/{id}/products")
    @PreAuthorize(MANAGE_USERS + OR +ADMINISTRATE_PARTNERS)
    public Set<AssignedProductModel> modifyUserProducts
            (@PathVariable("id") UUID id, @RequestBody List<CreateAssignedProductModel> userProductRequestModels){
        UserModel user = usersService.getUserById(id);
        this.accessGuard.verifyUserCanAccessResource(user.getPartnerId(), Permission.ADMINISTRATE_PARTNERS);
        return usersService.modifyUserProducts(id, userProductRequestModels);
    }

    @GetMapping("/account-managers")
    @PreAuthorize(ADMINISTRATE_PARTNERS+OR+ADMINISTRATE_SUPPLIERS+OR+MANAGE_SUPPLIERS)
    public List<AccountManagerModel> getAllAccountManagers() {
        return usersService.getAllAccountManagers();
    }


}
