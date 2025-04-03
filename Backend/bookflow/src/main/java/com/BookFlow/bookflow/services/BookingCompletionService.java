package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CashBookDTO;
import com.BookFlow.bookflow.dto.LedgerDTO;
import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.BookingRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class BookingCompletionService {

    @Autowired
    private BookingRepo bookingRepository;
    @Autowired
    private CashBookService cashBookService;
    @Autowired
    private LedgerService ledgerService;
    @Autowired
    private UserRepo userRepository;


    public void completeBooking(Long bookingId, String paymentStatus, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getBooking_status() != Booking.BookingStatus.COMPLETED) {
            booking.setBooking_status(Booking.BookingStatus.COMPLETED);
            booking.setPayment_status(paymentStatus);
            booking.setPaymentMethod(paymentMethod);
            bookingRepository.save(booking);

            if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
                createCashBookEntry(booking);
                createRevenueLedgerEntry(booking);
            } else {
                createAccountsReceivableEntry(booking);
            }
        }
    }

    private void createCashBookEntry(Booking booking) {
        CashBookDTO cashBookDTO = new CashBookDTO();
        cashBookDTO.setDate(LocalDate.now());
        cashBookDTO.setDescription("Payment for Booking #" + booking.getBooking_id() + " - " + booking.getService().getServiceName());
        cashBookDTO.setReceiptAmount(booking.getExpectedAmount());
        cashBookDTO.setPaymentAmount(BigDecimal.ZERO);
        cashBookDTO.setCategory("SERVICE_REVENUE");
        cashBookDTO.setVoucherNumber("BK-" + booking.getBooking_id() + "-" + LocalDate.now());
        cashBookService.addTransaction(cashBookDTO);
    }

    private void createRevenueLedgerEntry(Booking booking) {
        User user = userRepository.findById(booking.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LedgerDTO revenueEntry = new LedgerDTO();
        revenueEntry.setDate(LocalDate.now());
        revenueEntry.setParticulars("Service Revenue: " + booking.getService().getServiceName());
        revenueEntry.setAmount(booking.getExpectedAmount());
        revenueEntry.setType("credit");
        revenueEntry.setReferenceNumber("REV-BK-" + booking.getBooking_id());
        revenueEntry.setNote("Completed booking payment received via " + booking.getPaymentMethod());
        revenueEntry.setUser_id(user.getUser_id().toString());
        ledgerService.addRecord(revenueEntry);
    }

    private void createAccountsReceivableEntry(Booking booking) {
        User user = userRepository.findById(booking.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LedgerDTO ledgerEntry = new LedgerDTO();
        ledgerEntry.setDate(LocalDate.now());
        ledgerEntry.setAmount(booking.getExpectedAmount());
        ledgerEntry.setUser_id(user.getUser_id().toString());

        if ("PENDING".equals(booking.getPayment_status()) &&
                Booking.BookingStatus.COMPLETED.equals(booking.getBooking_status())) {
            // For pending payment but completed booking - create DEBIT entry
            ledgerEntry.setParticulars("To Account Accounts Receivable For Service - " +
                    booking.getService().getServiceName() + " For Duration of " + booking.getDuration() + " HR");
            ledgerEntry.setType("debit");
            ledgerEntry.setReferenceNumber("AR-BK-" + booking.getBooking_id());
            ledgerEntry.setNote("Unpaid booking - Amount due from customer");
        } else if ("SUCCESS".equals(booking.getPayment_status()) &&
                Booking.BookingStatus.COMPLETED.equals(booking.getBooking_status())) {
            ledgerEntry.setParticulars("Payment Received For Service - " +
                    booking.getService().getServiceName() + " For Duration of " + booking.getDuration() + " HR");
            ledgerEntry.setType("credit");
            ledgerEntry.setReferenceNumber("PAY-BK-" + booking.getBooking_id());
            ledgerEntry.setNote("Payment received for completed booking via " + booking.getPaymentMethod());
        }

        ledgerService.addRecord(ledgerEntry);
    }

    public void recordPayment(Long bookingId, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Update booking status
        booking.setPayment_status("SUCCESS");
        booking.setPaymentMethod(paymentMethod);
        bookingRepository.save(booking);

        CashBookDTO cashBookDTO = new CashBookDTO();
        cashBookDTO.setDate(LocalDate.now());
        cashBookDTO.setDescription("Payment received for Booking #" + booking.getBooking_id());
        cashBookDTO.setReceiptAmount(booking.getExpectedAmount());
        cashBookDTO.setPaymentAmount(BigDecimal.ZERO);
        cashBookDTO.setCategory("PAYMENT_RECEIVED");
        cashBookService.addTransaction(cashBookDTO);

        User user = userRepository.findById(booking.getUser_id())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LedgerDTO paymentEntry = new LedgerDTO();
        paymentEntry.setDate(LocalDate.now());
        paymentEntry.setParticulars("Payment Received - BK-" + booking.getBooking_id());
        paymentEntry.setAmount(booking.getExpectedAmount());
        paymentEntry.setType("credit");
        paymentEntry.setReferenceNumber("PYMT-" + booking.getBooking_id());
        paymentEntry.setNote("Payment via " + paymentMethod);
        paymentEntry.setUser_id(user.getUser_id().toString());
        ledgerService.addRecord(paymentEntry);
    }
}