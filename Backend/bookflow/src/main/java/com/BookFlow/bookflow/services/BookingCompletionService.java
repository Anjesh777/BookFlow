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

            createFinancialEntries(booking);
        }
    }

    private void createFinancialEntries(Booking booking) {
        if ("SUCCESS".equalsIgnoreCase(booking.getPayment_status())) {
            handleSuccessfulPayment(booking);
        } else {
            handlePendingPayment(booking);
        }
    }

    private void handleSuccessfulPayment(Booking booking) {
        if (!cashBookEntryExists(booking)) {
            createCashBookEntry(booking);
        }
        if (!ledgerEntryExists(booking, "REV-BK-")) {
            createRevenueLedgerEntry(booking);
        }
    }

    private void handlePendingPayment(Booking booking) {
        if (!ledgerEntryExists(booking, "AR-BK-")) {
            createAccountsReceivableEntry(booking);
        }
    }

    public void recordPayment(Long bookingId, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getBooking_status() == Booking.BookingStatus.COMPLETED) {
            booking.setPayment_status("SUCCESS");
            booking.setPaymentMethod(paymentMethod);
            bookingRepository.save(booking);

            if (!cashBookEntryExists(booking)) {
                createCashBookEntry(booking);
            }
            updateLedgerForPayment(booking);
        }
    }

    private void updateLedgerForPayment(Booking booking) {
        String arReference = "AR-BK-" + booking.getBooking_id();
        if (ledgerService.existsByReferenceNumber(arReference)) {
            ledgerService.deleteByReferenceNumber(arReference);
        }

        String revReference = "REV-BK-" + booking.getBooking_id();
        if (!ledgerService.existsByReferenceNumber(revReference)) {
            createRevenueLedgerEntry(booking);
        }
    }

    private boolean cashBookEntryExists(Booking booking) {
        String voucherNumber = "BK-" + booking.getBooking_id() + "-" + LocalDate.now();
        return cashBookService.existsByVoucherNumber(voucherNumber);
    }

    private boolean ledgerEntryExists(Booking booking, String prefix) {
        String referenceNumber = prefix + booking.getBooking_id();
        return ledgerService.existsByReferenceNumber(referenceNumber);
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
        ledgerEntry.setParticulars("To Account Accounts Receivable For Service - " +
                booking.getService().getServiceName() + " For Duration of " + booking.getDuration() + " HR");
        ledgerEntry.setAmount(booking.getExpectedAmount());
        ledgerEntry.setType("debit");
        ledgerEntry.setReferenceNumber("AR-BK-" + booking.getBooking_id());
        ledgerEntry.setNote("Unpaid booking - Amount due from customer");
        ledgerEntry.setUser_id(user.getUser_id().toString());
        ledgerService.addRecord(ledgerEntry);
    }


}