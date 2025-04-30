package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.QueRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private UserContextUtil userContextUtil;



    public List<Booking> getBookingsByUserId(UUID userId) {
        return queRepo.findByUserId(userId);
    }

    public List<Booking> getBookingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Company currentCompany = userContextUtil.getCurrentUserCompany();
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
        Company currentCompany = userContextUtil.getCurrentUserCompany();
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
        Company currentCompany = userContextUtil.getCurrentUserCompany();
        UUID companyId = currentCompany.getCompany_id();
        return queRepo.findActiveBookingsAfterTwoDays(companyId, twoDaysLater);
    }

    public List<Booking> getCancelledBookingsForCurrentCompany() {
        UUID companyId = userContextUtil.getCurrentUserCompany().getCompany_id();
        return queRepo.findCancleBookingsByCompany(companyId);
    }

    public List<Booking> getConfirmedBookingsForCurrentCompany() {
        UUID companyId = userContextUtil.getCurrentUserCompany().getCompany_id();
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


    public List<Booking> getRecentCancelledBookingsForCurrentCompany() {
        UUID companyId = userContextUtil.getCurrentUserCompany().getCompany_id();
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        return queRepo.findRecentCancelledBookingsByCompany(
                companyId, threeMonthsAgo);
    }

    public List<Booking> getCancelledBookingsForCurrentCompanyByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        UUID companyId = userContextUtil.getCurrentUserCompany().getCompany_id();
        return queRepo.findCancelledBookingsByCompanyAndDateRange(
                companyId, startDate, endDate);
    }




}
