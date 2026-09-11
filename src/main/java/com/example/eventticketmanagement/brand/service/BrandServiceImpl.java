package com.example.eventticketmanagement.brand.service;

import com.example.eventticketmanagement.brand.dto.BrandRequest;
import com.example.eventticketmanagement.brand.dto.BrandResponse;
import com.example.eventticketmanagement.brand.entity.Brand;
import com.example.eventticketmanagement.brand.repository.BrandRepository;
import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import com.example.eventticketmanagement.exception.GlobalExceptionHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.util.List;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private Clock indianClock;

    @Override
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(
            @Valid BrandRequest request) {

        LocalDateTime currentTime = LocalDateTime.now(indianClock);

        Brand brand = Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        Brand savedBrand = brandRepository.save(brand);

        BrandResponse brandResponse = BrandResponse.builder()
                .id(savedBrand.getId())
                .name(savedBrand.getName())
                .description(savedBrand.getDescription())
                .status(savedBrand.getStatus())
                .createdAt(savedBrand.getCreatedAt())
                .updatedAt(savedBrand.getUpdatedAt())
                .build();

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Brand created successfully")
                .build();

        ApiResponse<BrandResponse> response = ApiResponse.<BrandResponse>builder()
                .success(true)
                .data(brandResponse)
                .error(null)
                .meta(meta)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllBrands(
            int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Brand> brandPage;

        if (search == null || search.isBlank()) {

            brandPage = brandRepository.findAll(pageable);

        } else {

            brandPage = brandRepository
                    .findByNameContainingIgnoreCase(search, pageable);

            if (brandPage.isEmpty()) {
                throw new RuntimeException(
                        "No brand found with name: " + search
                );
            }
        }

        List<BrandResponse> brands = brandPage.getContent()
                .stream()
                .map(brand -> BrandResponse.builder()
                        .id(brand.getId())
                        .name(brand.getName())
                        .description(brand.getDescription())
                        .status(brand.getStatus())
                        .createdAt(brand.getCreatedAt())
                        .updatedAt(brand.getUpdatedAt())
                        .build())
                .toList();

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Brands retrieved successfully")
                .build();

        ApiResponse<List<BrandResponse>> response =
                ApiResponse.<List<BrandResponse>>builder()
                        .success(true)
                        .data(brands)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }
}