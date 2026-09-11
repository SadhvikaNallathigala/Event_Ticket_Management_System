package com.example.eventticketmanagement.payment.service;

import com.example.eventticketmanagement.booking.entity.Booking;
import com.example.eventticketmanagement.booking.entity.BookingStatus;
import com.example.eventticketmanagement.booking.repository.BookingRepository;
import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.payment.dto.PaymentRequest;
import com.example.eventticketmanagement.payment.dto.PaymentResponse;
import com.example.eventticketmanagement.payment.entity.Payment;
import com.example.eventticketmanagement.payment.entity.PaymentStatus;
import com.example.eventticketmanagement.payment.repository.PaymentRepository;
import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import com.example.eventticketmanagement.user.entity.User;
import com.example.eventticketmanagement.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Clock indianClock;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(
            PaymentRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No user found with id: "
                                        + request.getUserId()
                        ));

        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No booking found with id: "
                                        + request.getBookingId()
                        ));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Booking does not belong to the given user"
            );
        }

        if (booking.getEvent().getStatus() != EventStatus.UPCOMING) {
            throw new RuntimeException(
                    "Payment is not allowed for inactive event"
            );
        }

        if (booking.getStatus() == BookingStatus.EXPIRED) {
            throw new RuntimeException(
                    "Booking has expired. Please book your tickets again"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException(
                    "Payment is not allowed for cancelled booking"
            );
        }

        if (booking.getStatus() == BookingStatus.REFUNDED) {
            throw new RuntimeException(
                    "Payment is not allowed for refunded booking"
            );
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException(
                    "Payment is allowed only for pending payment bookings"
            );
        }

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        if (!currentTime.isBefore(
                booking.getPaymentDeadline())) {

            throw new RuntimeException(
                    "Payment time has expired. Please book your tickets again"
            );
        }

        if (paymentRepository.findByBooking(booking).isPresent()) {
            throw new RuntimeException(
                    "Payment already exists for this booking"
            );
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .userId(user.getId())
                .amount(booking.getTotalAmount())
                .refundedAmount(BigDecimal.ZERO)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.SUCCESS)
                .transactionId(UUID.randomUUID().toString())
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        Payment savedPayment =
                paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(currentTime);

        bookingRepository.save(booking);

        PaymentResponse paymentResponse =
                buildPaymentResponse(savedPayment);

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Payment completed successfully")
                .build();

        ApiResponse<PaymentResponse> response =
                ApiResponse.<PaymentResponse>builder()
                        .success(true)
                        .data(paymentResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private PaymentResponse buildPaymentResponse(
            Payment payment) {

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBooking().getId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .refundedAmount(payment.getRefundedAmount())
                .build();
    }

    @Override
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            Long paymentId) {

        if (paymentId == null || paymentId <= 0) {
            throw new RuntimeException(
                    "Payment ID must be positive"
            );
        }

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No payment found with id: "
                                        + paymentId
                        ));

        PaymentResponse paymentResponse =
                buildPaymentResponse(payment);

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Payment details retrieved successfully"
                        )
                        .build();

        ApiResponse<PaymentResponse> response =
                ApiResponse.<PaymentResponse>builder()
                        .success(true)
                        .data(paymentResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }
}