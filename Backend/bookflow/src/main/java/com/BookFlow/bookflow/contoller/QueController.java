package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.BookingDto;
import com.BookFlow.bookflow.dto.QueMapper;
import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.services.QueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.BookFlow.bookflow.model.Booking.BookingStatus;


@Slf4j
@RestController
@RequestMapping("api/v1/admin")
public class QueController {

    private final QueService queService;
    private final QueMapper queMapper;


    public QueController(QueService queService, QueMapper queMapper) {
        this.queService = queService;
        this.queMapper = queMapper;
    }

    // Get bookings by user ID
    @GetMapping("/bookings/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable UUID userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return ResponseEntity.ok(
                queService.getBookingsByUserId(userId).stream()
                        .map(queMapper::toBookingDto)
                        .collect(Collectors.toList())
        );
    }

    // Get bookings between dates
    @GetMapping("/bookings/date-range")
    public ResponseEntity<List<BookingDto>> getBookingsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching bookings between {} and {}", startDate, endDate);
        return ResponseEntity.ok(
                queService.getBookingsBetweenDates(startDate, endDate).stream()
                        .map(queMapper::toBookingDto)
                        .collect(Collectors.toList())
        );
    }

//    @GetMapping("/bookings/payment-status/{paymentStatus}")
//    public ResponseEntity<List<BookingDto>> getBookingsByPaymentStatus(@PathVariable boolean paymentStatus) {
//        log.info("Fetching bookings with payment status: {}", paymentStatus);
//        return ResponseEntity.ok(
//                queService.getBookingsByPaymentStatus(paymentStatus).stream()
//                        .map(queMapper::toBookingDto)
//                        .collect(Collectors.toList())
//        );
//    }

    // Get bookings by service category
    @GetMapping("/bookings/category/{category}")
    public ResponseEntity<List<BookingDto>> getBookingsByServiceCategory(@PathVariable String category) {
        log.info("Fetching bookings in service category: {}", category);
        return ResponseEntity.ok(
                queService.getBookingsByServiceCategory(category).stream()
                        .map(queMapper::toBookingDto)
                        .collect(Collectors.toList())
        );
    }

//    // Get past bookings for a user
//    @GetMapping("/bookings/user/{userId}/past")
//    public ResponseEntity<List<BookingDto>> getPastBookingsForUser(@PathVariable UUID userId) {
//        log.info("Fetching past bookings for user ID: {}", userId);
//        return ResponseEntity.ok(
//                queService.getPastBookingsForUser(userId).stream()
//                        .map(queMapper::toBookingDto)
//                        .collect(Collectors.toList())
//        );
//    }

//    @GetMapping("/bookings/imminent")
//    public ResponseEntity<List<BookingDto>> getImmientBookings() {
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime upcomingTime = now.plusDays(2);
//        BookingStatus status = BookingStatus.PENDING;
//
//        log.info("Fetching imminent bookings from {} to {} with status: {}", now, upcomingTime, status);
//
//        return ResponseEntity.ok(
//                queService.getImminentBookings(now, upcomingTime, status).stream()
//                        .map(queMapper::toBookingDto)
//                        .collect(Collectors.toList())
//        );
//    }

//    @GetMapping("/future")
//    public ResponseEntity<List<BookingDto>> getFutureBookings() {
//        List<Booking> futureBookings = queService.getFutureBookingsAfterTwoDays();
//
//        List<BookingDto> bookingDtos = futureBookings.stream()
//                .map(queMapper::toBookingDto)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(bookingDtos);
//    }

//    @PutMapping("/bookings/{bookingId}/status")
//    public ResponseEntity<String> updateBookingStatus(
//            @PathVariable Long bookingId,
//            @RequestParam BookingStatus newStatus) {
//        log.info("Updating booking {} status to {}", bookingId, newStatus);
//        queService.updateBookingStatus(bookingId, newStatus);
//        return ResponseEntity.ok("Booking status updated successfully");
//    }

//    @PutMapping("/bookings/{bookingId}/details")
//    public ResponseEntity<String> updateBookingDetails(
//            @PathVariable Long bookingId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDate,
//            @RequestParam BookingStatus bookingStatus,
//            @RequestParam(required = false) String bookingNotes) {
//        log.info("Updating booking {} details", bookingId);
//        queService.updateBookingDetails(bookingId, appointmentDate, bookingStatus, bookingNotes);
//        return ResponseEntity.ok("Booking details updated successfully");
//    }







}
