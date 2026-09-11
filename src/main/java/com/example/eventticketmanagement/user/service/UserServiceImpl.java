package com.example.eventticketmanagement.user.service;

import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import com.example.eventticketmanagement.user.dto.UserLoginRequest;
import com.example.eventticketmanagement.user.dto.UserRegistrationRequest;
import com.example.eventticketmanagement.user.dto.UserResponse;
import com.example.eventticketmanagement.user.entity.User;
import com.example.eventticketmanagement.user.entity.UserRole;
import com.example.eventticketmanagement.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Clock indianClock;

    @Override
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            UserRegistrationRequest request) {

        if (userRepository.existsByEmailIgnoreCase(
                request.getEmail())) {

            throw new RuntimeException(
                    "User already exists with email: "
                            + request.getEmail()
            );
        }

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(UserRole.USER)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        User savedUser =
                userRepository.save(user);

        UserResponse userResponse =
                buildUserResponse(savedUser);

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "User registered successfully"
                        )
                        .build();

        ApiResponse<UserResponse> response =
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .data(userResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(
            UserLoginRequest request) {

        User user =
                userRepository.findByEmailIgnoreCase(
                        request.getEmail()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        UserResponse userResponse =
                buildUserResponse(user);

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Login successful"
                        )
                        .build();

        ApiResponse<UserResponse> response =
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .data(userResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            int page,
            int size) {

        if (page < 0) {
            throw new RuntimeException(
                    "Page number cannot be negative"
            );
        }

        if (size <= 0) {
            throw new RuntimeException(
                    "Page size must be greater than 0"
            );
        }

        Pageable pageable =
                PageRequest.of(page, size);

        Page<User> userPage =
                userRepository.findAll(pageable);

        if (userPage.isEmpty()) {
            throw new RuntimeException(
                    "No users found"
            );
        }

        List<UserResponse> users =
                userPage.getContent()
                        .stream()
                        .map(this::buildUserResponse)
                        .toList();

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Users retrieved successfully"
                        )
                        .build();

        ApiResponse<List<UserResponse>> response =
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .data(users)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    private UserResponse buildUserResponse(
            User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}