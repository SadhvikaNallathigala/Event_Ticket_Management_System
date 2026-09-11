package com.example.eventticketmanagement.booking.controller;

import com.example.eventticketmanagement.booking.dto.BookingRequest;
import com.example.eventticketmanagement.booking.dto.BookingResponse;
import com.example.eventticketmanagement.booking.service.BookingService;
import com.example.eventticketmanagement.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eventticketmanagement.booking.dto.CancelBookingRequest;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @Valid @RequestBody BookingRequest request) {

        return bookingService.createBooking(request);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> get(
            @PathVariable Long bookingId) {

        return bookingService.getBooking(bookingId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> history(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return bookingService.getBookingHistory(
                userId, page, size);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<?>> cancel(
            @Valid @RequestBody CancelBookingRequest request) {

        return bookingService.cancelBooking(request);
    }
}