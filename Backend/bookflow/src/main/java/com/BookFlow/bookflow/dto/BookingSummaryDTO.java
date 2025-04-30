package com.BookFlow.bookflow.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryDTO {
    private BigDecimal totalPayment;
    private LocalDateTime upcomingServiceDate;
    private BigDecimal nextAppointmentPrice;
    private Long totalServicesBooked;
    private Long pendingServices;
}

