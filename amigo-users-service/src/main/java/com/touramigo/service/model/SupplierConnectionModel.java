package com.touramigo.service.model;


import com.touramigo.service.entity.supplier.SupplierConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierConnectionModel {

    private UUID id;

    private String key;

    public static SupplierConnectionModel create(SupplierConnection supplierConnection) {
        SupplierConnectionModel model = new SupplierConnectionModel();
        model.setId(supplierConnection.getId());
        model.setKey(supplierConnection.getKey());

        return model;
    }

}
