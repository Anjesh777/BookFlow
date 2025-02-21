package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.dto.CashBookDTO;
import com.BookFlow.bookflow.dto.CashBookSummaryDTO;
import com.BookFlow.bookflow.services.AdminService;
import com.BookFlow.bookflow.services.CashBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
public class CashbookController {


    @Autowired
    private CashBookService cashBookService;

    @PostMapping("/transaction")
    public ResponseEntity<CashBookDTO> addTransaction(@RequestBody CashBookDTO transactionDTO) {

        try {
            CashBookDTO savedTransaction = cashBookService.addTransaction(transactionDTO);
            return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("error is ",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<CashBookDTO> getTransaction(@PathVariable Long id) {
        Optional<CashBookDTO> transaction = cashBookService.getTransactionById(id);
        return transaction.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/transaction/{id}")
    public ResponseEntity<CashBookDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody CashBookDTO transactionDTO) {
        try {
            CashBookDTO updatedTransaction = cashBookService.updateTransaction(id, transactionDTO);
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/transaction/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            cashBookService.deleteTransaction(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CashBookDTO>> searchTransactions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CashBookDTO> transactions = cashBookService.searchTransactions(query, page, size);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/by-date")
    public ResponseEntity<Page<CashBookDTO>> getTransactionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CashBookDTO> transactions = cashBookService.getTransactionsByDate(date, page, size);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<CashBookSummaryDTO> getTransactionSummary() {
        try {
            CashBookSummaryDTO summary = cashBookService.getTransactionSummary();
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error is ",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<CashBookDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Page<CashBookDTO> transactions = cashBookService.getAllTransactions(page, size, sortBy, direction);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error is ",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }








}
