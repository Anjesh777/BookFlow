package com.BookFlow.bookflow.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "service_book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;


    @Column(nullable = false)
    private UUID user_id;

    @Column(nullable = false)
    private LocalDateTime booking_date;

    @Column(nullable = false)
    private LocalDateTime appointment_date;

    @Column(nullable = false)
    private String payment_status;

    @Column
    private String booking_notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus booking_status;


    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        RESCHEDULED,
        NO_SHOW
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = true)
    private LocalDateTime fixedDatedTime;

    @Column(nullable = true)
    private Date ScheduleDay;

    @Column(nullable = false)
    private BigDecimal expectedAmount;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "payment_method", nullable = true)
    private String paymentMethod;

}
