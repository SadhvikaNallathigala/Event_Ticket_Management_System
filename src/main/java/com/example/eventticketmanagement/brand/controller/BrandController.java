package com.example.eventticketmanagement.brand.controller;

import com.example.eventticketmanagement.brand.dto.BrandRequest;
import com.example.eventticketmanagement.brand.dto.BrandResponse;
import com.example.eventticketmanagement.brand.service.BrandService;
import com.example.eventticketmanagement.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(
            @Valid @RequestBody BrandRequest request) {

        return brandService.createBrand(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        return brandService.getAllBrands(page, size, search);
    }
}