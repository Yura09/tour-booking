package com.touramigo.service.model;

import com.touramigo.service.entity.partner.ConnectionValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConnectionValueModel {

    private UUID id;

    private String value;

    private UUID partnerId;

    private UUID supplierConnectionId;

    private String supplierConnectionKey;

    public static ConnectionValueModel create(ConnectionValue entity) {
        ConnectionValueModel model = new ConnectionValueModel();

        model.setId(entity.getId());

        if (Objects.nonNull(entity.getPartner())) {

            model.setPartnerId(entity.getPartner().getId());
        }

        if (Objects.nonNull(entity.getValue())) {
            model.setValue(entity.getValue());
        }

        if (Objects.nonNull(entity.getSupplierConnection())) {
            model.setSupplierConnectionKey(entity.getSupplierConnection().getKey());
            model.setSupplierConnectionId(entity.getSupplierConnection().getId());
        }

        return model;
    }
}
