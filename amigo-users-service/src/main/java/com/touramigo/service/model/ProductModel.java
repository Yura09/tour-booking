package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private UUID id;

    private String name;

    private String description;

    /**
     * Should this be the same as CodeKey with Permissions ??
     * @author Quang Van Tran
     */
    private String codeVariable;

    private Integer availablePermissions;

    public static ProductModel create(Product product){
        ProductModel productModel = new ProductModel();
        productModel.setId(product.getId());
        productModel.setName(product.getName());
        productModel.setDescription(product.getDescription());
        productModel.setCodeVariable(product.getCodeVariable());
        return productModel;
    }
}
