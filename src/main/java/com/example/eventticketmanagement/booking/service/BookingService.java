package com.example.eventticketmanagement.booking.service;

import com.example.eventticketmanagement.booking.dto.BookingRequest;
import com.example.eventticketmanagement.booking.dto.BookingResponse;
import com.example.eventticketmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import com.example.eventticketmanagement.booking.dto.CancelBookingRequest;

public interface BookingService {

    ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            BookingRequest request);

    ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            Long bookingId);

    ResponseEntity<ApiResponse<?>> getBookingHistory(
            Long userId,
            int page,
            int size);

    ResponseEntity<ApiResponse<?>> cancelBooking(
            CancelBookingRequest request);
}