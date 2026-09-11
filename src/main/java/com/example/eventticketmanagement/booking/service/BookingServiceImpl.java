package com.example.eventticketmanagement.booking.service;

import com.example.eventticketmanagement.booking.dto.BookingRequest;
import com.example.eventticketmanagement.booking.dto.BookingResponse;
import com.example.eventticketmanagement.booking.entity.Booking;
import com.example.eventticketmanagement.booking.entity.BookingStatus;
import com.example.eventticketmanagement.booking.repository.BookingRepository;
import com.example.eventticketmanagement.event.entity.Event;
import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.repository.EventRepository;
import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import com.example.eventticketmanagement.seat.entity.SeatType;
import com.example.eventticketmanagement.user.entity.User;
import com.example.eventticketmanagement.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eventticketmanagement.payment.entity.Payment;
import com.example.eventticketmanagement.payment.entity.PaymentStatus;
import com.example.eventticketmanagement.booking.dto.CancelBookingRequest;
import com.example.eventticketmanagement.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Clock indianClock;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            BookingRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No user found with id: "
                                        + request.getUserId()
                        ));

        Event event = eventRepository
                .findByIdForUpdate(request.getEventId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No event found with id: "
                                        + request.getEventId()
                        ));

        if (event.getStatus() != EventStatus.UPCOMING) {
            throw new RuntimeException(
                    "Booking is available only for upcoming events"
            );
        }

        int requestedTickets = request.getNumberOfTickets();

        int availableTickets;

        if (request.getSeatType() == SeatType.VIP) {
            availableTickets = event.getVipSeats();

        } else if (request.getSeatType() == SeatType.GOLD) {
            availableTickets = event.getGoldSeats();

        } else {
            availableTickets = event.getNormalSeats();
        }

        if (requestedTickets > availableTickets) {
            throw new RuntimeException(
                    "Only " + availableTickets +
                            " " + request.getSeatType() +
                            " tickets are available"
            );
        }

        BigDecimal totalAmount =
                calculateAmount(event, request.getSeatType(),
                        requestedTickets);

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        LocalDateTime paymentDeadline =
                currentTime.plusMinutes(2);

        if (request.getSeatType() == SeatType.VIP) {
            event.setVipSeats(
                    event.getVipSeats() - requestedTickets
            );

        } else if (request.getSeatType() == SeatType.GOLD) {
            event.setGoldSeats(
                    event.getGoldSeats() - requestedTickets
            );

        } else {
            event.setNormalSeats(
                    event.getNormalSeats() - requestedTickets
            );
        }

        event.setUpdatedAt(currentTime);

        eventRepository.save(event);

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .seatType(request.getSeatType())
                .numberOfTickets(requestedTickets)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING_PAYMENT)
                .paymentDeadline(paymentDeadline)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        Booking savedBooking =
                bookingRepository.save(booking);

        BookingResponse bookingResponse =
                buildBookingResponse(savedBooking);

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message(
                        "Booking created. Please complete payment within 2 minutes to avoid timeout"
                )
                .build();

        ApiResponse<BookingResponse> response =
                ApiResponse.<BookingResponse>builder()
                        .success(true)
                        .data(bookingResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private BigDecimal calculateAmount(
            Event event,
            SeatType seatType,
            int numberOfTickets) {

        if (seatType == SeatType.VIP) {
            return event.getVipPrice()
                    .multiply(
                            BigDecimal.valueOf(numberOfTickets)
                    );
        }

        if (seatType == SeatType.GOLD) {
            return event.getGoldPrice()
                    .multiply(
                            BigDecimal.valueOf(numberOfTickets)
                    );
        }

        if (numberOfTickets == 2 &&
                event.getComboPriceForTwo() != null) {

            return event.getComboPriceForTwo();
        }

        if (numberOfTickets == 4 &&
                event.getComboPriceForFour() != null) {

            return event.getComboPriceForFour();
        }

        return event.getNormalPrice()
                .multiply(
                        BigDecimal.valueOf(numberOfTickets)
                );
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingBookings() {

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        List<Booking> expiredBookings =
                bookingRepository
                        .findByStatusAndPaymentDeadlineBefore(
                                BookingStatus.PENDING_PAYMENT,
                                currentTime
                        );

        for (Booking booking : expiredBookings) {

            Event event = eventRepository
                    .findByIdForUpdate(
                            booking.getEvent().getId()
                    )
                    .orElse(null);

            if (event == null) {
                continue;
            }

            restoreSeats(
                    event,
                    booking.getSeatType(),
                    booking.getNumberOfTickets()
            );

            event.setUpdatedAt(currentTime);

            eventRepository.save(event);

            booking.setStatus(BookingStatus.EXPIRED);
            booking.setUpdatedAt(currentTime);

            bookingRepository.save(booking);
        }
    }

    private void restoreSeats(
            Event event,
            SeatType seatType,
            int numberOfTickets) {

        if (seatType == SeatType.VIP) {

            event.setVipSeats(
                    event.getVipSeats() + numberOfTickets
            );

        } else if (seatType == SeatType.GOLD) {

            event.setGoldSeats(
                    event.getGoldSeats() + numberOfTickets
            );

        } else {

            event.setNormalSeats(
                    event.getNormalSeats() + numberOfTickets
            );
        }
    }

    private BookingResponse buildBookingResponse(
            Booking booking) {

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .userId(booking.getUser().getId())
                .eventId(booking.getEvent().getId())
                .eventName(booking.getEvent().getEventName())
                .seatType(booking.getSeatType())
                .numberOfTickets(
                        booking.getNumberOfTickets()
                )
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .paymentDeadline(
                        booking.getPaymentDeadline()
                )
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    @Override
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            Long bookingId) {

        if (bookingId == null || bookingId <= 0) {
            throw new RuntimeException(
                    "Booking ID must be positive"
            );
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No booking found with id: " + bookingId
                        ));

        BookingResponse bookingResponse =
                buildBookingResponse(booking);

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Booking details retrieved successfully")
                .build();

        ApiResponse<BookingResponse> response =
                ApiResponse.<BookingResponse>builder()
                        .success(true)
                        .data(bookingResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getBookingHistory(
            Long userId,
            int page,
            int size) {

        if (userId == null || userId <= 0) {
            throw new RuntimeException(
                    "User ID must be positive"
            );
        }

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

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No user found with id: " + userId
                        ));

        Pageable pageable = PageRequest.of(page, size);

        Page<Booking> bookingPage =
                bookingRepository.findByUser(user, pageable);

        if (bookingPage.isEmpty()) {
            throw new RuntimeException(
                    "No bookings found for user: " + userId
            );
        }

        List<BookingResponse> bookings =
                bookingPage.getContent()
                        .stream()
                        .map(this::buildBookingResponse)
                        .toList();

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message("Booking history retrieved successfully")
                .build();

        ApiResponse<List<BookingResponse>> response =
                ApiResponse.<List<BookingResponse>>builder()
                        .success(true)
                        .data(bookings)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> cancelBooking(
            CancelBookingRequest request) {

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

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Only confirmed bookings can be cancelled"
            );
        }

        int cancelTickets = request.getNumberOfTickets();
        int bookedTickets = booking.getNumberOfTickets();

        if (cancelTickets > bookedTickets) {
            throw new RuntimeException(
                    "Cannot cancel more tickets than booked"
            );
        }

        Event event = eventRepository
                .findByIdForUpdate(booking.getEvent().getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No event found with id: "
                                        + booking.getEvent().getId()
                        ));

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        BigDecimal refundAmount =
                calculateCancellationAmount(
                        booking,
                        cancelTickets
                );

        restoreSeats(
                event,
                booking.getSeatType(),
                cancelTickets
        );

        Payment payment = paymentRepository
                .findByBooking(booking)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No payment found for booking: "
                                        + booking.getId()
                        ));

        BigDecimal currentRefundedAmount =
                payment.getRefundedAmount() == null
                        ? BigDecimal.ZERO
                        : payment.getRefundedAmount();

        BigDecimal newRefundedAmount =
                currentRefundedAmount.add(refundAmount);

        payment.setRefundedAmount(newRefundedAmount);
        payment.setUpdatedAt(currentTime);

        int remainingTickets =
                bookedTickets - cancelTickets;

        if (remainingTickets == 0) {

            booking.setNumberOfTickets(0);
            booking.setTotalAmount(BigDecimal.ZERO);
            booking.setStatus(BookingStatus.CANCELLED);

            payment.setStatus(PaymentStatus.REFUNDED);

        } else {

            booking.setNumberOfTickets(remainingTickets);
            booking.setTotalAmount(
                    booking.getTotalAmount().subtract(refundAmount)
            );
            booking.setStatus(BookingStatus.CONFIRMED);

            payment.setStatus(
                    PaymentStatus.PARTIALLY_REFUNDED
            );
        }

        booking.setUpdatedAt(currentTime);

        event.setUpdatedAt(currentTime);

        paymentRepository.save(payment);
        bookingRepository.save(booking);
        eventRepository.save(event);

        BookingResponse bookingResponse =
                buildBookingResponse(booking);

        MetaResponse meta = MetaResponse.builder()
                .timestamp(LocalDateTime.now(indianClock))
                .message(
                        remainingTickets == 0
                                ? "All tickets cancelled and payment refunded successfully"
                                : "Tickets cancelled partially and partial refund processed successfully"
                )
                .build();

        ApiResponse<BookingResponse> response =
                ApiResponse.<BookingResponse>builder()
                        .success(true)
                        .data(bookingResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }
    private BigDecimal calculateCancellationAmount(
            Booking booking,
            int numberOfTickets) {

        Event event = booking.getEvent();

        if (booking.getSeatType() == SeatType.VIP) {

            return event.getVipPrice()
                    .multiply(
                            BigDecimal.valueOf(numberOfTickets)
                    );
        }

        if (booking.getSeatType() == SeatType.GOLD) {

            return event.getGoldPrice()
                    .multiply(
                            BigDecimal.valueOf(numberOfTickets)
                    );
        }

        if (numberOfTickets == 2 &&
                event.getComboPriceForTwo() != null) {

            return event.getComboPriceForTwo();
        }

        if (numberOfTickets == 4 &&
                event.getComboPriceForFour() != null) {

            return event.getComboPriceForFour();
        }

        return event.getNormalPrice()
                .multiply(
                        BigDecimal.valueOf(numberOfTickets)
                );
    }
}