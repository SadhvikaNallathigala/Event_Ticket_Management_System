package com.example.eventticketmanagement.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CancelBookingRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    @NotNull(message = "Number of tickets to cancel is required")
    @Positive(message = "Number of tickets to cancel must be positive")
    private Integer numberOfTickets;
}