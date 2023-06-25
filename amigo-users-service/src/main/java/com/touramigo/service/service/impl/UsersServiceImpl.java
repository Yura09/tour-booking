package com.touramigo.service.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.touramigo.service.entity.enumeration.TemporalLinkType;
import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.Product;
import com.touramigo.service.entity.partner.PartnerProduct;
import com.touramigo.service.entity.user.Image;
import com.touramigo.service.entity.user.TemporalLink;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.entity.user.UserProduct;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.model.AccountManagerModel;
import com.touramigo.service.model.AssignedProductModel;
import com.touramigo.service.model.CreateAssignedProductModel;
import com.touramigo.service.model.CreateUpdateUserModel;
import com.touramigo.service.model.ImageModel;
import com.touramigo.service.model.ResetPasswordEmailRequestModel;
import com.touramigo.service.model.UserModel;
import com.touramigo.service.repository.ImageRepository;
import com.touramigo.service.repository.PartnerProductRepository;
import com.touramigo.service.repository.PartnerRepository;
import com.touramigo.service.repository.PermissionRepository;
import com.touramigo.service.repository.ProductRepository;
import com.touramigo.service.repository.SupplierRepository;
import com.touramigo.service.repository.UserProductRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.service.TemporalLinkService;
import com.touramigo.service.service.UsersService;
import com.touramigo.service.service.client.EmailClient;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UsersServiceImpl implements UsersService {

    private final static String RESET_PASSWORD_PAGE_URL = "pages/auth/reset-password/";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private UserProductRepository userProductRepository;
    @Autowired
    private PartnerProductRepository partnerProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private EmailClient emailClient;
    @Autowired
    private TemporalLinkService temporalLinkService;
    @Value("${front-end.url}")
    private String frontEndUrl;

    @Override
    public Optional<User> findOneById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll().stream().map(UserModel::create).collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getPartnerUsers(UUID partnerId) {
        return userRepository.findByPartnerId(partnerId).map(UserModel::create).collect(Collectors.toList());
    }

    @Override
    public UserModel getUserByEmail(String userEmail) {
        final User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UnexistedDataException("User does not exist"));
        return UserModel.create(user);
    }

    @Override
    public UserModel getUserById(UUID id) {
        final User user = userRepository.findById(id)
            .orElseThrow(() -> new UnexistedDataException("User does not exist"));
        return UserModel.create(user);
    }

    @Override
    @Transactional
    public UserModel updateUser(CreateUpdateUserModel model, UUID id) {
        User user = this.initUserFromModel(model, userRepository.findById(id)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND)));

        return UserModel.create(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserModel createUser(CreateUpdateUserModel model) {
        User user = this.initUserFromModel(model, new User());
        return UserModel.create(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserModel changePassword(UUID userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(password));
        user.setCredentialsNotExpired(true);
        return UserModel.create(userRepository.save(user));
    }


    @Override
    @Transactional(rollbackFor = IOException.class)
    public ImageModel uploadUserImage(UUID userId, MultipartFile image) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));
        try {
            if (Objects.nonNull(user.getImage())) {
                deleteOldUserImage(user);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("data:image/png;base64,");
            sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(image.getBytes())));

            Map uploadResult = cloudinary.uploader().upload(sb.toString(), ObjectUtils.emptyMap());

            Image newImage = new Image();

            newImage.setCloudURL((String) uploadResult.get("secure_url"));
            newImage.setImageURL((String) uploadResult.get("secure_url"));
            newImage.setHeight((Integer) uploadResult.get("height"));
            newImage.setWidth((Integer) uploadResult.get("width"));

            imageRepository.save(newImage);

            user.setImage(newImage);

            userRepository.save(user);

            return ImageModel.create(user.getImage());
        } catch (Exception ex) {
            throw new InvalidDataException(String.format("Something went wrong with uploading file to Cloudinary: %s", ex.getMessage()));
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public void deleteOldUserImage(User user) throws IOException {
        cloudinary.uploader().destroy(user.getImage().getCloudURL(), ObjectUtils.emptyMap());
        imageRepository.delete(user.getImage());
        user.setImage(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserModel enableOrDisableUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));

        if (user.isEnabled()) {
            user.setEnabled(false);
        } else {
            if (!user.isEnabledBefore()) {
                TemporalLink temporalLink = temporalLinkService.createTemporalLink(user.getId(), TemporalLinkType.WELCOME_USER);
                ResetPasswordEmailRequestModel resetPasswordRequest = new ResetPasswordEmailRequestModel();
                resetPasswordRequest.setName(user.getFullName());
                resetPasswordRequest.setSendTo(user.getEmail());
                resetPasswordRequest.setToken(frontEndUrl + RESET_PASSWORD_PAGE_URL + temporalLink.getToken());
                emailClient.sendWelcomeUserEmail(resetPasswordRequest);
                user.setEnabledBefore(true);
            }
            user.setEnabled(true);
        }
        return UserModel.create(userRepository.save(user));
    }

    @Override
    public Set<AssignedProductModel> getAllAssignedProducts(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));
        return userProductRepository.findByUserId(userId).stream().map(AssignedProductModel::create).collect(Collectors.toSet());
    }

    @Override
    public Set<AssignedProductModel> modifyUserProducts(UUID userId, List<CreateAssignedProductModel> userProductRequestModels) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnexistedDataException("User " + userId + " not found"));

        List<PartnerProduct> partnerProducts = partnerProductRepository.findByPartnerId(user.getPartner().getId());
        List<UUID> availableProducts = partnerProducts.stream().map(partnerProduct -> partnerProduct.getProduct().getId()).collect(Collectors.toList());
        List<UUID> availablePermissions = partnerProducts.stream().flatMap(partnerProduct -> partnerProduct.getPermissions().stream().map(Permission::getId)).collect(Collectors.toList());
        Set<UserProduct> userProducts = userProductRequestModels
            .stream().map(assignUserProductModel -> {
                UserProduct putUserProduct = new UserProduct();

                Product product = productRepository.findById(assignUserProductModel.getProductId()).orElseThrow(
                    () -> new UnexistedDataException("Product " + assignUserProductModel.getProductId() + " not found"));

                if (availableProducts.contains(product.getId())) {
                    putUserProduct.setProduct(product);
                } else {
                    throw new InvalidDataException("Product cannot be assigned to a User");
                }

                Set<Permission> permissionsToBeAssigned = new HashSet<>(permissionRepository.findAll(assignUserProductModel.getPermissionIds()));
                permissionsToBeAssigned.forEach(permission -> {
                    if (!availablePermissions.contains(permission.getId())) {
                        throw new InvalidDataException("Permission cannot be assigned to a User");
                    }
                });
                putUserProduct.setPermissions(permissionsToBeAssigned);

                putUserProduct.setUser(user);

                return putUserProduct;
            }).collect(Collectors.toSet());

        userProductRepository.delete(userProductRepository.findByUserId(userId));
        userProductRepository.save(userProducts);

        return userProducts.stream().map(AssignedProductModel::create).collect(Collectors.toSet());
    }

    private User initUserFromModel(CreateUpdateUserModel model, User user) {
        user.setUserName(model.getUserName());
        user.setFullName(model.getFullName());
        user.setEmail(model.getEmail());
        user.setContactNumber(model.getContactNumber());
        user.setJobTitle(model.getJobTitle());
        user.setCredentialsNotExpired(model.isCredentialsNotExpired());

        if (Objects.isNull(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }

        user.setPartner(partnerRepository.findById(model.getPartnerId())
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND)));

        if (!Objects.isNull(model.getSupplierId())) {
            user.setSupplier(supplierRepository.findById(model.getSupplierId())
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND)));
        } else {
            user.setSupplier(null);
        }

        return user;
    }

    @Override
    public List<AccountManagerModel> getAllAccountManagers() {
        return userRepository.findByMastersPartner().stream().map(AccountManagerModel::create).collect(Collectors.toList());
    }

}
