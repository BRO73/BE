package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.ProductRequestDTO;
import com.example.demo_innocode.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO dto);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    void deleteProduct(Long id);
}
