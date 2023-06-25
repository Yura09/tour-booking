package com.touramigo.service.service;

import com.touramigo.service.model.ProductModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductModel> getAllProducts();

    @Transactional
    ProductModel updateProduct(UUID id, ProductModel model);
}
