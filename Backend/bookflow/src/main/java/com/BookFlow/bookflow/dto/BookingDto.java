package com.BookFlow.bookflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private String serviceId;
    private String appointmentDate;
    private String bookingDate;
    private String paymentStatus;
    private String bookingNotes;
    private String bookingStatus;
    private String fixedDatedTime;

    private String serviceName;
    private String serviceDescription;
    private BigDecimal servicePrice;
    private String serviceCategory;

    private String duration;
    private String endTime;
    private String bookingPrice;

    private BigDecimal expectedAmount;
    private String bookingId;
    private String userName;
    private boolean sheduleVerified;
    private String paymentMethod;



}
