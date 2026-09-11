package com.example.eventticketmanagement.brand.service;

import com.example.eventticketmanagement.brand.dto.BrandRequest;
import com.example.eventticketmanagement.brand.dto.BrandResponse;
import com.example.eventticketmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface BrandService {

    ResponseEntity<ApiResponse<BrandResponse>> createBrand(BrandRequest request);

    ResponseEntity<ApiResponse<?>> getAllBrands(int page, int size, String search);
}