package com.example.eventticketmanagement.event.service;

import com.example.eventticketmanagement.event.entity.EventType;
import com.example.eventticketmanagement.brand.entity.Brand;
import com.example.eventticketmanagement.brand.repository.BrandRepository;
import com.example.eventticketmanagement.event.dto.EventRequest;
import com.example.eventticketmanagement.event.dto.EventResponse;
import com.example.eventticketmanagement.event.entity.Event;
import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.repository.EventRepository;
import com.example.eventticketmanagement.response.ApiResponse;
import com.example.eventticketmanagement.response.MetaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eventticketmanagement.event.dto.EventSearchRequest;
import com.example.eventticketmanagement.brand.entity.BrandStatus;
import com.example.eventticketmanagement.booking.entity.Booking;
import com.example.eventticketmanagement.booking.entity.BookingStatus;
import com.example.eventticketmanagement.booking.repository.BookingRepository;
import com.example.eventticketmanagement.event.dto.EventUpdateRequest;
import com.example.eventticketmanagement.payment.entity.Payment;
import com.example.eventticketmanagement.payment.entity.PaymentStatus;
import com.example.eventticketmanagement.payment.repository.PaymentRepository;
import com.example.eventticketmanagement.seat.entity.SeatType;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private Clock indianClock;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            EventRequest request) {

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "No brand found with id: " + request.getBrandId()
                        ));

        if (brand.getStatus() != BrandStatus.ACTIVE) {
            throw new RuntimeException(
                    "Event cannot be created because the brand is inactive"
            );
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException(
                    "End time must be after start time"
            );
        }

        if (!request.getClosingTime().isBefore(request.getStartTime())) {
            throw new RuntimeException(
                    "Closing time must be before event start time"
            );
        }

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        Event event = Event.builder()
                .brand(brand)
                .eventName(request.getEventName())
                .eventDate(request.getEventDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .eventType(request.getEventType())
                .description(request.getDescription())
                .ageLimit(request.getAgeLimit())
                .closingTime(request.getClosingTime())
                .vipPrice(request.getVipPrice())
                .goldPrice(request.getGoldPrice())
                .normalPrice(request.getNormalPrice())
                .vipSeats(request.getVipSeats())
                .goldSeats(request.getGoldSeats())
                .normalSeats(request.getNormalSeats())
                .comboPriceForTwo(request.getComboPriceForTwo())
                .comboPriceForFour(request.getComboPriceForFour())
                .status(EventStatus.UPCOMING)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        Event savedEvent =
                eventRepository.save(event);

        EventResponse eventResponse =
                EventResponse.builder()
                        .id(savedEvent.getId())
                        .brandId(savedEvent.getBrand().getId())
                        .eventName(savedEvent.getEventName())
                        .eventDate(savedEvent.getEventDate())
                        .startTime(savedEvent.getStartTime())
                        .endTime(savedEvent.getEndTime())
                        .location(savedEvent.getLocation())
                        .eventType(savedEvent.getEventType())
                        .description(savedEvent.getDescription())
                        .ageLimit(savedEvent.getAgeLimit())
                        .closingTime(savedEvent.getClosingTime())
                        .vipPrice(savedEvent.getVipPrice())
                        .goldPrice(savedEvent.getGoldPrice())
                        .normalPrice(savedEvent.getNormalPrice())
                        .vipSeats(savedEvent.getVipSeats())
                        .goldSeats(savedEvent.getGoldSeats())
                        .normalSeats(savedEvent.getNormalSeats())
                        .comboPriceForTwo(savedEvent.getComboPriceForTwo())
                        .comboPriceForFour(savedEvent.getComboPriceForFour())
                        .status(savedEvent.getStatus())
                        .createdAt(savedEvent.getCreatedAt())
                        .updatedAt(savedEvent.getUpdatedAt())
                        .build();

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Event created successfully"
                        )
                        .build();

        ApiResponse<EventResponse> response =
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .data(eventResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getEvents(
            EventSearchRequest request) {

        int page = request.getPage();
        int size = request.getSize();

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

        LocalDate fromDate =
                request.getFromDate();

        LocalDate toDate =
                request.getToDate();

        if (fromDate != null &&
                toDate != null &&
                fromDate.isAfter(toDate)) {

            throw new RuntimeException(
                    "From date cannot be after to date"
            );
        }

        BigDecimal minPrice =
                request.getMinPrice();

        BigDecimal maxPrice =
                request.getMaxPrice();

        if (minPrice != null &&
                minPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null &&
                maxPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null &&
                maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0) {

            throw new RuntimeException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        String search =
                request.getSearch();

        if (search != null &&
                search.isBlank()) {

            search = null;
        }

        String location =
                request.getLocation();

        if (location != null &&
                location.isBlank()) {

            location = null;
        }

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Event> eventPage =
                eventRepository.searchEvents(
                        search,
                        location,
                        fromDate,
                        toDate,
                        request.getEventType(),
                        request.getStatus(),
                        minPrice,
                        maxPrice,
                        pageable
                );

        if (eventPage.isEmpty()) {
            throw new RuntimeException(
                    "No events found matching the given criteria"
            );
        }

        List<EventResponse> events =
                eventPage.getContent()
                        .stream()
                        .map(event ->
                                EventResponse.builder()
                                        .id(event.getId())
                                        .brandId(
                                                event.getBrand().getId()
                                        )
                                        .eventName(
                                                event.getEventName()
                                        )
                                        .eventDate(
                                                event.getEventDate()
                                        )
                                        .startTime(
                                                event.getStartTime()
                                        )
                                        .endTime(
                                                event.getEndTime()
                                        )
                                        .location(
                                                event.getLocation()
                                        )
                                        .eventType(
                                                event.getEventType()
                                        )
                                        .description(
                                                event.getDescription()
                                        )
                                        .ageLimit(
                                                event.getAgeLimit()
                                        )
                                        .closingTime(
                                                event.getClosingTime()
                                        )
                                        .vipPrice(
                                                event.getVipPrice()
                                        )
                                        .goldPrice(
                                                event.getGoldPrice()
                                        )
                                        .normalPrice(
                                                event.getNormalPrice()
                                        )
                                        .vipSeats(
                                                event.getVipSeats()
                                        )
                                        .goldSeats(
                                                event.getGoldSeats()
                                        )
                                        .normalSeats(
                                                event.getNormalSeats()
                                        )
                                        .comboPriceForTwo(
                                                event.getComboPriceForTwo()
                                        )
                                        .comboPriceForFour(
                                                event.getComboPriceForFour()
                                        )
                                        .status(
                                                event.getStatus()
                                        )
                                        .createdAt(
                                                event.getCreatedAt()
                                        )
                                        .updatedAt(
                                                event.getUpdatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Events retrieved successfully"
                        )
                        .build();

        ApiResponse<List<EventResponse>> response =
                ApiResponse.<List<EventResponse>>builder()
                        .success(true)
                        .data(events)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            Long eventId,
            EventUpdateRequest request) {

        if (eventId == null ||
                eventId <= 0) {

            throw new RuntimeException(
                    "Event ID must be positive"
            );
        }

        Event event = eventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No event found with id: "
                                        + eventId
                        ));

        if (event.getStatus() ==
                EventStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled event cannot be updated"
            );
        }

        if (!request.getEndTime().isAfter(
                request.getStartTime())) {

            throw new RuntimeException(
                    "End time must be after start time"
            );
        }

        if (!request.getClosingTime().isBefore(
                request.getStartTime())) {

            throw new RuntimeException(
                    "Closing time must be before event start time"
            );
        }

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        event.setEventName(
                request.getEventName().trim()
        );

        event.setEventDate(
                request.getEventDate()
        );

        event.setStartTime(
                request.getStartTime()
        );

        event.setEndTime(
                request.getEndTime()
        );

        event.setLocation(
                request.getLocation().trim()
        );

        event.setEventType(
                request.getEventType()
        );

        event.setDescription(
                request.getDescription()
        );

        event.setAgeLimit(
                request.getAgeLimit()
        );

        event.setClosingTime(
                request.getClosingTime()
        );

        event.setVipPrice(
                request.getVipPrice()
        );

        event.setGoldPrice(
                request.getGoldPrice()
        );

        event.setNormalPrice(
                request.getNormalPrice()
        );

        event.setComboPriceForTwo(
                request.getComboPriceForTwo()
        );

        event.setComboPriceForFour(
                request.getComboPriceForFour()
        );

        event.setUpdatedAt(currentTime);

        Event updatedEvent =
                eventRepository.save(event);

        EventResponse eventResponse =
                buildEventResponse(updatedEvent);

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Event updated successfully"
                        )
                        .build();

        ApiResponse<EventResponse> response =
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .data(eventResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<EventResponse>> cancelEvent(
            Long eventId) {

        if (eventId == null ||
                eventId <= 0) {

            throw new RuntimeException(
                    "Event ID must be positive"
            );
        }

        Event event = eventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No event found with id: "
                                        + eventId
                        ));

        if (event.getStatus() ==
                EventStatus.CANCELLED) {

            throw new RuntimeException(
                    "Event is already cancelled"
            );
        }

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        List<Booking> bookings =
                bookingRepository.findByEvent(event);

        for (Booking booking : bookings) {

            if (booking.getStatus() ==
                    BookingStatus.CONFIRMED) {

                restoreEventSeats(
                        event,
                        booking
                );

                Payment payment =
                        paymentRepository
                                .findByBooking(booking)
                                .orElse(null);

                if (payment != null &&
                        payment.getStatus() ==
                                PaymentStatus.SUCCESS) {

                    BigDecimal alreadyRefunded =
                            payment.getRefundedAmount() == null
                                    ? BigDecimal.ZERO
                                    : payment.getRefundedAmount();

                    payment.setRefundedAmount(
                            alreadyRefunded.add(
                                    booking.getTotalAmount()
                            )
                    );

                    payment.setStatus(
                            PaymentStatus.REFUNDED
                    );

                    payment.setUpdatedAt(
                            currentTime
                    );

                    paymentRepository.save(
                            payment
                    );
                }

                booking.setStatus(
                        BookingStatus.REFUNDED
                );

                booking.setUpdatedAt(
                        currentTime
                );

                bookingRepository.save(
                        booking
                );

            } else if (
                    booking.getStatus() ==
                            BookingStatus.PENDING_PAYMENT) {

                restoreEventSeats(
                        event,
                        booking
                );

                booking.setStatus(
                        BookingStatus.CANCELLED
                );

                booking.setUpdatedAt(
                        currentTime
                );

                bookingRepository.save(
                        booking
                );
            }
        }

        event.setStatus(
                EventStatus.CANCELLED
        );

        event.setUpdatedAt(
                currentTime
        );

        Event cancelledEvent =
                eventRepository.save(event);

        EventResponse eventResponse =
                buildEventResponse(cancelledEvent);

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(
                                LocalDateTime.now(indianClock)
                        )
                        .message(
                                "Event cancelled successfully. Confirmed bookings refunded and seats released"
                        )
                        .build();

        ApiResponse<EventResponse> response =
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .data(eventResponse)
                        .error(null)
                        .meta(meta)
                        .build();

        return ResponseEntity.ok(response);
    }

    private void restoreEventSeats(
            Event event,
            Booking booking) {

        if (booking.getSeatType() ==
                SeatType.VIP) {

            event.setVipSeats(
                    event.getVipSeats()
                            + booking.getNumberOfTickets()
            );

        } else if (
                booking.getSeatType() ==
                        SeatType.GOLD) {

            event.setGoldSeats(
                    event.getGoldSeats()
                            + booking.getNumberOfTickets()
            );

        } else {

            event.setNormalSeats(
                    event.getNormalSeats()
                            + booking.getNumberOfTickets()
            );
        }
    }

    private EventResponse buildEventResponse(
            Event event) {

        return EventResponse.builder()
                .id(event.getId())
                .brandId(event.getBrand().getId())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .eventType(event.getEventType())
                .description(event.getDescription())
                .ageLimit(event.getAgeLimit())
                .closingTime(event.getClosingTime())
                .vipPrice(event.getVipPrice())
                .goldPrice(event.getGoldPrice())
                .normalPrice(event.getNormalPrice())
                .vipSeats(event.getVipSeats())
                .goldSeats(event.getGoldSeats())
                .normalSeats(event.getNormalSeats())
                .comboPriceForTwo(event.getComboPriceForTwo())
                .comboPriceForFour(event.getComboPriceForFour())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}