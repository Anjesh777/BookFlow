package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.BookingDto;
import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Services;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.BookingRepo;
import com.BookFlow.bookflow.repository.ServiceRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ServiceRepo serviceRepo;
    @Autowired
    private BookingRepo bookingRepo;

    private Company getCurrentUserCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        return currentUser.get().getCompany_id();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public List<BookingDto> findUpcomingBookingsByUserId() {
        UUID userId = getCurrentUser().getUser_id();
        List<Booking> upcomingBookings = bookingRepo.findPendingBookingsByUserId(userId);
        return upcomingBookings.stream()
                .map(this::mapToDto)
                .toList();
    }


    public List<BookingDto> findPastBookingsByUserId() {
        UUID userId = getCurrentUser().getUser_id();
        List<Booking> pastBookings = bookingRepo.findCompletedBookingsByUserId(userId);
        return pastBookings.stream()
                .map(this::mapToDto)
                .toList();
    }


    public BookingDto addBooking(BookingDto bookingDto) {
        try {
            Booking booking = mapToEntity(bookingDto);
            Booking savedBooking = bookingRepo.save(booking);

            return mapToDto(savedBooking);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add booking: " + e.getMessage(), e);
        }
    }

    private Booking mapToEntity(BookingDto dto) {
        Booking booking = new Booking();

        booking.setBooking_date(LocalDateTime.parse(dto.getBookingDate(),
                java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME));
        booking.setAppointment_date(LocalDateTime.parse(dto.getAppointmentDate(),
                java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME));

        Services service = serviceRepo.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + dto.getServiceId()));
        booking.setService(service);
        booking.setPayment_status(dto.getPaymentStatus());
        booking.setBooking_notes(dto.getBookingNotes());
        booking.setBooking_status(Booking.BookingStatus.valueOf(Booking.BookingStatus.PENDING.name()));

        // Set duration from the DTO
        booking.setDuration(dto.getDuration());

        // Calculate end time based on duration (which now can be a decimal value)
        LocalDateTime appointmentDate = booking.getAppointment_date();
        double durationHours = Double.parseDouble(dto.getDuration());

        // Calculate minutes and hours
        int hours = (int) durationHours;
        int minutes = (int) Math.round((durationHours - hours) * 60);

        // Add hours and minutes to appointment date
        LocalDateTime endTime = appointmentDate.plusHours(hours).plusMinutes(minutes);
        booking.setEndTime(endTime);

        // Set fixedDatedTime if provided
        if (dto.getFixedDatedTime() != null && !dto.getFixedDatedTime().isEmpty()) {
            booking.setFixedDatedTime(LocalDateTime.parse(dto.getFixedDatedTime(),
                    java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }

        // Calculate the expected amount based on the service price and duration
        BigDecimal expectedAmount = calculateExpectedAmount(service.getPrice(),
                dto.getDuration(),
                booking.getFixedDatedTime());
        booking.setExpectedAmount(expectedAmount);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        booking.setUser_id(currentUser.getUser_id());
        booking.setCompany(currentUser.getCompany_id());

        return booking;
    }

    // Fixed method signature to match the calling code
    private BigDecimal calculateExpectedAmount(BigDecimal basePrice, String durationStr, LocalDateTime fixedDatedTime) {
        double durationHours = Double.parseDouble(durationStr);

        // Apply hourly pricing based on duration
        BigDecimal hourlyRate = basePrice;
        BigDecimal durationBigDecimal = BigDecimal.valueOf(durationHours);
        BigDecimal baseCost = hourlyRate.multiply(durationBigDecimal);

        if (fixedDatedTime != null) {
            int hourOfDay = fixedDatedTime.getHour();
            BigDecimal multiplier = new BigDecimal("1.0");
            return baseCost.multiply(multiplier).setScale(2, java.math.RoundingMode.HALF_UP);
        }

        return baseCost.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public boolean deletePendingBookingIfLessThan12Hours(Long bookingId) {
        LocalDateTime cutoffTime = LocalDateTime.now().plusHours(12);
        int deletedCount = bookingRepo.deletePendingBookingById(bookingId, cutoffTime);
        return deletedCount > 0;
    }


    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        Services service = booking.getService();

        dto.setServiceId(service.getService_id());
        dto.setAppointmentDate(booking.getAppointment_date().toString());
        dto.setBookingDate(booking.getBooking_date().toString());
        dto.setPaymentStatus(booking.getPayment_status());
        dto.setBookingNotes(booking.getBooking_notes());
        dto.setBookingStatus(String.valueOf(booking.getBooking_status()));
        dto.setDuration(booking.getDuration());
        dto.setBookingId(String.valueOf(booking.getBooking_id()));
        dto.setSheduleVerified(booking.isVerified());


        if (booking.getEndTime() != null) {
            dto.setEndTime(booking.getEndTime().toString());
        }

        dto.setServiceName(service.getServiceName());
        dto.setServiceDescription(service.getServiceDescription());
        dto.setServicePrice(service.getPrice());
        dto.setServiceCategory(service.getCategory());
        dto.setBookingPrice(String.valueOf(booking.getExpectedAmount()));
        dto.setUserName(String.valueOf(userRepo.findById(booking.getUser_id())));

        dto.setExpectedAmount(booking.getExpectedAmount());
        if (booking.getFixedDatedTime() != null) {
            dto.setFixedDatedTime(booking.getFixedDatedTime().toString());
        }

        return dto;
    }







}
