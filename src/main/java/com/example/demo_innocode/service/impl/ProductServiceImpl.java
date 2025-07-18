package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.dto.request.ProductRequestDTO;
import com.example.demo_innocode.dto.response.ProductResponseDTO;
import com.example.demo_innocode.entity.Product;
import com.example.demo_innocode.entity.User;
import com.example.demo_innocode.repository.ProductRepository;
import com.example.demo_innocode.repository.UserRepository;
import com.example.demo_innocode.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .type(product.getType())
                .image(product.getImage())
                .featured(product.getFeatured())
                .village(product.getVillage())
                .userId(product.getUser() != null ? product.getUser().getId() : null)
                .build();
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        // Lấy user từ userId
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getUserId()));
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .type(dto.getType())
                .image(dto.getImage())
                .featured(dto.getFeatured() != null ? dto.getFeatured() : false)
                .village(dto.getVillage())
                .user(user)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
