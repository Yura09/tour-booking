package com.touramigo.service.service;

import com.touramigo.service.entity.user.User;
import com.touramigo.service.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UsersService {

    Optional<User> findOneById(UUID id);

    List<UserModel> getAllUsers();

    List<UserModel> getPartnerUsers(UUID partnerId);

    UserModel getUserByEmail(String userEmail);

    UserModel getUserById(UUID id);

    @Transactional
    UserModel updateUser(CreateUpdateUserModel model, UUID id);

    @Transactional
    UserModel createUser(CreateUpdateUserModel model);

    @Transactional
    UserModel changePassword(UUID userId, String password);

    @Transactional
    ImageModel uploadUserImage(UUID userId, MultipartFile image);

    @Transactional
    UserModel enableOrDisableUser(UUID id);

    Set<AssignedProductModel> getAllAssignedProducts(UUID userId);

    Set<AssignedProductModel> modifyUserProducts(UUID userId, List<CreateAssignedProductModel> userProductRequestModels);

    List<AccountManagerModel> getAllAccountManagers();
}
