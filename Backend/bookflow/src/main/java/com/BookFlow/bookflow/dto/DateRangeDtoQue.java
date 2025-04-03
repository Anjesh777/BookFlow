package com.BookFlow.bookflow.dto;

import com.BookFlow.bookflow.enums.BookingStatus;
import com.BookFlow.bookflow.model.Booking;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateRangeDtoQue {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}


