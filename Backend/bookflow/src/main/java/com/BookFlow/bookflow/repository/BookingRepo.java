package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface BookingRepo extends JpaRepository<Booking,Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO service_book (service_id, user_id, booking_date, appointment_date, payment_status, " +
            "booking_notes, booking_status, company_id, duration, end_time, expected_amount, fixed_dated_time) " +
            "VALUES (:serviceId, :userId, :bookingDate, :appointmentDate, :paymentStatus, :bookingNotes, " +
            ":bookingStatus, :companyId, :duration, :endTime, :expectedAmount, :fixedDatedTime)",
            nativeQuery = true)
    void insertBooking(
            @Param("serviceId") String serviceId,
            @Param("userId") UUID userId,
            @Param("bookingDate") LocalDateTime bookingDate,
            @Param("appointmentDate") LocalDateTime appointmentDate,
            @Param("paymentStatus") boolean paymentStatus,
            @Param("bookingNotes") String bookingNotes,
            @Param("bookingStatus") String bookingStatus,
            @Param("companyId") Long companyId,
            @Param("duration") String duration,
            @Param("endTime") LocalDateTime endTime,
            @Param("expectedAmount") BigDecimal expectedAmount,
            @Param("fixedDatedTime") LocalDateTime fixedDatedTime
    );

    @Query("SELECT b FROM Booking b WHERE b.user_id = :userId AND (b.booking_status = 'PENDING' OR b.booking_status = 'CONFIRMED') ORDER BY b.appointment_date ASC")
    List<Booking> findPendingBookingsByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM Booking b WHERE b.user_id = :userId AND b.booking_status = 'COMPLETED' ORDER BY b.appointment_date DESC")
    List<Booking> findCompletedBookingsByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.booking_id = :bookingId AND b.booking_status = 'PENDING' AND b.appointment_date > :cutoffTime")
    int deletePendingBookingById(@Param("bookingId") Long bookingId, @Param("cutoffTime") LocalDateTime cutoffTime);



}
