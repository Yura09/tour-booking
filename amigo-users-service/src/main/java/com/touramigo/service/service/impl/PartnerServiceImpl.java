package com.touramigo.service.service.impl;

import com.touramigo.service.entity.enumeration.CompanyBusinessType;
import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.Product;
import com.touramigo.service.entity.partner.ConnectionValue;
import com.touramigo.service.entity.partner.OperationalGroup;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.partner.PartnerGroup;
import com.touramigo.service.entity.partner.PartnerProduct;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.supplier.SupplierConnection;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.DataConflictException;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.model.AssignedProductModel;
import com.touramigo.service.model.ConnectionValueModel;
import com.touramigo.service.model.CreateAssignedProductModel;
import com.touramigo.service.model.CreateUpdateConnectionModel;
import com.touramigo.service.model.GroupCommercialModel;
import com.touramigo.service.model.OperationalGroupModel;
import com.touramigo.service.model.PartnerGroupModel;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.model.PermissionModel;
import com.touramigo.service.repository.*;
import com.touramigo.service.service.PartnerHistoryService;
import com.touramigo.service.service.PartnerService;
import com.touramigo.service.service.PermissionService;
import com.touramigo.service.service.UsersService;
import com.touramigo.service.util.SecurityUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PartnerServiceImpl implements PartnerService {

    private static final String SEPARATOR = ",";
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private PartnerGroupRepository partnerGroupRepository;
    @Autowired
    private PartnerProductRepository partnerProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ConnectionValueRepository connectionValueRepository;
    @Autowired
    private SupplierConnectionRepository supplierConnectionRepository;
    @Autowired
    private OperationalGroupRepository operationalGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private PartnerHistoryService partnerHistoryService;
    @Autowired
    private PermissionService permissionService;

    public List<GroupCommercialModel> findAllForCommercial() {
        PartnerGroup partnerGroup = getPartnerGroup();
        return partnerRepository.findAllForCommercial().stream().peek(partner -> {
            if (partner.getPartnerGroup() == null) {
                partner.setPartnerGroup(partnerGroup);
            }
        }).collect(Collectors.groupingBy(Partner::getPartnerGroup)).entrySet().stream().map(
            pair -> {
                pair.getKey().setPartners(pair.getValue());
                return GroupCommercialModel.create(pair.getKey());
            }).collect(Collectors.toList());
    }

    @Override
    public List<PartnerModel> getAllCompanies() {
        List<UUID> userIdsFromCurrentPartner = userRepository.findByPartnerId(SecurityUtil.getPartnerId()).map(User::getId)
            .collect(Collectors.toList());

        return partnerRepository.findAll().stream().peek(sup -> sup.setNotes(sup.getNotes()
            .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
            .collect(Collectors.toSet()))).map(PartnerModel::create)
            .collect(Collectors.toList());
    }

    @Override
    public PartnerModel getOneById(UUID id) {
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        return PartnerModel.create(partner);
    }

    @Override
    public Optional<Partner> findOne(UUID id) {
        return partnerRepository.findById(id);
    }

    @Override
    @Transactional
    public PartnerModel createCompany(PartnerModel model) {
        Partner partner = this.initCompanyVariables(model, new Partner());
        PartnerModel toSave = PartnerModel.create(partnerRepository.save(partner));
        partnerHistoryService.create(toSave);
        return toSave;
    }

    @Override
    @Transactional
    public PartnerModel updateCompany(UUID id, PartnerModel model) {
        Partner partner = this.initCompanyVariables(model,
            partnerRepository.findById(id)
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND)));
        PartnerModel partnerModel = PartnerModel.create(partnerRepository.save(partner));
        partnerHistoryService.create(partnerModel);
        return partnerModel;
    }

    @Override
    @Transactional
    public PartnerModel enableOrDisableCompany(UUID id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));

        if (partner.isEnabled()) {
            partner.setEnabled(false);
        } else {
            partner.setEnabled(true);
        }

        return PartnerModel.create(partnerRepository.save(partner));
    }

    @Override
    @Transactional
    public PartnerModel assignSupplier(UUID partnerId, UUID supplierId, List<CreateUpdateConnectionModel> connectionModels) {
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));

        if (!supplier.isEnabled()) {
            throw new InvalidDataException("You can not assign a disabled supplier.");
        }

        for (CreateUpdateConnectionModel connectionModel : connectionModels) {
            SupplierConnection supplierConnection = supplierConnectionRepository.findById(connectionModel.getSupplierConnectionKeyId())
                .orElseThrow(() -> new UnexistedDataException("Supplier Connection Key not found"));

            if (Objects.isNull(connectionModel.getConnectionValueId())) {
                ConnectionValue newValue = new ConnectionValue();
                newValue.setPartner(partner);
                newValue.setSupplierConnection(supplierConnection);
                newValue.setValue(connectionModel.getValue());
                connectionValueRepository.save(newValue);
            } else {
                ConnectionValue connectionValue = connectionValueRepository.findById(connectionModel.getConnectionValueId())
                    .orElseThrow(() -> new UnexistedDataException("Connection Value not found"));

                connectionValue.setValue(connectionModel.getValue());
                connectionValueRepository.save(connectionValue);
            }
        }

        if (Objects.nonNull(partner.getSuppliers())) {
            partner.getSuppliers().add(supplier);
        } else {
            partner.setSuppliers(Collections.singleton(supplier));
        }
        return PartnerModel.create(partnerRepository.save(partner));
    }

    @Override
    @Transactional
    public PartnerModel removeSupplierFromPartner(UUID partnerId, UUID supplierId) {
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));

        if (!supplier.isEnabled()) {
            throw new InvalidDataException("You can not unassign a disabled supplier.");
        }

        if (Objects.nonNull(partner.getSuppliers())) {
            partner.getSuppliers().remove(supplier);
        } else {
            partner.setSuppliers(Collections.emptySet());
        }
        return PartnerModel.create(partnerRepository.save(partner));
    }


    private Partner initCompanyVariables(PartnerModel model, Partner partner) {
        partner.setName(model.getName());
        partner.setAddress(model.getAddress());
        partner.setZip(model.getZip());
        partner.setCity(model.getCity());
        partner.setCountry(model.getCountry());
        partner.setMaster(model.isMaster());
        partner.setTimezone(model.getTimezone());
        partner.setWebsite(model.getWebsite());
        partner.setBookingsEnabled(model.isBookingsEnabled());
        if (nonNull(model.getBusinessType()) && !model.getBusinessType().isEmpty()) {
            partner.setBusinessType(CompanyBusinessType.valueOf(model.getBusinessType()));
        }

        if (nonNull(model.getEmails()) && !model.getEmails().isEmpty() && !StringUtils.isBlank(String.join("", model.getEmails()))) {
            String joined =
                model.getEmails().stream()
                    .filter(s -> s != null && !StringUtils.isBlank(s))
                    .collect(Collectors.joining(SEPARATOR));
            if (!joined.isEmpty()) {
                partner.setEmails(joined);
            }
        } else {
            partner.setEmails(null);
        }

        if (nonNull(model.getContactPhones()) && !model.getContactPhones().isEmpty() && !StringUtils.isBlank(String.join("", model.getContactPhones()))) {
            String joined =
                model.getContactPhones().stream()
                    .filter(s -> s != null && !StringUtils.isBlank(s))
                    .collect(Collectors.joining(SEPARATOR));
            if (!joined.isEmpty()) {
                partner.setContactPhones(joined);
            }
        } else {
            partner.setContactPhones(null);
        }

        if (nonNull(model.getCompanyGroup())) {
            if (isNull(model.getCompanyGroup().getId())) {
                throw new InvalidDataException("Please provide partner group id");
            }

            PartnerGroup partnerGroup = partnerGroupRepository.findById(model.getCompanyGroup().getId())
                .orElseThrow(() -> new UnexistedDataException("Partner group not found."));

            partner.setPartnerGroup(partnerGroup);
        }

        partner.setEnabled(model.isEnabled());

        if (nonNull(model.getAccountManagerId())) {
            partner.setAccountManager(usersService.findOneById(model.getAccountManagerId())
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND)));
        } else {
            partner.setAccountManager(null);
        }

        if (nonNull(model.getPartnerGroupId())) {
            partner.setPartnerGroup(partnerGroupRepository.findById(model.getPartnerGroupId())
                .orElseThrow(() -> new UnexistedDataException("Partner Group not found")));
        } else {
            partner.setPartnerGroup(null);
        }

        return partner;
    }

    @Override
    @Transactional
    public PartnerGroupModel createCompanyGroup(PartnerGroupModel model) {
        PartnerGroup partnerGroup = new PartnerGroup();
        partnerGroup.setName(model.getName());
        return PartnerGroupModel.create(partnerGroupRepository.save(partnerGroup));
    }


    @Transactional
    @Override
    public PartnerGroupModel updateCompanyGroup(PartnerGroupModel model) {

        if (Objects.isNull(model.getId())) {
            throw new InvalidDataException("Please provide a partner group id");
        }

        PartnerGroup partnerGroup = partnerGroupRepository.findById(model.getId())
            .orElseThrow(() -> new UnexistedDataException("Partner Group not found"));

        partnerGroup.setName(model.getName());

        return PartnerGroupModel.create(partnerGroupRepository.save(partnerGroup));
    }

    @Override
    @Transactional
    public void deletePartnerGroup(UUID id) {
        List<Partner> partners = partnerRepository.findByPartnerGroupId(id);
        partners.forEach(partner -> partner.setPartnerGroup(null));

        partnerRepository.save(partners);
        partnerGroupRepository.delete(id);
    }

    @Override
    @Transactional
    public PartnerModel assignPartnerGroupToParner(UUID id, UUID groupId) {
        Partner partner = partnerRepository.findById(id).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        PartnerGroup partnerGroup = partnerGroupRepository.findById(groupId).orElseThrow(() -> new UnexistedDataException("Partner Group not found"));

        partner.setPartnerGroup(partnerGroup);

        return PartnerModel.create(partnerRepository.save(partner));
    }

    @Override
    public List<PartnerGroupModel> getAllCompanyGroups() {
        return partnerGroupRepository.findAll().stream().map(PartnerGroupModel::create).collect(Collectors.toList());
    }

    @Override
    public Set<AssignedProductModel> modifyPartnerProducts(UUID partnerId, List<CreateAssignedProductModel> partnerProductRequestModels) {
        Partner partner = partnerRepository.findById(partnerId)
            .orElseThrow(() -> new UnexistedDataException("Partner " + partnerId + " not found"));
        Set<PartnerProduct> partnerProducts = partnerProductRequestModels
            .stream().map(assignPartnerProductModel -> {

                PartnerProduct putPartnerProduct = new PartnerProduct();

                Optional<Product> product = productRepository.findById(assignPartnerProductModel.getProductId());
                putPartnerProduct.setProduct(
                    product.orElseThrow(
                        () -> new UnexistedDataException("Product " + assignPartnerProductModel.getProductId() + " not found")));

                Set<Permission> permissionsToBeAssigned = new HashSet<>(permissionRepository.findAll(assignPartnerProductModel.getPermissionIds()));

                // Do not allow to assign master permissions to not master partners
                if (!partner.isMaster()) {
                    permissionsToBeAssigned = permissionsToBeAssigned.stream().filter(permissions -> !permissions.isMaster()).collect(Collectors.toSet());
                }

                putPartnerProduct.setPermissions(permissionsToBeAssigned);
                putPartnerProduct.setPartner(partner);

                return putPartnerProduct;
            }).collect(Collectors.toSet());

        partnerProductRepository.delete(partnerProductRepository.findByPartnerId(partnerId));
        partnerProductRepository.save(partnerProducts);

        return partnerProducts.stream().map(AssignedProductModel::create)
                .peek(x -> x.getProduct().setAvailablePermissions(permissionService.getAllPermissionsFromProduct(x.getProduct().getId()).size())).collect(Collectors.toSet());
    }

    @Override
    public Set<AssignedProductModel> getAllAssignedProducts(UUID partnerId) {
        return partnerProductRepository.findByPartnerId(partnerId).stream().map(AssignedProductModel::create).collect(Collectors.toSet());
    }

    @Override
    public List<ConnectionValueModel> getConnectionValueByPartnerId(UUID partnerId) {
        final Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        return partner.getConnectionValues().stream().map(ConnectionValueModel::create).collect(Collectors.toList());
    }

    @Override
    public ConnectionValueModel getConnectionValueById(UUID connectionValueId) {
        return ConnectionValueModel.create(connectionValueRepository.findById(connectionValueId)
            .orElseThrow(() -> new UnexistedDataException("Connection value not found")));
    }

    @Override
    public List<ConnectionValueModel> updateConnectionValueModels(List<CreateUpdateConnectionModel> models) {
        List<ConnectionValueModel> updatedList = new ArrayList<>();

        for (CreateUpdateConnectionModel model : models) {
            ConnectionValue connectionValue = connectionValueRepository.findById(model.getConnectionValueId())
                .orElseThrow(() -> new UnexistedDataException("Connection value not found"));
            connectionValue.setValue(model.getValue());
            updatedList.add(ConnectionValueModel.create(connectionValueRepository.save(connectionValue)));
        }

        return updatedList;
    }

    @Override
    public List<ConnectionValueModel> getConnectionValuesBySupplierCode(UUID id, String supplierCode) {
        final Partner partner = partnerRepository.findById(id).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));
        return partner.getConnectionValues()
            .stream()
            .filter(it -> it.getSupplierConnection().getSupplier().getCodeVariable().equals(supplierCode))
            .map(ConnectionValueModel::create)
            .collect(Collectors.toList());
    }

    @Override
    public List<OperationalGroupModel> getAllOperationalGroups(UUID partnerId) {

        // Find Partner by Id
        Partner partner = partnerRepository.findById(partnerId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));

        List<OperationalGroup> oGroupList = operationalGroupRepository.findByPartnerId(partnerId);

        return oGroupList.stream().map(OperationalGroupModel::create).collect(Collectors.toList());
    }

    @Override
    public OperationalGroupModel getOperationalGroup(UUID groupId) {
        OperationalGroup oGroup = this.operationalGroupRepository.findById(groupId)
            .orElseThrow(() -> new UnexistedDataException("Group not found"));

        return OperationalGroupModel.create(oGroup);
    }

    @Override
    public OperationalGroupModel createOperationalGroup(UUID partnerId, String oGroupName) {
        // Find Partner by Id
        Partner partner = partnerRepository.findById(partnerId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));

        // Create Operational Group
        OperationalGroup oGroup = new OperationalGroup();
        oGroup.setName(oGroupName);
        oGroup.setPartner(partner);
        try {
            // Save
            return OperationalGroupModel.create(operationalGroupRepository.save(oGroup));
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("You cannot add a group with the same name");
        }
    }


    @Override
    @SneakyThrows
    public OperationalGroupModel modifyUsersInOperationalGroup(UUID oGroupId, List<UUID> userIDs) {
        OperationalGroup operationalGroup = operationalGroupRepository.findById(oGroupId)
            .orElseThrow(() -> new UnexistedDataException("No Operational Group found"));

        List<User> userList = userRepository.findAll(userIDs);

        Partner partner = operationalGroup.getPartner();

        // Firstly, check if Users are the employees of Partner
        List<User> usersInPartner = partner.getUsers();
        for (User user : userList) {
            if (!usersInPartner.contains(user)) {
                throw new IllegalAccessException("Users outside Partner is not allowed");
            }
        }
        ;

        // Then assign Users to the Operational Group
        operationalGroup.setUsers(userList);

        // Save
        OperationalGroup saved = operationalGroupRepository.save(operationalGroup);

        return OperationalGroupModel.create(saved);
    }

    @Override
    public void deleteOperationalGroup(UUID oGroupId) {
        operationalGroupRepository.delete(oGroupId);
    }

    @SneakyThrows
    @Override
    public OperationalGroupModel modifyPermissionsInOperationalGroup(UUID oGroupId, List<UUID> permissionIDs) {
        OperationalGroup operationalGroup = operationalGroupRepository.findById(oGroupId)
            .orElseThrow(() -> new UnexistedDataException("No Operational Group found"));

        List<Permission> permissionList = permissionRepository.findAll(permissionIDs);

        Partner partner = operationalGroup.getPartner();
        List<Permission> assignedPartnerPermissions = new ArrayList<>();
        partner.getPartnerProducts().forEach(product -> assignedPartnerPermissions.addAll(product.getPermissions()));

        // Firstly, check if Permissions are the subset of assigned Partner-Permissions
        for (Permission permission : permissionList) {
            if (!assignedPartnerPermissions.contains(permission)) {
                throw new IllegalAccessException("You can't assign Permission - "
                    + permission.getCodeKey() +
                    " outside Partner-permissions");
            }
        }
        ;

        // Then assign Permissions to the Operational Group
        operationalGroup.setPermissions(permissionList);

        // Save
        OperationalGroup saved = operationalGroupRepository.save(operationalGroup);

        return OperationalGroupModel.create(saved);
    }

    @Override
    public List<PermissionModel> getAllAssignedPartnerPermissions(UUID partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));

        List<Permission> assignedPartnerPermissions = new ArrayList<>();
        partner.getPartnerProducts().forEach(product -> assignedPartnerPermissions.addAll(product.getPermissions()));

        return assignedPartnerPermissions.stream().map(PermissionModel::create).collect(Collectors.toList());
    }

    private PartnerGroup getPartnerGroup() {
        PartnerGroup partnerGroup = new PartnerGroup();
        partnerGroup.setName("OTHER");
        partnerGroup.setPartners(new ArrayList<>());
        return partnerGroup;
    }
}
