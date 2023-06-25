package com.touramigo.service.controller;


import com.touramigo.service.entity.enumeration.Permission;
import com.touramigo.service.model.ContactModel;
import com.touramigo.service.model.CreateUpdateSupplierModel;
import com.touramigo.service.model.SupplierConnectionModel;
import com.touramigo.service.model.SupplierModel;
import com.touramigo.service.model.TermsAndConditionsCreateUpdateDto;
import com.touramigo.service.model.TermsAndConditionsViewDto;
import com.touramigo.service.model.UserModel;
import com.touramigo.service.security.AccessGuard;
import com.touramigo.service.service.ContactService;
import com.touramigo.service.service.SupplierService;
import com.touramigo.service.service.UsersService;
import com.touramigo.service.service.impl.TermsAndConditionsService;
import java.security.Principal;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.touramigo.service.entity.enumeration.Permission.Authority.*;

@RestController
@RequestMapping("/suppliers")
public class SuppliersController {
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private AccessGuard accessGuard;

    @Autowired
    private ContactService contactService;

    @GetMapping
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + ADMINISTRATE_PARTNERS + OR + ADMINISTRATE_BOOKINGS + OR + MANAGE_USERS)
    public List<SupplierModel> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/active")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<SupplierModel> getActiveSuppliers() {
        return supplierService.getActiveSuppliers();
    }

    @GetMapping("/partner")
    @PreAuthorize(MANAGE_SUPPLIERS + OR + MANAGE_BOOKINGS)
    public List<SupplierModel> getCurrentUserPartnersSuppliers(Principal principal) {
        String userEmail = principal.getName();
        UserModel user = this.usersService.getUserByEmail(userEmail);
        return supplierService.getPartnerSuppliers(user.getPartnerId());
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS)
    public SupplierModel getOneSupplierById(@PathVariable UUID id) {
        SupplierModel supplier = supplierService.getSupplierById(id);
        accessGuard.verifyUserCanAccessResource(supplier.getPartners(), Permission.ADMINISTRATE_SUPPLIERS);
        return supplier;
    }

    @GetMapping("/code/{code}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS)
    public SupplierModel getOneSupplierByCode(@PathVariable String code) {
        SupplierModel supplier = supplierService.getSupplierByCode(code);
        accessGuard.verifyUserCanAccessResource(supplier.getPartners(), Permission.ADMINISTRATE_SUPPLIERS);
        return supplier;
    }

    @PostMapping
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public SupplierModel createSupplier(@RequestBody @Valid CreateUpdateSupplierModel model) {
        return supplierService.createSupplier(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public SupplierModel updateSupplier(@RequestBody @Valid CreateUpdateSupplierModel model, @PathVariable UUID id) {
        return supplierService.updateSupplier(model, id);
    }

    @GetMapping("/{id}/connections")
    @PreAuthorize(ADMINISTRATE_PARTNERS)
    public List<SupplierConnectionModel> getConnectionsBySupplierId(@PathVariable UUID id) {
        return supplierService.getConnectionsBySupplierId(id);
    }

    @PutMapping("/{id}/enable-disable")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public SupplierModel enableOrDisableUser(@PathVariable UUID id) {
        return supplierService.enableOrDisableUser(id);
    }

    @PostMapping("/{id}/terms-and-conditions")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public ResponseEntity<UUID> createTermsAndConditions(@PathVariable(name = "id") UUID supplierId, @Valid @RequestBody TermsAndConditionsCreateUpdateDto tacDto) {
        return ResponseEntity.ok(termsAndConditionsService.create(supplierId, tacDto).getId());
    }

    @PutMapping("/{id}/terms-and-conditions")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public ResponseEntity<UUID> updateTermsAndConditions(@PathVariable(name = "id") UUID supplierId, @Valid @RequestBody TermsAndConditionsCreateUpdateDto tacDto) {
        return ResponseEntity.ok(termsAndConditionsService.update(tacDto));
    }

    @PostMapping("/{supplierId}/terms-and-conditions/{tacId}/publish")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public ResponseEntity<UUID> publishTermsAndConditions(@PathVariable(name = "supplierId") UUID supplierId, @PathVariable(name = "tacId") UUID tacId) {
        termsAndConditionsService.publish(tacId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/terms-and-conditions")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS)
    public ResponseEntity<List<TermsAndConditionsViewDto>> getTermsAndConditionsList(@PathVariable(name = "id") UUID supplierId) {
        return ResponseEntity.ok(termsAndConditionsService.getList(supplierId));
    }

    @GetMapping("/{supplierId}/terms-and-conditions/latest")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS)
    public ResponseEntity<TermsAndConditionsViewDto> getLatestTermsAndConditions(@PathVariable(name = "supplierId") UUID supplierId) {
        SupplierModel supplier = supplierService.getSupplierById(supplierId);
        accessGuard.verifyUserCanAccessResource(supplier.getPartners(), Permission.ADMINISTRATE_SUPPLIERS);
        return ResponseEntity.ok(termsAndConditionsService.getLatest(supplierId));
    }

    @GetMapping("/{supplierId}/terms-and-conditions/{tacId}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS)
    public ResponseEntity<TermsAndConditionsViewDto> getTermsAndConditions(@PathVariable(name = "supplierId") UUID supplierId, @PathVariable(name = "tacId") UUID tacId) {
        SupplierModel supplier = supplierService.getSupplierById(supplierId);
        accessGuard.verifyUserCanAccessResource(supplier.getPartners(), Permission.ADMINISTRATE_SUPPLIERS);
        return ResponseEntity.ok(termsAndConditionsService.getOne(tacId));
    }

    @GetMapping("/contacts/")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS + OR + ADMINISTRATE_PARTNERS)
    public List<ContactModel> findAllContactsBySupplierId(@RequestParam(name = "supplierId") UUID supplierId) {
        return contactService.findAllBySupplierId(supplierId);
    }

    @PostMapping("/contacts/")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS + OR + ADMINISTRATE_PARTNERS)
    public ContactModel saveContact(@RequestBody ContactModel contactModel) {
        return contactService.save(contactModel);
    }

    @PutMapping("/contacts/{contactId}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS + OR + ADMINISTRATE_PARTNERS)
    public ContactModel updateContact(@PathVariable UUID contactId, @RequestBody ContactModel contactModel) {
        return contactService.update(contactId, contactModel);
    }

    @DeleteMapping("/contacts/{contactId}")
    @PreAuthorize(ADMINISTRATE_SUPPLIERS + OR + MANAGE_SUPPLIERS + OR + ADMINISTRATE_PARTNERS)
    public ResponseEntity<Void> deleteContactById(@PathVariable UUID contactId) {
        contactService.deleteById(contactId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
