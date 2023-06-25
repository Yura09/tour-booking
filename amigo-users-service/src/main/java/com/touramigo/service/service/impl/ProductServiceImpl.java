package com.touramigo.service.service.impl;

import com.touramigo.service.entity.global_config.Product;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.model.ProductModel;
import com.touramigo.service.repository.ProductRepository;
import com.touramigo.service.service.PermissionService;
import com.touramigo.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    /**
     * Auto wiring (automatically connect to Repos - PostgreSQL)
     */
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PermissionService permissionService;

    @Override
    public List<ProductModel> getAllProducts() {
        // Entity - products
        final List<Product> products = productRepository.findAll();

        // Map all the data from entiy to Model
        return products
                .stream()
                //:: means Function reference
                .map(ProductModel::create)
                .peek(p -> p.setAvailablePermissions(permissionService.getAllPermissionsFromProduct(p.getId()).size()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductModel updateProduct(UUID id, ProductModel model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new UnexistedDataException("Product not found"));
        product.setName(model.getName());
        product.setDescription(model.getDescription());
        ProductModel toSave = ProductModel.create(productRepository.save(product));
        toSave.setAvailablePermissions(permissionService.getAllPermissionsFromProduct(product.getId()).size());
        return toSave;
    }

}
