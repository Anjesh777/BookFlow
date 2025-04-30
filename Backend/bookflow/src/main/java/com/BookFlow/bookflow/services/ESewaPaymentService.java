package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.repository.BookingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ESewaPaymentService {

    private static final String ESEWA_TEST_URL = "https://rc-epay.esewa.com.np/api/epay/main/v2/form";
    private static final String MERCHANT_ID = "EPAYTEST";
    private static final String SECRET_KEY = "8gBm/:&EnhH.1/q";

    @Autowired
    private BookingRepo bookingRepository;

    @Autowired
    private PaymentInvoiceService paymentInvoiceService;

    public Map<String, Object> initiatePayment(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            String transactionUuid = generateUniqueTransactionId(bookingId);
            log.info("Generated transaction UUID: {} for booking: {}", transactionUuid, bookingId);

            // Update booking with new payment reference and status
            booking.setPaymentReference(transactionUuid);
            booking.setPayment_status("PENDING");
            bookingRepository.save(booking);

            // Calculate amounts
            double amount = booking.getExpectedAmount().doubleValue();
            double taxAmount = 0;
            double serviceCharge = 0;
            double deliveryCharge = 0;
            double totalAmount = amount + taxAmount + serviceCharge + deliveryCharge;

            String formattedTotalAmount = formatAmount(totalAmount);

            Map<String, Object> params = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order
            params.put("amount", formatAmount(amount));
            params.put("tax_amount", formatAmount(taxAmount));
            params.put("product_service_charge", formatAmount(serviceCharge));
            params.put("product_delivery_charge", formatAmount(deliveryCharge));
            params.put("total_amount", formattedTotalAmount);
            params.put("transaction_uuid", transactionUuid);
            params.put("product_code", MERCHANT_ID);

            // Set callback URLs
            String baseUrl = "http://localhost:4200/user/booking";
            String successUrl ="http://localhost:4200/user/payment/success";
            String failureUrl ="http://localhost:4200/user/payment/failure";


            params.put("success_url", successUrl);
            params.put("failure_url", failureUrl);
            params.put("cancel_url", baseUrl);

            String signedFieldNames = "total_amount,transaction_uuid,product_code";
            params.put("signed_field_names", signedFieldNames);

            String signature = generateSignature(formattedTotalAmount, transactionUuid, MERCHANT_ID);
            params.put("signature", signature);

            params.put("payment_url", ESEWA_TEST_URL);
            params.put("method", "POST");

            params.put("merchant_id", MERCHANT_ID);
            params.put("booking_id", bookingId.toString());
            params.put("timestamp", new Date().toString());

            log.info("Payment initiated for booking {} with params: {}", bookingId, params);
            return params;

        } catch (Exception e) {
            log.error("Failed to initiate eSewa payment for booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to initiate payment. Please try again.", e);
        }
    }

    private String generateUniqueTransactionId(Long bookingId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        String timestamp = dateFormat.format(new Date());
        Random random = new Random();
        int randomSuffix = random.nextInt(9000) + 1000;
        return "ESEWA-" + timestamp + "-" + bookingId + "-" + randomSuffix;
    }

    private String formatAmount(double amount) {
        if (amount == Math.floor(amount)) {
            return String.valueOf((int)amount);
        } else {
            return String.valueOf(amount);
        }
    }

    private String generateSignature(String totalAmountStr, String transactionUuid, String productCode) {
        try {
            String message = String.format("total_amount=%s,transaction_uuid=%s,product_code=%s",
                    totalAmountStr,
                    transactionUuid,
                    productCode);

            log.info("Signature input string: {}", message);

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hashBytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String hash = Base64.getEncoder().encodeToString(hashBytes);

            log.info("Generated signature: {}", hash);
            return hash;
        } catch (Exception e) {
            log.error("Signature generation error: {}", e.getMessage());
            throw new RuntimeException("Failed to generate signature: " + e.getMessage(), e);
        }
    }

    public boolean verifyPayment(String referenceId, String transactionUuid, String status) {
        log.info("Verifying payment with reference ID: {}, transaction UUID: {}, status: {}",
                referenceId, transactionUuid, status);

        Optional<Booking> bookingOpt = findBookingByTransactionUuid(transactionUuid);

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            log.info("Found booking with ID: {} for transaction: {}", booking.getBooking_id(), transactionUuid);

            if ("COMPLETE".equals(status)) {
                booking.setPayment_status("SUCCESS");
                log.info("Payment successful - Updated booking {} status to SUCCESS", booking.getBooking_id());

                paymentInvoiceService.sendPaymentInvoiceEmail(
                        booking,
                        transactionUuid,
                        booking.getExpectedAmount().doubleValue(),
                        "ONLINE"
                );
            } else {
                booking.setPayment_status("FAILED");
                booking.setPaymentReference(null);
                log.info("Payment failed - Updated booking {} status to FAILED", booking.getBooking_id());

                // Send payment failure email
                paymentInvoiceService.sendPaymentFailureEmail(
                        booking,
                        transactionUuid,
                        booking.getExpectedAmount().doubleValue()
                );
            }

            bookingRepository.save(booking);
            return "COMPLETE".equals(status);
        } else {
            log.error("No booking found for transaction UUID: {}", transactionUuid);
            return false;
        }
    }

    private Optional<Booking> findBookingByTransactionUuid(String transactionUuid) {
        log.info("Finding booking for transaction UUID: {}", transactionUuid);

        try {
            List<Booking> bookings = bookingRepository.findByPaymentReference(transactionUuid);
            if (!bookings.isEmpty()) {
                log.info("Found booking by direct payment reference match");
                return Optional.of(bookings.get(0));
            }


            String[] parts = transactionUuid.split("-");
            if (parts.length >= 3) {
                Long bookingId = Long.valueOf(parts[2]);
                log.info("Extracted booking ID: {} from transaction UUID", bookingId);
                return bookingRepository.findById(bookingId);
            } else {
                log.warn("Transaction UUID does not follow expected format: {}", transactionUuid);
            }
        } catch (Exception e) {
            log.error("Error finding booking by transaction UUID: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Long getBookingIdFromTransaction(String transactionUuid) {
        Optional<Booking> bookingOpt = findBookingByTransactionUuid(transactionUuid);
        return bookingOpt.map(Booking::getBooking_id).orElse(null);
    }
}