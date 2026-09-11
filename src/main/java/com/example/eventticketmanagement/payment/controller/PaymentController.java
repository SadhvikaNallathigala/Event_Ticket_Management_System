package com.example.eventticketmanagement.payment.controller;

import com.example.eventticketmanagement.payment.dto.PaymentRequest;
import com.example.eventticketmanagement.payment.dto.PaymentResponse;
import com.example.eventticketmanagement.payment.service.PaymentService;
import com.example.eventticketmanagement.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.makePayment(request);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long paymentId) {

        return paymentService.getPayment(paymentId);
    }
}