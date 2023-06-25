package com.touramigo.service.controller;

import com.touramigo.service.entity.enumeration.Permission;
import com.touramigo.service.model.AssignedProductModel;
import com.touramigo.service.model.ConnectionValueModel;
import com.touramigo.service.model.CreateAssignedProductModel;
import com.touramigo.service.model.CreateUpdateConnectionModel;
import com.touramigo.service.model.GroupCommercialModel;
import com.touramigo.service.model.OperationalGroupModel;
import com.touramigo.service.model.PartnerGroupModel;
import com.touramigo.service.model.PartnerModel;
import com.touramigo.service.model.PermissionModel;
import com.touramigo.service.security.AccessGuard;
import com.touramigo.service.service.PartnerService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.touramigo.service.entity.enumeration.Permission.Authority.*;

@RestController
@RequestMapping("/partners")
public class PartnersController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private AccessGuard accessGuard;

    @GetMapping
    @PreAuthorize(ADMINISTRATE_PARTNERS + OR + ADMINISTRATE_BOOKINGS)
    public List<PartnerModel> getAllCompanies() {
        return partnerService.getAllCompanies();
    }

    @GetMapping("/commercial")
    public List<GroupCommercialModel> getAllPartnersForCommercial() {
        return partnerService.findAllForCommercial();
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_PARTNERS + OR + MANAGE_USERS)
    public PartnerModel getOneCompanyById(@PathVariable UUID id) {
        return partnerService.getOneById(id);
    }

    @PostMapping
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerModel createCompany(@RequestBody @Valid PartnerModel model) {
        return partnerService.createCompany(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerModel updateCompany(@PathVariable UUID id, @RequestBody PartnerModel model) {
        return partnerService.updateCompany(id, model);
    }

    @PostMapping("/groups")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerGroupModel createCompanyGroup(@RequestBody PartnerGroupModel model) {
        return partnerService.createCompanyGroup(model);
    }

    @PutMapping("/groups")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerGroupModel updateCompanyGroup(@RequestBody PartnerGroupModel model) {
        return partnerService.updateCompanyGroup(model);
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        partnerService.deletePartnerGroup(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/groups/{groupId}")
    @PostMapping(ADMINISTRATE_PARTNERS)
    public PartnerModel assignPartnerGroupToPartner(@PathVariable UUID id, @PathVariable UUID groupId) {
        return partnerService.assignPartnerGroupToParner(id, groupId);
    }

    @GetMapping("/groups")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<PartnerGroupModel> getAllCompanyGroups() {
        return partnerService.getAllCompanyGroups();
    }

    @GetMapping("/{id}/products")
    @PreAuthorize(ADMINISTRATE_PARTNERS + OR + MANAGE_USERS)
    public Set<AssignedProductModel> getAllAssignedProducts(@PathVariable UUID id) {
        this.accessGuard.verifyUserCanAccessResource(id, Permission.ADMINISTRATE_PARTNERS);
        return partnerService.getAllAssignedProducts(id);
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize(ADMINISTRATE_PARTNERS + OR + MANAGE_USERS)
    public List<PermissionModel> getAllAssignedPartnerPermissions(@PathVariable UUID id) {
        this.accessGuard.verifyUserCanAccessResource(id, Permission.ADMINISTRATE_PARTNERS);
        return partnerService.getAllAssignedPartnerPermissions(id);
    }

    @PutMapping("/{id}/products")
    @PreAuthorize(ADMINISTRATE_PARTNERS + OR + MANAGE_USERS)
    public Set<AssignedProductModel> modifyPartnerProducts
        (@PathVariable("id") UUID partnerId, @RequestBody List<CreateAssignedProductModel> partnerProductRequestModels) {
        this.accessGuard.verifyUserCanAccessResource(partnerId, Permission.ADMINISTRATE_PARTNERS);
        return partnerService.modifyPartnerProducts(partnerId, partnerProductRequestModels);
    }

    @PutMapping("/{id}/assign-supplier/{supplierId}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerModel assignSupplier(@PathVariable UUID id, @PathVariable("supplierId") UUID supplierId, @RequestBody List<CreateUpdateConnectionModel> body) {
        return partnerService.assignSupplier(id, supplierId, body);
    }

    @PutMapping("/{id}/remove-supplier/{supplierId}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public PartnerModel removeSupplier(@PathVariable(name = "id") UUID id, @PathVariable("supplierId") UUID supplierId) {
        return partnerService.removeSupplierFromPartner(id, supplierId);
    }

    @GetMapping("/{id}/connections")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<ConnectionValueModel> getConnectionValuesOfPartner(@PathVariable UUID id) {
        return partnerService.getConnectionValueByPartnerId(id);
    }

    @GetMapping("/{id}/connections-for-supplier/{supplierCode}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<ConnectionValueModel> getConnectionValuesBySupplierCode(@PathVariable UUID id, @PathVariable String supplierCode) {
        return partnerService.getConnectionValuesBySupplierCode(id, supplierCode);
    }

    @GetMapping("/connections/{id}")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public ConnectionValueModel getConnectionValueById(@PathVariable UUID id) {
        return partnerService.getConnectionValueById(id);
    }

    @PutMapping("/connections")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<ConnectionValueModel> updateConnectionValueParameters(@RequestBody List<CreateUpdateConnectionModel> models) {
        return partnerService.updateConnectionValueModels(models);
    }

    @GetMapping("/{id}/ogroup")
    @PreAuthorize(MANAGE_USERS)
    public List<OperationalGroupModel> getAllOperationalGroups(@PathVariable UUID id) {
        this.accessGuard.verifyUserCanAccessResource(id, null);
        return partnerService.getAllOperationalGroups(id);
    }

    @PostMapping("/{id}/ogroup")
    @PreAuthorize(MANAGE_USERS)
    public OperationalGroupModel createOperationalGroup(@PathVariable UUID id, @RequestBody String name) {
        this.accessGuard.verifyUserCanAccessResource(id, null);
        return partnerService.createOperationalGroup(id, name);
    }

    @DeleteMapping("/{id}/ogroup/{oGroupId}")
    @PreAuthorize(MANAGE_USERS)
    public ResponseEntity<Void> deleteOperationalGroup(@PathVariable UUID oGroupId) {
        OperationalGroupModel oGroup = partnerService.getOperationalGroup(oGroupId);
        this.accessGuard.verifyUserCanAccessResource(oGroup.getPartnerId(), null);
        partnerService.deleteOperationalGroup(oGroupId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/ogroup/{oGroupId}/assign_users")
    @PreAuthorize(MANAGE_USERS)
    public OperationalGroupModel modifyUsersInOperationalGroup(@PathVariable UUID oGroupId, @RequestBody List<UUID> userIDs) {
        OperationalGroupModel oGroup = partnerService.getOperationalGroup(oGroupId);
        this.accessGuard.verifyUserCanAccessResource(oGroup.getPartnerId(), null);
        return partnerService.modifyUsersInOperationalGroup(oGroupId, userIDs);
    }

    @PostMapping("/{id}/ogroup/{oGroupId}/assign_permissions")
    @PreAuthorize(MANAGE_USERS)
    public OperationalGroupModel modifyPermissionsInOperationalGroup(@PathVariable UUID oGroupId, @RequestBody List<UUID> permissionIDs) {
        OperationalGroupModel oGroup = partnerService.getOperationalGroup(oGroupId);
        this.accessGuard.verifyUserCanAccessResource(oGroup.getPartnerId(), null);
        return partnerService.modifyPermissionsInOperationalGroup(oGroupId, permissionIDs);
    }
}
