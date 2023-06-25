package com.touramigo.service.controller;

import com.touramigo.service.model.ProductModel;
import com.touramigo.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.touramigo.service.entity.enumeration.Permission.Authority.*;

@RestController
@RequestMapping("/products")
@PreAuthorize(ADMINISTRATE_GLOBAL + OR + ADMINISTRATE_PARTNERS)
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductModel> getAllProducts(){
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public ProductModel updateProduct(@PathVariable UUID id, @RequestBody ProductModel body) {
        return productService.updateProduct(id,body);
    }
}
