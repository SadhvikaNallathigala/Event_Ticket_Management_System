package com.example.eventticketmanagement.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;
    private MetaResponse meta;
}