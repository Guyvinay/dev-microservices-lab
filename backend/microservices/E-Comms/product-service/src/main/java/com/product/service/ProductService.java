package com.product.service;

import com.product.dto.ProductRequest;
import com.product.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    public ProductResponse createProduct(ProductRequest productRequest);
    public List<ProductResponse> getAllProducts();
    public ProductResponse getProductById(UUID id);

    public ProductResponse updateProduct(ProductRequest productRequest);

    public ProductResponse deleteProduct(UUID id);
}
