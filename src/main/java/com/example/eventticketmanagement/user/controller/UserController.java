package com.example.eventticketmanagement.user.controller;

import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.user.dto.UserLoginRequest;
import com.example.eventticketmanagement.user.dto.UserRegistrationRequest;
import com.example.eventticketmanagement.user.dto.UserResponse;
import com.example.eventticketmanagement.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(
            @Valid @RequestBody UserLoginRequest request) {

        return userService.loginUser(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getAllUsers(page, size);
    }
}