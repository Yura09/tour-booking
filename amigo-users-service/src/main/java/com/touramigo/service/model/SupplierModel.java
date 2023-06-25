package com.touramigo.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.touramigo.service.entity.contact.Contact;
import com.touramigo.service.entity.note.Note;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.supplier.SupplierConnection;
import com.touramigo.service.entity.supplier.TermsAndConditionsState;
import com.touramigo.service.entity.supplier.TermsAndConditionsType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierModel {

    private UUID id;

    private String name;

    private String address;

    private String zip;

    private String city;

    private String country;

    private List<String> emails;

    private List<String> contactPhones;

    private String websiteUrl;

    private boolean enabled;

    private String supplierType;

    private List<UUID> connectionKeys;

    private List<UUID> partners;

    private String codeVariable;

    private boolean hasTermsAndConditions;

    @JsonIgnore
    private boolean hasPublishedTermsAndConditions;

    private String termsAndConditionsUrl;

    private AccountManagerModel accountManager;

    private List<UUID> contacts;

    private List<UUID> notes;

    public static SupplierModel create(Supplier supplier) {
        SupplierModel model = new SupplierModel();
        model.setId(supplier.getId());
        model.setName(supplier.getName());

        model.setEnabled(supplier.isEnabled());


        if (Objects.nonNull(supplier.getAddress())) {
            model.setAddress(supplier.getAddress());
        }

        if (Objects.nonNull(supplier.getZip())) {
            model.setZip(supplier.getZip());
        }


        if (Objects.nonNull(supplier.getCity())) {
            model.setCity(supplier.getCity());
        }

        if (Objects.nonNull(supplier.getCountry())) {
            model.setCountry(supplier.getCountry());
        }

        if (Objects.nonNull(supplier.getEmails())) {
            model.setEmails(Arrays.asList(supplier.getEmails().split(",")));
        }

        if (Objects.nonNull(supplier.getContactPhones())) {
            model.setContactPhones(Arrays.asList(supplier.getContactPhones().split(",")));
        }

        if (Objects.nonNull(supplier.getWebsiteUrl())) {
            model.setWebsiteUrl(supplier.getWebsiteUrl());
        }

        if (Objects.nonNull(supplier.getSupplierType())) {
            model.setSupplierType(String.valueOf(supplier.getSupplierType()));
        }

        if (Objects.nonNull(supplier.getConnectionKeys()) && !supplier.getConnectionKeys().isEmpty()) {
            model.setConnectionKeys(supplier.getConnectionKeys().stream().map(SupplierConnection::getId).collect(Collectors.toList()));
        }

        if (Objects.nonNull(supplier.getPartners())) {
            model.setPartners(supplier.getPartners().stream().map(Partner::getId).collect(Collectors.toList()));
        }

        if (Objects.nonNull(supplier.getAccountManager())) {
            model.setAccountManager(AccountManagerModel.create(supplier.getAccountManager()));
        }

        model.setCodeVariable(supplier.getCodeVariable());

        model.hasTermsAndConditions = !CollectionUtils.isEmpty(supplier.getTermsAndConditionsList());

        model.hasPublishedTermsAndConditions = model.hasTermsAndConditions && supplier
            .getTermsAndConditionsList()
            .stream()
            .anyMatch(it -> it.getState().equals(TermsAndConditionsState.PUBLISHED));

        if (Objects.nonNull(supplier.getTermsAndConditionsList()) && !supplier.getTermsAndConditionsList().isEmpty()) {
            supplier.getTermsAndConditionsList().stream()
                .filter(it -> it.getType().equals(TermsAndConditionsType.URL))
                .findFirst()
                .ifPresent(it -> model.setTermsAndConditionsUrl(it.getContent()));
        }
        if (Objects.nonNull(supplier.getContacts())) {
            model.setContacts(supplier.getContacts().stream().map(Contact::getId).collect(Collectors.toList()));
        }
        if (Objects.nonNull(supplier.getNotes())) {
            model.setNotes(supplier.getNotes().stream().map(Note::getId).collect(Collectors.toList()));
        }
        return model;
    }
}
