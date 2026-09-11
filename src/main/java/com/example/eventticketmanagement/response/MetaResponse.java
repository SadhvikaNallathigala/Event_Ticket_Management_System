package com.example.eventticketmanagement.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MetaResponse {

    private LocalDateTime timestamp;
    private String message;
}