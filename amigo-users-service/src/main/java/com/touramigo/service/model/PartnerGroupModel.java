package com.touramigo.service.model;

import com.touramigo.service.entity.partner.PartnerGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class PartnerGroupModel {

    private UUID id;

    @NotNull
    private String name;


    public static PartnerGroupModel create(PartnerGroup partnerGroup) {
        PartnerGroupModel model = new PartnerGroupModel();
        model.setId(partnerGroup.getId());
        model.setName(partnerGroup.getName());
        return model;
    }
}
