package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.LedgerDTO;
import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.services.LedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/ledger-system")
public class LedgerSystemController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerSystemController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }




    @GetMapping("/users")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<UserDetailsResponse> users = ledgerService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error while fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add/ledger")
    public ResponseEntity<LedgerDTO> addLedger(@RequestBody LedgerDTO ledgerDTO) {
        try {
            log.info("Adding new ledger entry: {}", ledgerDTO);
            LedgerDTO savedRecord = ledgerService.addRecord(ledgerDTO);
            return new ResponseEntity<>(savedRecord, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding ledger entry", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user/{userId}/entries")
    public ResponseEntity<List<LedgerDTO>> getUserLedgerEntries(
            @PathVariable UUID userId) {
        try {
            log.info("Fetching ledger entries for user: {}", userId);
            List<LedgerDTO> entries = ledgerService.getLedgerEntriesByUser(userId);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error fetching user ledger entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/user/{userId}/entries/daterange")
    public ResponseEntity<List<LedgerDTO>> getUserLedgerEntriesByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Fetching ledger entries for user: {} between {} and {}", userId, startDate, endDate);
            List<LedgerDTO> entries = ledgerService.getLedgerEntriesByUserAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error fetching user ledger entries by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<LedgerSummaryDTO> getLedgerSummary(@PathVariable UUID userId) {
        try {
            log.info("Fetching ledger summary for user: {}", userId);
            LedgerSummaryDTO summary = ledgerService.getUserLedgerSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching user ledger summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/company/summary")
    public ResponseEntity<LedgerSummaryDTO> getCompanySummary() {
        try {
            log.info("Fetching overall company ledger summary");
            LedgerSummaryDTO summary = ledgerService.getCompanyLedgerSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching company ledger summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/entry/{entryId}")
    public ResponseEntity<LedgerDTO> getLedgerEntry(@PathVariable String entryId) {
        try {
            log.info("Fetching ledger entry: {}", entryId);
            Optional<LedgerDTO> entry = ledgerService.getLedgerEntryById(entryId);
            return entry.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching ledger entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<Void> deleteLedgerEntry(@PathVariable String entryId) {
        try {
            log.info("Deleting ledger entry: {}", entryId);
            boolean deleted = ledgerService.deleteLedgerEntry(entryId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting ledger entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<LedgerDTO>> searchLedgerEntries(@RequestParam String term) {
        try {
            log.info("Searching ledger entries with term: {}", term);
            List<LedgerDTO> results = ledgerService.searchLedgerEntries(term);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error searching ledger entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}