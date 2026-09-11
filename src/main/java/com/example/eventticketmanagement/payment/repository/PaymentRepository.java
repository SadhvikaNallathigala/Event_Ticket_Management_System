package com.example.eventticketmanagement.payment.repository;

import com.example.eventticketmanagement.booking.entity.Booking;
import com.example.eventticketmanagement.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBooking(Booking booking);
}