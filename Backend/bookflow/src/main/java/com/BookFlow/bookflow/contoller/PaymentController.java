package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.services.BookingCompletionService;
import com.BookFlow.bookflow.services.ESewaPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    @Autowired
    private ESewaPaymentService eSewaPaymentService;

    @Autowired
    private BookingCompletionService bookingCompletionService;

    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<Map<String, Object>> initiatePayment(@PathVariable String bookingId) {
        try {
            Map<String, Object> paymentData = eSewaPaymentService.initiatePayment(Long.valueOf(bookingId));
            return ResponseEntity.ok(paymentData);
        } catch (Exception e) {
            log.error("Error initiating payment for booking ID: {}", bookingId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to initiate payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> paymentData) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract relevant fields from the payment data
            String transactionCode = paymentData.get("transaction_code");
            String transactionUuid = paymentData.get("transaction_uuid");
            String status = paymentData.get("status");

            log.info("Verifying payment - Transaction Code: {}, UUID: {}, Status: {}",
                    transactionCode, transactionUuid, status);

            boolean verified = eSewaPaymentService.verifyPayment(transactionCode, transactionUuid, status);

            if (verified) {

                Long bookingId = eSewaPaymentService.getBookingIdFromTransaction(transactionUuid);
                if (bookingId != null) {
                    bookingCompletionService.recordPayment(bookingId,"ONLINE");
                    log.info("Booking {} completed successfully with cashbook and ledger entries", bookingId);
                }

                response.put("status", "success");
                response.put("message", "Payment verified successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Payment verification failed");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error verifying payment", e);
            response.put("status", "error");
            response.put("message", "Error processing payment verification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}