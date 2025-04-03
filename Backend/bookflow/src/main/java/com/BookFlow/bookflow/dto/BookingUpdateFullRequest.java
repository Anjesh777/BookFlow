package com.BookFlow.bookflow.dto;

import com.BookFlow.bookflow.model.Booking;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingUpdateFullRequest {
    private String bookingId;
    private String serviceId;
    private String appointmentDate;
    private String bookingNotes;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentMethod;
}
