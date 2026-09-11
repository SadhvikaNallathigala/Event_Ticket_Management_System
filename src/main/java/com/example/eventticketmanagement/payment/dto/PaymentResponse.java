package com.example.eventticketmanagement.payment.dto;

import com.example.eventticketmanagement.payment.entity.PaymentMethod;
import com.example.eventticketmanagement.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long paymentId;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal refundedAmount;
}