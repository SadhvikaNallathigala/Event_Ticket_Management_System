package com.example.eventticketmanagement.payment.service;

import com.example.eventticketmanagement.payment.dto.PaymentRequest;
import com.example.eventticketmanagement.payment.dto.PaymentResponse;
import com.example.eventticketmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    ResponseEntity<ApiResponse<PaymentResponse>> makePayment(
            PaymentRequest request
    );

    ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            Long paymentId
    );
}