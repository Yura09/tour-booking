package com.touramigo.service.model;

import com.touramigo.service.entity.enumeration.CompanyBusinessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdatePartnerModel {

    @NotNull(message = "Name required")
    private String name;

    private String address;

    private String zip;

    List<String> emails;

    List<String> contactPhones;

    private String timeZone;

    private PartnerGroupModel companyGroup;

    private CompanyBusinessType businessType;

    private UUID accountManagerId;

    private UUID partnerGroupId;

}
