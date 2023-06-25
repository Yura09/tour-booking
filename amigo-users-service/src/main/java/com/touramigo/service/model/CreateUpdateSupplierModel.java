package com.touramigo.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class CreateUpdateSupplierModel {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Code is required")
    @Size(min = 1)
    private String codeVariable;

    private String address;

    private String zip;

    private String city;

    private String country;

    private List<String> emails;

    private List<String> contactPhones;

    private String websiteUrl;

    private String supplierType;

    private UUID accountManagerId;

    private String termsAndConditionsUrl;

}
