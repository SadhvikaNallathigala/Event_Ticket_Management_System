package com.example.eventticketmanagement.user.service;

import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.user.dto.UserLoginRequest;
import com.example.eventticketmanagement.user.dto.UserRegistrationRequest;
import com.example.eventticketmanagement.user.dto.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<ApiResponse<UserResponse>> registerUser(
            UserRegistrationRequest request
    );

    ResponseEntity<ApiResponse<UserResponse>> loginUser(
            UserLoginRequest request
    );

    ResponseEntity<ApiResponse<?>> getAllUsers(
            int page,
            int size
    );
}