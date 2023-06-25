package com.touramigo.service.service;

import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.model.AssignedProductModel;
import com.touramigo.service.model.ConnectionValueModel;
import com.touramigo.service.model.CreateAssignedProductModel;
import com.touramigo.service.model.CreateUpdateConnectionModel;
import com.touramigo.service.model.GroupCommercialModel;
import com.touramigo.service.model.OperationalGroupModel;
import com.touramigo.service.model.PartnerGroupModel;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.model.PermissionModel;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

public interface PartnerService {
    List<GroupCommercialModel> findAllForCommercial();

    List<PartnerModel> getAllCompanies();

    PartnerModel getOneById(UUID id);

    Optional<Partner> findOne(UUID id);

    @Transactional
    PartnerModel createCompany(PartnerModel model);

    @Transactional
    PartnerModel updateCompany(UUID id, PartnerModel model);

    @Transactional
    PartnerModel enableOrDisableCompany(UUID id);

    @Transactional
    PartnerModel assignSupplier(UUID partnerId, UUID supplierId, List<CreateUpdateConnectionModel> body);

    @Transactional
    PartnerModel removeSupplierFromPartner(UUID partnerId, UUID supplierId);

    @Transactional
    PartnerGroupModel createCompanyGroup(PartnerGroupModel model);

    @Transactional
    PartnerGroupModel updateCompanyGroup(PartnerGroupModel model);

    @Transactional
    void deletePartnerGroup(UUID id);

    @Transactional
    PartnerModel assignPartnerGroupToParner(UUID id, UUID groupId);

    List<PartnerGroupModel> getAllCompanyGroups();

    Set<AssignedProductModel> modifyPartnerProducts(UUID partnerId, List<CreateAssignedProductModel> partnerProductRequestModels);

    Set<AssignedProductModel> getAllAssignedProducts(UUID partnerId);

    List<ConnectionValueModel> getConnectionValueByPartnerId(UUID partnerId);

    ConnectionValueModel getConnectionValueById(UUID connectionValueId);

    List<ConnectionValueModel> updateConnectionValueModels(List<CreateUpdateConnectionModel> model);

    List<ConnectionValueModel> getConnectionValuesBySupplierCode(UUID id, String supplierCode);

    List<OperationalGroupModel> getAllOperationalGroups(UUID partnerId);

    OperationalGroupModel getOperationalGroup(UUID groupId);

    OperationalGroupModel createOperationalGroup(UUID partnerId, String oGroupName);

    @SneakyThrows
    OperationalGroupModel modifyUsersInOperationalGroup(UUID oGroupId, List<UUID> userIDs);

    void deleteOperationalGroup(UUID oGroupId);

    @SneakyThrows
    OperationalGroupModel modifyPermissionsInOperationalGroup(UUID oGroupId, List<UUID> permissionIDs);

    List<PermissionModel> getAllAssignedPartnerPermissions(UUID partnerId);
}
