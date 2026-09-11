package com.example.eventticketmanagement.event.dto;

import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.entity.EventType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class EventResponse {

    private Long id;

    private Long brandId;

    private String eventName;

    private LocalDate eventDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private EventType eventType;

    private String description;

    private Integer ageLimit;

    private LocalTime closingTime;

    private BigDecimal vipPrice;

    private BigDecimal goldPrice;

    private BigDecimal normalPrice;

    private Integer vipSeats;

    private Integer goldSeats;

    private Integer normalSeats;

    private BigDecimal comboPriceForTwo;

    private BigDecimal comboPriceForFour;

    private EventStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}