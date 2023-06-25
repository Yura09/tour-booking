package com.touramigo.service.model;

import com.touramigo.service.entity.partner.Partner;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerCommercialModel {
    private UUID id;
    private String name;
    private Set<SupplierCommercialModel> supplierCommercialModels;

    public static PartnerCommercialModel create(Partner partner) {
        PartnerCommercialModel partnerCommercialModel = new PartnerCommercialModel();
        if (Objects.nonNull(partner.getId())) {
            partnerCommercialModel.setId(partner.getId());
        }
        if (Objects.nonNull(partner.getName())) {
            partnerCommercialModel.setName(partner.getName());
        }
        if (Objects.nonNull(partner.getSuppliers())) {
            partnerCommercialModel.setSupplierCommercialModels(partner.getSuppliers().stream().map(
                SupplierCommercialModel::create).collect(Collectors.toSet()));
        }
        return partnerCommercialModel;
    }
}
