package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.QueRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.BookFlow.bookflow.model.Booking.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QueService {

    @Autowired
    private QueRepo queRepo;

    @Autowired
    private UserRepo userRepo;

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


    public List<Booking> getBookingsByUserId(UUID userId) {
        return queRepo.findByUserId(userId);
    }

    public List<Booking> getBookingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Company currentCompany = getCurrentUserCompany();
        return queRepo.findBookingsBetweenDates(startDate, endDate, currentCompany.getCompany_id());
    }


//    public List<Booking> getBookingsByPaymentStatus(boolean paymentStatus) {
//        Company currentCompany = getCurrentUserCompany();
//        return queRepo.findByPaymentStatus(paymentStatus, currentCompany.getCompany_id());
//    }

    public List<Booking> getBookingsByServiceCategory(String category) {
        return queRepo.findBookingsByServiceCategory(category);
    }

    public List<Booking> getPastBookingsForUser(UUID userId) {
        return queRepo.findPastBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getImminentBookings(LocalDateTime now, LocalDateTime upcomingTime) {
        Company currentCompany = getCurrentUserCompany();
        return queRepo.findImminentBookings(now, upcomingTime, currentCompany.getCompany_id());
    }

//    public void updateBookingStatus(Long bookingId, BookingStatus newStatus) {
//        queRepo.updateBookingStatus(bookingId, newStatus);
//    }

    public void updateBookingDetails(Long bookingId, LocalDateTime appointmentDate,
                                     BookingStatus bookingStatus, String bookingNotes) {
        queRepo.updateBookingDetails(bookingId, appointmentDate, bookingStatus, bookingNotes);
    }

//    public List<Booking> getFutureBookingsAfterTwoDays() {
//        LocalDateTime cutoffDate = LocalDateTime.now().plusDays(2);
//        Company currentCompany = getCurrentUserCompany();
//
//        return queRepo.findFutureBookingsAfterTwoDays(
//                cutoffDate,
//                "PENDING",
//                currentCompany.getCompany_id()
//        );
//    }

    public List<Booking> getActiveBookingsForCurrentCompany() {
        LocalDateTime twoDaysLater = LocalDateTime.now().plusDays(2);
        Company currentCompany = getCurrentUserCompany();
        UUID companyId = currentCompany.getCompany_id();
        return queRepo.findActiveBookingsAfterTwoDays(companyId, twoDaysLater);
    }

    public List<Booking> getCancelledBookingsForCurrentCompany() {
        UUID companyId = getCurrentUserCompany().getCompany_id();
        return queRepo.findCancleBookingsByCompany(companyId);
    }

    public List<Booking> getConfirmedBookingsForCurrentCompany() {
        UUID companyId = getCurrentUserCompany().getCompany_id();
        return queRepo.findCompletedBookingsByCompany(companyId);
    }

    public void updateBookingFull(Long bookingId, LocalDateTime appointmentDate, String bookingNotes,
                                  BookingStatus bookingStatus, String paymentStatus, String paymentMethod) {

        queRepo.updateBookingFull(bookingId,
                appointmentDate,
                bookingNotes,
                bookingStatus,
                paymentStatus,
                paymentMethod);
        }







}
