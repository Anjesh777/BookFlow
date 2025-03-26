package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.dto.LedgerDTO;
import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.model.CompanyNotification;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.model.Services;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.services.AdminNotificationService;
import com.BookFlow.bookflow.services.LedgerService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class CustomerService {

    private final LedgerService ledgerService;


    private final UserService userService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    private UserRepo userRepo;

    public CustomerService(LedgerService ledgerService, UserService userService, AdminNotificationService adminNotificationService) {
        this.ledgerService = ledgerService;
        this.userService = userService;
        this.adminNotificationService = adminNotificationService;
    }


    @GetMapping("/notification")
public ResponseEntity<List<Notification>> getUserNotificationPushbyAdmin(){
    try {
        List<Notification> notification = userService.getRecentUserNotifications(2);
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
            List<Services> services = adminNotificationService.getAllCompanyService();
            return ResponseEntity.ok(services);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/entries")
    public ResponseEntity<List<LedgerDTO>> getUserLedgerEntries() {
        try {

            List<LedgerDTO> entries = ledgerService.getLedgerEntriesByUser(ledgerService.getCurrentUser().getUser_id());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error fetching user ledger entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLedgerToCSV() {
        try {
            List<LedgerDTO> ledgerEntries = ledgerService.getLedgerEntriesByUser(ledgerService.getCurrentUser().getUser_id());
            LedgerSummaryDTO ledgerSummary = ledgerService.getUserLedgerSummary(ledgerService.getCurrentUser().getUser_id());
            StringWriter stringWriter = new StringWriter();

            BigDecimal totalLedgerBalance = ledgerSummary.getBalance().max(BigDecimal.ZERO);
            BigDecimal totalCredits = ledgerSummary.getTotalCredits().max(BigDecimal.ZERO);
            BigDecimal totalDebits = ledgerSummary.getTotalDebits().max(BigDecimal.ZERO);
            BigDecimal outstandingBalance = ledgerSummary.getOutstandingBalance().max(BigDecimal.ZERO);

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
                csvPrinter.printRecord("Outstanding Balance", "", "", "", outstandingBalance);

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



}
