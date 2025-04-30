package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.dto.*;
import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.services.*;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class CustomerController {

    private final LedgerService ledgerService;


    private final UserService userService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private AdminServiceManagement adminServiceManagement;
    @Autowired
    private UserContextUtil userContextUtil;

    public CustomerController(LedgerService ledgerService, UserService userService, AdminNotificationService adminNotificationService) {
        this.ledgerService = ledgerService;
        this.userService = userService;
        this.adminNotificationService = adminNotificationService;
    }


    @GetMapping("/notification")
public ResponseEntity<List<Notification>> getUserNotificationPushbyAdmin(){
    try {
        List<Notification> notification = userService.getRecentUserNotifications(3);
        return ResponseEntity.ok(notification);
    }
    catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    }

    @GetMapping("/get-notification")
    public ResponseEntity<List<CompanyNotification>> getUserNotifications() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getRecentUserNotifications(1);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<CompanyNotification>> getAllNotifications() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getRecentCompanyAllUserNotifications(1);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all-notification")
    public ResponseEntity<List<CompanyNotification>> getAll() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getAllCompanyNotification(10);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-services")
    public ResponseEntity<List<Services>> getCmpService(){
        try {
            List<Services> services = adminServiceManagement.getAllCompanyServices();
            return ResponseEntity.ok(services);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/entries")
    public ResponseEntity<List<LedgerDTO>> getUserLedgerEntries() {
        try {

            List<LedgerDTO> entries = ledgerService.getLedgerEntriesByUserInverted(userContextUtil.getCurrentUser().getUser_id());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error fetching user ledger entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLedgerToCSV() {
        try {
            User currentUser = userContextUtil.getCurrentUser();
            List<LedgerDTO> ledgerEntries = ledgerService.getLedgerEntriesByUserInverted(userContextUtil.getCurrentUser().getUser_id());
            LedgerSummaryDTO ledgerSummary = ledgerService.getUserLedgerSummaryInverted(userContextUtil.getCurrentUser().getUser_id());
            StringWriter stringWriter = new StringWriter();

            BigDecimal totalLedgerBalance = ledgerSummary.getBalance().max(BigDecimal.ZERO);
            BigDecimal totalCredits = ledgerSummary.getTotalCredits().max(BigDecimal.ZERO);
            BigDecimal totalDebits = ledgerSummary.getTotalDebits().max(BigDecimal.ZERO);
            //BigDecimal outstandingBalance = ledgerSummary.getOutstandingBalance().max(BigDecimal.ZERO);

            try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT
                    .withHeader("Date", "Particulars", "Type", "Amount", "Balance"))) {

                for (LedgerDTO entry : ledgerEntries) {
                    csvPrinter.printRecord(
                            entry.getDate(),
                            entry.getParticulars(),
                            entry.getType(),
                            entry.getAmount(),
                            entry.getBalance()
                    );
                }

                csvPrinter.println();
                csvPrinter.printRecord("Total Ledger Balance", "", "", "", totalLedgerBalance);
                csvPrinter.printRecord("Total Credits", "", "", "", totalCredits);
                csvPrinter.printRecord("Total Debits", "", "", "", totalDebits);
          //      csvPrinter.printRecord("Outstanding Balance", "", "", "", outstandingBalance);

                csvPrinter.flush();
            }

            byte[] csvBytes = stringWriter.toString().getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=ledger_export.csv");
            headers.add("Content-Type", "text/csv");

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity <BookingDto> getActiveServices(@RequestBody BookingDto Request) {
        try {

            bookingService.addBooking(Request);
            return ResponseEntity.ok(Request);
        } catch (Exception e) {
            log.error("Error fetching active services", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/upcoming-bookings")
    public ResponseEntity<List<BookingDto>> getUpcomingBookings() {
        try {
            List<BookingDto> upcomingBookings = bookingService.findUpcomingBookingsByUserId();
            return ResponseEntity.ok(upcomingBookings);
        } catch (Exception e) {
            log.error("Error fetching upcoming bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/past-bookings")
    public ResponseEntity<List<BookingDto>> getPastBookings() {
        try {
            List<BookingDto> pastBookings = bookingService.findPastBookingsByUserId();
            return ResponseEntity.ok(pastBookings);
        } catch (Exception e) {
            log.error("Error fetching past bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Object> cancelBooking(@PathVariable String bookingId) {
        try {
            boolean deleted = bookingService.deletePendingBookingIfLessThan12Hours(Long.valueOf(bookingId));
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Booking successfully canceled");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Booking not found or cannot be canceled (less than 12 hours before appointment)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error canceling booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/booking-summary")
    public ResponseEntity<BookingSummaryDTO> getBookingSummary() {
        try {
            BookingSummaryDTO summary = bookingService.getBookingSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching booking summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ledger/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyLedgerSummary() {
        try {
            Map<String, BigDecimal> monthlyData = ledgerService.getMonthlyLedgerSummary();
            return new ResponseEntity<>(monthlyData, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching monthly ledger transactions", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ledger/daily")
    public ResponseEntity<Map<String, BigDecimal>> getDailyLedgerSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        try {
            Map<String, BigDecimal> dailyData = ledgerService.getDailyLedgerSummary(month);
            return new ResponseEntity<>(dailyData, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching daily ledger transactions", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
