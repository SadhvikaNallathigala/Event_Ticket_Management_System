package com.example.eventticketmanagement.event.entity;

import com.example.eventticketmanagement.brand.entity.Brand;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String eventName;

    private LocalDate eventDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String description;

    private Integer ageLimit;

    private LocalTime closingTime;

    private BigDecimal vipPrice;

    private BigDecimal goldPrice;

    private BigDecimal normalPrice;

    private Integer vipSeats;

    private Integer goldSeats;

    private Integer normalSeats;

    private BigDecimal comboPriceForTwo;

    private BigDecimal comboPriceForFour;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}