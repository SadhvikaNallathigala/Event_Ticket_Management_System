package com.example.eventticketmanagement.booking.dto;

import com.example.eventticketmanagement.booking.entity.BookingStatus;
import com.example.eventticketmanagement.seat.entity.SeatType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {

    private Long bookingId;

    private Long userId;

    private Long eventId;

    private String eventName;

    private SeatType seatType;

    private Integer numberOfTickets;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private LocalDateTime paymentDeadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}