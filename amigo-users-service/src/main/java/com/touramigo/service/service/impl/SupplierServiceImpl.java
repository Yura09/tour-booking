package com.touramigo.service.service.impl;

import com.touramigo.service.entity.enumeration.SupplierType;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.supplier.TermsAndConditions;
import com.touramigo.service.entity.supplier.TermsAndConditionsType;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.DataConflictException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.exception.UserServiceErrorMessages;
import com.touramigo.service.model.CreateUpdateSupplierModel;
import com.touramigo.service.model.SupplierConnectionModel;
import com.touramigo.service.model.SupplierModel;
import com.touramigo.service.model.TermsAndConditionsCreateUpdateDto;
import com.touramigo.service.repository.PartnerRepository;
import com.touramigo.service.repository.SupplierRepository;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.service.SupplierService;
import com.touramigo.service.service.UsersService;
import com.touramigo.service.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class SupplierServiceImpl implements SupplierService {


    private static final String SEPARATOR = ",";
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private TermsAndConditionsService tacService;
    @Autowired
    private UsersService usersService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<SupplierModel> getAllSuppliers() {
        List<UUID> userIdsFromCurrentPartner = userRepository.findByPartnerId(SecurityUtil.getPartnerId()).map(User::getId)
            .collect(Collectors.toList());

        return supplierRepository.findAll().stream()
            .peek(sup -> sup.setContacts(sup.getContacts()
                .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
                .collect(Collectors.toSet())))
            .peek(sup -> sup.setNotes(sup.getNotes()
                .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
                .collect(Collectors.toSet()))).map(SupplierModel::create)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplierModel> getActiveSuppliers() {
        return supplierRepository.findAllByEnabledTrue()
            .stream()
            .map(SupplierModel::create)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierModel> getPartnerSuppliers(UUID partnerId) {
        Partner partner = this.partnerRepository.findById(partnerId).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.PARTNER_NOT_FOUND));

        List<UUID> userIdsFromCurrentPartner = partner.getUsers().stream().map(User::getId)
            .collect(Collectors.toList());

        if (Objects.nonNull(partner.getSuppliers())) {
            return partner.getSuppliers().stream()
                .peek(sup -> sup.setContacts(sup.getContacts()
                    .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
                    .collect(Collectors.toSet())))
                .peek(sup -> sup.setNotes(sup.getNotes()
                    .stream().filter(n -> userIdsFromCurrentPartner.contains(n.getCreatedBy()))
                    .collect(Collectors.toSet())))
                .map(SupplierModel::create).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public SupplierModel getSupplierById(UUID id) {
        final Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));

        return SupplierModel.create(supplier);
    }

    @Override
    public SupplierModel getSupplierByCode(String code) {
        final Supplier supplier = supplierRepository.findByCodeVariable(code)
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));

        return SupplierModel.create(supplier);
    }

    @Override
    public List<SupplierType> getAllSupplierTypes() {
        return Arrays.asList(SupplierType.values());
    }

    @Override
    @Transactional
    public SupplierModel createSupplier(CreateUpdateSupplierModel model) {
        validateCreateModel(model);

        Supplier supplier = this.initSupplier(new Supplier(), model);
        supplier = supplierRepository.save(supplier);

        if (nonNull(model.getTermsAndConditionsUrl())) {
            TermsAndConditionsCreateUpdateDto tac = new TermsAndConditionsCreateUpdateDto();
            tac.setType(TermsAndConditionsType.URL);
            tac.setContent(model.getTermsAndConditionsUrl());
            TermsAndConditions createdTac = tacService.create(supplier.getId(), tac);
            // this workaround works as manual entity update before turning it to a dto
            supplier.getTermsAndConditionsList().add(createdTac);
        }

        return SupplierModel.create(supplier);
    }


    @Override
    @Transactional
    public SupplierModel updateSupplier(CreateUpdateSupplierModel model, UUID id) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));
        validateUpdateModel(supplier, model);

        supplier = this.initSupplier(supplier, model);
        supplier = supplierRepository.save(supplier);

        Optional<TermsAndConditions> tacUrlOptional = supplier.getTermsAndConditionsList()
            .stream()
            .filter(it -> it.getType().equals(TermsAndConditionsType.URL))
            .findFirst();
        if (nonNull(model.getTermsAndConditionsUrl())) {
            if (tacUrlOptional.isPresent()) {
                TermsAndConditions tacUrl = tacUrlOptional.get();
                tacService.update(new TermsAndConditionsCreateUpdateDto(
                    tacUrl.getId(),
                    tacUrl.getType(),
                    tacUrl.getDescription(),
                    model.getTermsAndConditionsUrl()
                ));
            } else {
                TermsAndConditions createdTac = tacService.create(supplier.getId(), new TermsAndConditionsCreateUpdateDto(
                    null,
                    TermsAndConditionsType.URL,
                    null,
                    model.getTermsAndConditionsUrl()
                ));
                // this workaround works as manual entity update before turning it to a dto
                supplier.getTermsAndConditionsList().add(createdTac);
            }
        }

        return SupplierModel.create(supplier);
    }

    @Override
    public List<SupplierConnectionModel> getConnectionsBySupplierId(UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.SUPPLIER_NOT_FOUND));

        if (Objects.nonNull(supplier.getConnectionKeys()) && !supplier.getConnectionKeys().isEmpty()) {
            return supplier.getConnectionKeys().stream().map(SupplierConnectionModel::create).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public SupplierModel enableOrDisableUser(UUID id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND));
        if (supplier.isEnabled()) {
            supplier.setEnabled(false);
        } else {
            supplier.setEnabled(true);
        }
        return SupplierModel.create(supplierRepository.save(supplier));
    }


    @Transactional
    public Supplier initSupplier(Supplier entity, CreateUpdateSupplierModel model) {
        entity.setName(model.getName());
        entity.setCodeVariable(model.getCodeVariable());

        if (nonNull(model.getSupplierType()) && !model.getSupplierType().isEmpty()) {
            entity.setSupplierType(SupplierType.valueOf(model.getSupplierType()));
        }

        if (nonNull(model.getAddress())) {
            entity.setAddress(model.getAddress());
        }

        if (nonNull(model.getZip())) {
            entity.setZip(model.getZip());
        }

        if (nonNull(model.getCity())) {
            entity.setCity(model.getCity());
        }

        if (nonNull(model.getCountry())) {
            entity.setCountry(model.getCountry());
        }

        if (nonNull(model.getEmails()) && !model.getEmails().isEmpty()) {
            String joined =
                model.getEmails().stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(SEPARATOR));
            if (!joined.isEmpty()) {
                entity.setEmails(joined);
            }
        }

        if (nonNull(model.getContactPhones()) && !model.getContactPhones().isEmpty()) {
            String joined =
                model.getContactPhones().stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(SEPARATOR));
            if (!joined.isEmpty()) {
                entity.setContactPhones(joined);
            }
        }

        if (nonNull(model.getWebsiteUrl())) {
            entity.setWebsiteUrl(model.getWebsiteUrl());
        }

        if (nonNull(model.getAccountManagerId())) {
            entity.setAccountManager(usersService.findOneById(model.getAccountManagerId())
                .orElseThrow(() -> new UnexistedDataException(UserServiceErrorMessages.USER_IS_NOT_FOUND)));
        } else {
            entity.setAccountManager(null);
        }

        return entity;
    }

    private void validateCreateModel(CreateUpdateSupplierModel model) {
        if (supplierRepository.existsByNameIgnoreCase(model.getName())) {
            throw new DataConflictException("Supplier with name " + model.getName() + " already exists!");
        }

        if (supplierRepository.existsByCodeVariableIgnoreCase(model.getCodeVariable())) {
            throw new DataConflictException("Supplier with code " + model.getCodeVariable() + " already exists!");
        }
    }

    private void validateUpdateModel(Supplier supplier, CreateUpdateSupplierModel model) {
        if (supplierRepository.existsByNameIgnoreCaseAndIdNot(model.getName(), supplier.getId())) {
            throw new DataConflictException("Supplier with name " + model.getName() + " already exists!");
        }

        if (supplierRepository.existsByCodeVariableIgnoreCaseAndIdNot(model.getCodeVariable(), supplier.getId())) {
            throw new DataConflictException("Supplier with code " + model.getCodeVariable() + " already exists!");
        }
    }
}
