package com.example.eventticketmanagement.brand.dto;

import com.example.eventticketmanagement.brand.entity.BrandStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BrandResponse {

    private Long id;

    private String name;

    private String description;

    private BrandStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}