package com.example.eventticketmanagement.event.controller;

import com.example.eventticketmanagement.event.dto.EventRequest;
import com.example.eventticketmanagement.event.dto.EventResponse;
import com.example.eventticketmanagement.event.dto.EventSearchRequest;
import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.entity.EventType;
import com.example.eventticketmanagement.event.service.EventService;
import com.example.eventticketmanagement.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eventticketmanagement.event.dto.EventUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> create(
            @Valid @RequestBody EventRequest request) {

        return eventService.createEvent(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        EventSearchRequest request = new EventSearchRequest();

        request.setPage(page);
        request.setSize(size);
        request.setSearch(search);
        request.setLocation(location);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setEventType(eventType);
        request.setStatus(status);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);

        return eventService.getEvents(request);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> update(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequest request) {

        return eventService.updateEvent(eventId, request);
    }

    @PutMapping("/{eventId}/cancel")
    public ResponseEntity<ApiResponse<EventResponse>> cancel(
            @PathVariable Long eventId) {

        return eventService.cancelEvent(eventId);
    }
}