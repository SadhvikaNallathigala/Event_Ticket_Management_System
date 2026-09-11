package com.example.eventticketmanagement.event.dto;

import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.entity.EventType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EventSearchRequest {

    private int page = 0;

    private int size = 10;

    private String search;

    private String location;

    private LocalDate fromDate;

    private LocalDate toDate;

    private EventType eventType;

    private EventStatus status;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}