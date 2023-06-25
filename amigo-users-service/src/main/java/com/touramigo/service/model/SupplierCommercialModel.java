package com.touramigo.service.model;

import com.touramigo.service.entity.supplier.Supplier;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierCommercialModel {
    private UUID id;
    private String name;

    public static SupplierCommercialModel create(Supplier supplier) {
        SupplierCommercialModel supplierCommercialModel = new SupplierCommercialModel();
        if (Objects.nonNull(supplier.getId())) {
            supplierCommercialModel.setId(supplier.getId());
        }
        if (Objects.nonNull(supplier.getName())) {
            supplierCommercialModel.setName(supplier.getName());
        }
        return supplierCommercialModel;
    }
}
