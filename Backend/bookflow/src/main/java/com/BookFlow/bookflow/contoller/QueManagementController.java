package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.dto.*;
import com.BookFlow.bookflow.model.Booking.BookingStatus;
import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.services.BookingCompletionService;
import com.BookFlow.bookflow.services.BookingService;
import com.BookFlow.bookflow.services.QueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/admin")
public class QueManagementController {

    @Autowired
    private QueService queService;
    @Autowired
    private QueMapper queMapper;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingCompletionService bookingCompletionService;

    @GetMapping("que/imminent")
    public ResponseEntity<List<BookingDto>> getImminentBookings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingTime = now.plusDays(2);
        List<Booking> bookings = queService.getImminentBookings(now, upcomingTime);

        List<BookingDto> dtos = bookings.stream()
                .map(queMapper::toBookingDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("que/active")
    public ResponseEntity<List<BookingDto>> getActiveBookings() {
        List<Booking> activeBookings = queService.getActiveBookingsForCurrentCompany();

        List<BookingDto> dtos = activeBookings.stream()
                .map(queMapper::toBookingDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

//// this need some modification
//    @PutMapping("que/update")
//    public ResponseEntity updateBooking(@RequestBody BookingUpdateFullRequest updateRequest) {
//        try {
//            Long bookingId = Long.parseLong(updateRequest.getBookingId());
//            BookingStatus newStatus = BookingStatus.valueOf(updateRequest.getBookingStatus());
//
//            if (newStatus == BookingStatus.COMPLETED) {
//                bookingCompletionService.completeBooking(bookingId, updateRequest.getPaymentStatus());
//            } else {
//
//                ZonedDateTime utcDateTime = ZonedDateTime.parse(
//                        updateRequest.getAppointmentDate(),
//                        DateTimeFormatter.ISO_DATE_TIME
//                );
//                ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault());
//                LocalDateTime appointmentDate = localDateTime.toLocalDateTime();
//
//                Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(updateRequest.getBookingStatus());
//
//                queService.updateBookingFull(
//                        bookingId,
//                        appointmentDate,
//                        updateRequest.getBookingNotes(),
//                        bookingStatus,
//                        updateRequest.getPaymentStatus(),
//                        updateRequest.getPaymentMethod()
//                );
//
//            }
//
//
//            return ResponseEntity.ok().build();
//
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PutMapping("que/update")
    public ResponseEntity updateBooking(@RequestBody BookingUpdateFullRequest updateRequest) {
        try {
            Long bookingId = Long.parseLong(updateRequest.getBookingId());
            BookingStatus newStatus = BookingStatus.valueOf(updateRequest.getBookingStatus());

            // Parse date for both cases
            ZonedDateTime utcDateTime = ZonedDateTime.parse(
                    updateRequest.getAppointmentDate(),
                    DateTimeFormatter.ISO_DATE_TIME
            );
            LocalDateTime appointmentDate = utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

            if (newStatus == BookingStatus.COMPLETED) {
                // Pass both payment status and payment method to the completion service
                bookingCompletionService.completeBooking(
                        bookingId,
                        updateRequest.getPaymentStatus(),
                        updateRequest.getPaymentMethod()  // Add payment method parameter
                );
            } else {
                // Handle other status updates
                queService.updateBookingFull(
                        bookingId,
                        appointmentDate,
                        updateRequest.getBookingNotes(),
                        newStatus,
                        updateRequest.getPaymentStatus(),
                        updateRequest.getPaymentMethod()
                );
            }
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            e.printStackTrace(); // Add logging to see what entity wasn't found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Add logging to see the actual error
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bookings/{bookingId}/payments")
    public ResponseEntity recordPayment(
            @PathVariable Long bookingId,
            @RequestParam String paymentMethod) {
        try {
            bookingCompletionService.recordPayment(bookingId, paymentMethod);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("que/cancelled")
    public ResponseEntity<List<Booking>> getCancelledBookings() {

        List<Booking> cancelledBookings = queService.getCancelledBookingsForCurrentCompany();
        return ResponseEntity.ok(cancelledBookings);
    }

    @GetMapping("que/date-filter")
    public ResponseEntity<List<BookingDto>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Booking> bookings = queService.getBookingsBetweenDates(startDate, endDate);
        List<BookingDto> dtos = bookings.stream()
                .map(queMapper::toBookingDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }






}
