package com.example.eventticketmanagement.event.dto;

import com.example.eventticketmanagement.event.entity.EventType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventRequest {

    @NotNull(message = "Brand ID is required")
    @Positive(message = "Brand ID must be positive")
    private Long brandId;

    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date cannot be in the past")
    private LocalDate eventDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    private String description;

    @NotNull(message = "Age limit is required")
    @Min(value = 0, message = "Age limit cannot be negative")
    private Integer ageLimit;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @NotNull(message = "VIP price is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "VIP price must be greater than 0"
    )
    private BigDecimal vipPrice;

    @NotNull(message = "Gold price is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Gold price must be greater than 0"
    )
    private BigDecimal goldPrice;

    @NotNull(message = "Normal price is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Normal price must be greater than 0"
    )
    private BigDecimal normalPrice;

    @NotNull(message = "VIP seat count is required")
    @Min(
            value = 1,
            message = "VIP seat count must be at least 1"
    )
    private Integer vipSeats;

    @NotNull(message = "Gold seat count is required")
    @Min(
            value = 1,
            message = "Gold seat count must be at least 1"
    )
    private Integer goldSeats;

    @NotNull(message = "Normal seat count is required")
    @Min(
            value = 1,
            message = "Normal seat count must be at least 1"
    )
    private Integer normalSeats;

    private BigDecimal comboPriceForTwo;

    private BigDecimal comboPriceForFour;
}