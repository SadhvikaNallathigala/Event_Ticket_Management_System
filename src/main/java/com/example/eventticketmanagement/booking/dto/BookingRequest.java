package com.example.eventticketmanagement.booking.dto;

import com.example.eventticketmanagement.seat.entity.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Event ID is required")
    @Positive(message = "Event ID must be positive")
    private Long eventId;

    @NotNull(message = "Seat type is required")
    private SeatType seatType;

    @NotNull(message = "Number of tickets is required")
    @Min(value = 1, message = "Number of tickets must be at least 1")
    private Integer numberOfTickets;
}