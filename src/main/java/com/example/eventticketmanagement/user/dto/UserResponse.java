package com.example.eventticketmanagement.user.dto;

import com.example.eventticketmanagement.user.entity.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}