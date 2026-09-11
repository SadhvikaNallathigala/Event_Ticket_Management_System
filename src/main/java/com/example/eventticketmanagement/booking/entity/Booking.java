package com.example.eventticketmanagement.booking.entity;

import com.example.eventticketmanagement.event.entity.Event;
import com.example.eventticketmanagement.seat.entity.SeatType;
import com.example.eventticketmanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Integer numberOfTickets;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime paymentDeadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}