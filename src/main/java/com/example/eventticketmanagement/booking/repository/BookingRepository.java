package com.example.eventticketmanagement.booking.repository;

import com.example.eventticketmanagement.booking.entity.Booking;
import com.example.eventticketmanagement.booking.entity.BookingStatus;
import com.example.eventticketmanagement.event.entity.Event;
import com.example.eventticketmanagement.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByEvent(Event event);

    List<Booking> findByStatusAndPaymentDeadlineBefore(
            BookingStatus status,
            LocalDateTime time);

    Page<Booking> findByUser(User user, Pageable pageable);

    List<Booking> findByUserAndEventAndStatus(
            User user,
            Event event,
            BookingStatus status);
}