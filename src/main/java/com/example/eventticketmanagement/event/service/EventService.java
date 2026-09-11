package com.example.eventticketmanagement.event.service;

import com.example.eventticketmanagement.event.dto.EventRequest;
import com.example.eventticketmanagement.event.dto.EventResponse;
import com.example.eventticketmanagement.event.dto.EventSearchRequest;
import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.event.dto.EventUpdateRequest;
import org.springframework.http.ResponseEntity;



public interface EventService {

    ResponseEntity<ApiResponse<EventResponse>> createEvent(
            EventRequest request);

    ResponseEntity<ApiResponse<?>> getEvents(
            EventSearchRequest request);
    ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            Long eventId,
            EventUpdateRequest request);

    ResponseEntity<ApiResponse<EventResponse>> cancelEvent(
            Long eventId);
}