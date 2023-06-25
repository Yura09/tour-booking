package com.touramigo.service.model;

import com.touramigo.service.entity.partner.PartnerGroup;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCommercialModel {
    private UUID id;
    private String name;
    private List<PartnerCommercialModel> partnerCommercialModels;

    public static GroupCommercialModel create(PartnerGroup partnerGroup) {
        GroupCommercialModel groupCommercialModel = new GroupCommercialModel();
        if (Objects.nonNull(partnerGroup.getId())) {
            groupCommercialModel.setId(partnerGroup.getId());
        }
        if (Objects.nonNull(partnerGroup.getName())) {
            groupCommercialModel.setName(partnerGroup.getName());
        }
        if (Objects.nonNull(partnerGroup.getPartners())) {
            groupCommercialModel.setPartnerCommercialModels(
                partnerGroup.getPartners().stream().map(
                    PartnerCommercialModel::create).collect(Collectors.toList()));
        }
        return groupCommercialModel;
    }
}
