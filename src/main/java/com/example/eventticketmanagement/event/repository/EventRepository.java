package com.example.eventticketmanagement.event.repository;

import com.example.eventticketmanagement.event.entity.Event;
import com.example.eventticketmanagement.event.entity.EventStatus;
import com.example.eventticketmanagement.event.entity.EventType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT e FROM Event e
            WHERE (:search IS NULL OR
                   LOWER(e.eventName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:location IS NULL OR
                 LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')))
            AND (:fromDate IS NULL OR e.eventDate >= :fromDate)
            AND (:toDate IS NULL OR e.eventDate <= :toDate)
            AND (:eventType IS NULL OR e.eventType = :eventType)
            AND (:status IS NULL OR e.status = :status)
            AND (:minPrice IS NULL OR e.normalPrice >= :minPrice)
            AND (:maxPrice IS NULL OR e.normalPrice <= :maxPrice)
            """)
    Page<Event> searchEvents(
            @Param("search") String search,
            @Param("location") String location,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("eventType") EventType eventType,
            @Param("status") EventStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}