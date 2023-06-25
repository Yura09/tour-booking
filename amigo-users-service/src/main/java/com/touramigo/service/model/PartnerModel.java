package com.touramigo.service.model;


import com.touramigo.service.entity.note.Note;
import com.touramigo.service.entity.partner.ConnectionValue;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerModel {

    List<String> emails;
    List<String> contactPhones;
    List<UUID> users;
    List<UUID> suppliers;
    List<UUID> connectionValues;
    private UUID id;
    private String name;
    private String address;
    private String city;
    private String country;
    private String zip;
    private String website;
    private String timezone;
    private boolean master;
    private boolean enabled;
    private String businessType;
    private UserModel taContact;
    private PartnerGroupModel companyGroup;
    private AccountManagerModel accountManager;

    private UUID accountManagerId;

    private PartnerGroupModel partnerGroup;

    private UUID partnerGroupId;

    private List<UUID> notes;

    private boolean bookingsEnabled;

    public static PartnerModel create(Partner partner) {
        PartnerModel model = new PartnerModel();
        model.setId(partner.getId());
        model.setName(partner.getName());
        model.setAddress(partner.getAddress());
        model.setZip(partner.getZip());
        model.setMaster(partner.isMaster());
        model.setEnabled(partner.isEnabled());
        model.setCity(partner.getCity());
        model.setCountry(partner.getCountry());
        model.setWebsite(partner.getWebsite());
        model.setTimezone(partner.getTimezone());
        model.setBookingsEnabled(partner.isBookingsEnabled());

        if (Objects.nonNull(partner.getTaContact())) {
            model.setTaContact(UserModel.create(partner.getTaContact()));
        }
        if (Objects.nonNull(partner.getPartnerGroup())) {
            model.setCompanyGroup(PartnerGroupModel.create(partner.getPartnerGroup()));
        }
        if (Objects.nonNull(partner.getBusinessType())) {
            model.setBusinessType(partner.getBusinessType().toString());
        }
        if (Objects.nonNull(partner.getEmails())) {
            model.setEmails(Arrays.asList(partner.getEmails().split(",")));
        }
        if (Objects.nonNull(partner.getContactPhones())) {
            model.setContactPhones(Arrays.asList(partner.getContactPhones().split(",")));
        }
        if (Objects.nonNull(partner.getUsers())) {
            model.setUsers(partner.getUsers().stream().map(UserModel::create).map(UserModel::getId).collect(Collectors.toList()));
        } else {
            model.setUsers(new ArrayList<>());
        }

        if (Objects.nonNull(partner.getSuppliers())) {
            model.setSuppliers(partner.getSuppliers().stream().map(Supplier::getId).collect(Collectors.toList()));
        } else {
            model.setSuppliers(new ArrayList<>());
        }

        if (Objects.nonNull(partner.getConnectionValues())) {
            model.setConnectionValues(partner.getConnectionValues().stream().map(ConnectionValue::getId).collect(Collectors.toList()));
        } else {
            model.setConnectionValues(new ArrayList<>());
        }

        if (Objects.nonNull(partner.getPartnerGroup())) {
            model.setPartnerGroup(PartnerGroupModel.create(partner.getPartnerGroup()));
        }

        if (Objects.nonNull(partner.getAccountManager())) {
            model.setAccountManager(AccountManagerModel.create(partner.getAccountManager()));
        }
        if (Objects.nonNull(partner.getNotes())) {
            model.setNotes(partner.getNotes().stream().map(Note::getId).collect(Collectors.toList()));
        }

        return model;
    }

}
