package com.example.eventticketmanagement.brand.dto;

import com.example.eventticketmanagement.brand.entity.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    private String name;

    @NotBlank(message = "Brand description is required")
    private String description;

    @NotNull(message = "Brand status is required")
    private BrandStatus status;
}