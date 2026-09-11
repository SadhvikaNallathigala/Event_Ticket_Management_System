package com.example.eventticketmanagement.exception;

import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock indianClock;

    public GlobalExceptionHandler(Clock indianClock) {
        this.indianClock = indianClock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Validation failed")
                .build();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .data(null)
                .error(errorMessage)
                .meta(meta)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParseException(
            HttpMessageNotReadableException exception) {

        String errorMessage = "Invalid value provided for request field";

        if (exception.getMessage() != null &&
                exception.getMessage().contains("BrandStatus")) {
            errorMessage = "Brand status must be ACTIVE or INACTIVE";
        }

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Invalid request")
                .build();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .data(null)
                .error(errorMessage)
                .meta(meta)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Request failed";

        if (exception.getMessage() != null) {

            if (exception.getMessage().startsWith("No brand found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Brand not found";

            } else if (exception.getMessage().startsWith("No events found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Events not found";

            } else if (exception.getMessage().startsWith("User already exists")) {
                status = HttpStatus.CONFLICT;
                message = "User already exists";
            } else if (exception.getMessage().equals("Invalid email or password")) {
                status = HttpStatus.UNAUTHORIZED;
                message = "Login failed";
            } else if (exception.getMessage().startsWith("No user found")) {
                status = HttpStatus.NOT_FOUND;
                message = "User not found";

            } else if (exception.getMessage().startsWith("No event found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Event not found";

            } else if (exception.getMessage().startsWith("No user found")) {
                status = HttpStatus.NOT_FOUND;
                message = "User not found";

            } else if (exception.getMessage().startsWith("No event found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Event not found";

            } else if (
                    exception.getMessage().startsWith("Booking is available") ||
                            exception.getMessage().startsWith("Only ")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid booking request";
            }
            else if (exception.getMessage().startsWith("No booking found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Booking not found";

            } else if (exception.getMessage().startsWith("No bookings found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Booking history not found";

            } else if (exception.getMessage().startsWith(
                    "No confirmed booking found")) {

                status = HttpStatus.NOT_FOUND;
                message = "Confirmed booking not found";

            } else if (
                    exception.getMessage().startsWith("Event ID")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid booking request";
            } else if (exception.getMessage().startsWith(
                    "Booking does not belong")) {

                status = HttpStatus.FORBIDDEN;
                message = "Booking does not belong to user";

            } else if (exception.getMessage().startsWith(
                    "Event cannot be created because the brand is inactive")) {

                status = HttpStatus.BAD_REQUEST;
                message = "Event cannot be created for an inactive brand";
            } else if (
                    exception.getMessage().startsWith(
                            "Cancelled event cannot be updated")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Cancelled event cannot be updated";

            } else if (
                    exception.getMessage().startsWith(
                            "Event is already cancelled")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Event is already cancelled";
            } else if (exception.getMessage().startsWith("No users found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Users not found";
            } else if (exception.getMessage().startsWith("No payment found")) {
                status = HttpStatus.NOT_FOUND;
                message = "Payment not found";
            } else if (exception.getMessage().startsWith("Payment ID")) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid payment request";
            }
            else if (
                    exception.getMessage().startsWith("Payment is not allowed") ||
                            exception.getMessage().startsWith("Payment time has expired") ||
                            exception.getMessage().startsWith("Booking has expired") ||
                            exception.getMessage().startsWith("Payment is allowed only") ||
                            exception.getMessage().startsWith("Payment already exists")
            ) {

                status = HttpStatus.BAD_REQUEST;
                message = "Payment failed";
            }
            else if (
                    exception.getMessage().startsWith("Booking ID") ||
                            exception.getMessage().startsWith("User ID") ||
                            exception.getMessage().startsWith("Page number") ||
                            exception.getMessage().startsWith("Page size")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid booking request";
            }
            else if (
                    exception.getMessage().startsWith("Booking is available") ||
                            exception.getMessage().startsWith("Number of tickets") ||
                            exception.getMessage().startsWith("Requested number of tickets")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid booking request";
            }
            else if (
                    exception.getMessage().startsWith("Page") ||
                            exception.getMessage().startsWith("Page size") ||
                            exception.getMessage().startsWith("From date") ||
                            exception.getMessage().startsWith("Minimum price") ||
                            exception.getMessage().startsWith("Maximum price")
            ) {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid event search parameters";
            }

        }

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message(message)
                .build();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .data(null)
                .error(exception.getMessage())
                .meta(meta)
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {

        String errorMessage =
                "Invalid value for parameter: " + exception.getName();

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Invalid request parameter")
                .build();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .data(null)
                .error(errorMessage)
                .meta(meta)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}