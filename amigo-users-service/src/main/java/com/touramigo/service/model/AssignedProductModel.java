package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.AssignedProduct;
import com.touramigo.service.entity.partner.PartnerProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignedProductModel {

    private ProductModel product;

    private Set<PermissionModel> permissions;

    private boolean active;

    public static AssignedProductModel create(AssignedProduct assignedProduct){
        AssignedProductModel model = new AssignedProductModel();
        model.product = ProductModel.create(assignedProduct.getProduct());
        model.permissions = assignedProduct.getPermissions()
                .stream().map(PermissionModel::create).collect(Collectors.toSet());
        return model;
    }
}