package com.example.eventticketmanagement.payment.dto;

import com.example.eventticketmanagement.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}