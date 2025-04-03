package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.Booking.BookingStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface QueRepo extends JpaRepository<Booking, Long> {


    @Query("SELECT b FROM Booking b WHERE b.user_id = :userId")
    List<Booking> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM Booking b WHERE b.appointment_date BETWEEN :startDate AND :endDate AND b.company.company_id = :companyId")
    List<Booking> findBookingsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("companyId") UUID companyId
    );

    @Query("SELECT b FROM Booking b WHERE b.service.category = :category")
    List<Booking> findBookingsByServiceCategory(@Param("category") String category);


    @Query("SELECT b FROM Booking b WHERE b.user_id = :userId AND b.appointment_date < :currentDateTime ORDER BY b.appointment_date DESC")
    List<Booking> findPastBookingsByUser(
            @Param("userId") UUID userId,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    @Query("SELECT b FROM Booking b WHERE b.appointment_date BETWEEN :now AND :upcomingTime AND (b.booking_status = 'PENDING' OR b.booking_status = 'CONFIRMED') AND b.company.company_id = :companyId")
    List<Booking> findImminentBookings(
            @Param("now") LocalDateTime now,
            @Param("upcomingTime") LocalDateTime upcomingTime,
            @Param("companyId") UUID companyId
    );



    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET " +
            "b.appointment_date = :appointmentDate, " +
            "b.booking_status = :bookingStatus, " +
            "b.booking_notes = :bookingNotes " +
            "WHERE b.booking_id = :bookingId")
    void updateBookingDetails(
            @Param("bookingId") Long bookingId,
            @Param("appointmentDate") LocalDateTime appointmentDate,
            @Param("bookingStatus") BookingStatus bookingStatus,
            @Param("bookingNotes") String bookingNotes
    );

    @Query("SELECT b FROM Booking b WHERE b.appointment_date > :cutoffDate " +
            "AND b.booking_status = :status AND b.company.company_id = :companyId " +
            "ORDER BY b.appointment_date ASC")
    List<Booking> findFutureBookingsAfterTwoDays(
            @Param("cutoffDate") LocalDateTime cutoffDate,
            @Param("status") String status,
            @Param("companyId") UUID companyId
    );


    @Query("SELECT b FROM Booking b WHERE b.company.company_id = :companyId " +
            "AND (b.booking_status = 'PENDING' OR b.booking_status = 'CONFIRMED') " +
            "AND b.appointment_date > :twoDaysLater")
    List<Booking> findActiveBookingsAfterTwoDays(
            @Param("companyId") UUID companyId,
            @Param("twoDaysLater") LocalDateTime twoDaysLater
    );

    @Query("SELECT b FROM Booking b WHERE b.company.company_id = :companyId AND (b.booking_status = 'CANCELLED')")
    List<Booking> findCancleBookingsByCompany(
            @Param("companyId") UUID companyId
    );

    @Query("SELECT b FROM Booking b WHERE b.company.company_id = :companyId AND b.booking_status = 'CONFIRMED' ")
    List<Booking> findCompletedBookingsByCompany(
            @Param("companyId") UUID companyId
    );

    @Query("SELECT b FROM Booking b WHERE b.company.company_id = :companyId " +
            "AND b.booking_status = 'PENDING' " +
            "AND b.appointment_date BETWEEN :startDate AND :endDate " +
            "ORDER BY b.appointment_date ASC")
    List<Booking> findPendingBookingsByDateRange(
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT b FROM Booking b WHERE b.company.company_id = :companyId " +
            "AND b.booking_status = 'PENDING' " +
            "AND b.appointment_date BETWEEN :startDate AND :endDate " +
            "ORDER BY b.appointment_date ASC")
    List<Booking> findAllPendingBookings(
            @Param("companyId") UUID companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET " +
            "b.appointment_date = :appointmentDate, " +
            "b.booking_notes = :bookingNotes, " +
            "b.booking_status = :bookingStatus, " +
            "b.payment_status = :paymentStatus, " +
            "b.paymentMethod = :paymentMethod " +  // Note: matches the entity field name
            "WHERE b.booking_id = :bookingId")
    void updateBookingFull(
            @Param("bookingId") Long bookingId,
            @Param("appointmentDate") LocalDateTime appointmentDate,
            @Param("bookingNotes") String bookingNotes,
            @Param("bookingStatus") Booking.BookingStatus bookingStatus,
            @Param("paymentStatus") String paymentStatus,
            @Param("paymentMethod") String paymentMethod
    );

}