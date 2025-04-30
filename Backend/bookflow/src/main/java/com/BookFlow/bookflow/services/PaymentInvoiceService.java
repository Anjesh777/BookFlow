package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.Booking;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class PaymentInvoiceService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepo userRepo;

    /**
     * Send an invoice email to the user when payment is successful
     */
    public void sendPaymentInvoiceEmail(Booking booking, String transactionId, Double amount, String paymentMethod) {
        try {

            String userEmail = userRepo.findById(booking.getUser_id())
                    .map(User::getEmail)
                    .orElseThrow(() -> new RuntimeException("User not found for ID: " + booking.getUser_id()));
            String userName = String.valueOf(userRepo.findFullnameById(booking.getUser_id()));


            String subject = "Payment Receipt - BookFlow Booking #" + booking.getBooking_id();
            String emailBody = createInvoiceEmailBody(
                    userName,
                    booking,
                    transactionId,
                    amount,
                    paymentMethod
            );

            // Send email
            emailService.sendEmail(userEmail, subject, emailBody);
            log.info("Payment invoice email sent to {} for booking {}", userEmail, booking.getBooking_id());
        } catch (Exception e) {
            log.error("Failed to send payment invoice email for booking {}: {}",
                    booking.getBooking_id(), e.getMessage(), e);
        }
    }


    private String createInvoiceEmailBody(String userName, Booking booking,
                                          String transactionId, Double amount, String paymentMethod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        String formattedDate = dateFormat.format(new Date());

        // Format invoice number - combination of booking ID and timestamp
        SimpleDateFormat invoiceFormat = new SimpleDateFormat("yyyyMMdd");
        String invoiceNumber = "INV-" + invoiceFormat.format(new Date()) + "-" + booking.getBooking_id();

        return String.format("""
                        <html>
                            <head>
                                <style>
                                    body {
                                        font-family: Arial, sans-serif;
                                        line-height: 1.6;
                                        color: #333;
                                    }
                                    .container {
                                        max-width: 600px;
                                        margin: 0 auto;
                                        padding: 20px;
                                        border: 1px solid #ddd;
                                        border-radius: 5px;
                                    }
                                    .header {
                                        background-color: #f8f9fa;
                                        padding: 15px;
                                        text-align: center;
                                        border-bottom: 2px solid #007bff;
                                    }
                                    .invoice-details {
                                        margin: 20px 0;
                                        padding: 15px;
                                        background-color: #f8f9fa;
                                        border-radius: 5px;
                                    }
                                    .booking-details {
                                        margin: 20px 0;
                                    }
                                    .amount {
                                        font-size: 18px;
                                        font-weight: bold;
                                        color: #007bff;
                                    }
                                    .footer {
                                        margin-top: 30px;
                                        text-align: center;
                                        font-size: 0.9em;
                                        color: #666;
                                    }
                                    table {
                                        width: 100%%;
                                        border-collapse: collapse;
                                        margin: 20px 0;
                                    }
                                    th, td {
                                        padding: 10px;
                                        border-bottom: 1px solid #ddd;
                                        text-align: left;
                                    }
                                    th {
                                        background-color: #f2f2f2;
                                    }
                                    .status-success {
                                        color: #28a745;
                                        font-weight: bold;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="container">
                                    <div class="header">
                                        <h2>Payment Receipt</h2>
                                        <p>Thank you for your payment!</p>
                                    </div>
                        
                                    <div class="invoice-details">
                                        <h3>Invoice #%s</h3>
                                        <p><strong>Date:</strong> %s</p>
                                        <p><strong>Transaction ID:</strong> %s</p>
                                        <p><strong>Payment Method:</strong> %s</p>
                                        <p><strong>Status:</strong> <span class="status-success">PAID</span></p>
                                    </div>
                        
                                    <div class="booking-details">
                                        <h3>Booking Details</h3>
                                        <table>
                                            <tr>
                                                <th>Description</th>
                                                <th>Amount</th>
                                            </tr>
                                            <tr>
                                                <td>Booking #%s</td>
                                                <td>Rs. %.2f</td>
                                            </tr>
                                        </table>
                        
                                        <div style="text-align: right; margin-top: 20px;">
                                            <p><strong>Total Amount:</strong> <span class="amount">Rs. %.2f</span></p>
                                        </div>
                                    </div>
                        
                                    <div class="footer">
                                        <p>This is an automated email. Please do not reply to this message.</p>
                                        <p>If you have any questions, please contact our support team.</p>
                                        <p>&copy; %d BookFlow. All rights reserved.</p>
                                    </div>
                                </div>
                            </body>
                        </html>
                        """,
                invoiceNumber,
                formattedDate,
                transactionId,
                paymentMethod,
                booking.getBooking_id(),
                amount,
                amount,
                java.time.Year.now().getValue()
        );
    }

    public void sendPaymentFailureEmail(Booking booking, String transactionId, Double amount) {
        try {
            String userEmail = String.valueOf(userRepo.findByEmail(String.valueOf(booking.getUser_id())));
            String userName = String.valueOf(userRepo.findFullnameById(booking.getUser_id()));

            // Create failure email
            String subject = "Payment Failed - BookFlow Booking #" + booking.getBooking_id();
            String emailBody = createPaymentFailureEmailBody(
                    userName,
                    booking,
                    transactionId,
                    amount
            );

            // Send email
            emailService.sendEmail(userEmail, subject, emailBody);
            log.info("Payment failure email sent to {} for booking {}", userEmail, booking.getBooking_id());
        } catch (Exception e) {
            log.error("Failed to send payment failure email for booking {}: {}",
                    booking.getBooking_id(), e.getMessage(), e);
        }
    }

    /**
     * Create HTML content for the payment failure email
     */
    private String createPaymentFailureEmailBody(String userName, Booking booking,
                                                 String transactionId, Double amount) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        String formattedDate = dateFormat.format(new Date());

        return String.format("""
        <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        border: 1px solid #ddd;
                        border-radius: 5px;
                    }
                    .header {
                        background-color: #f8f9fa;
                        padding: 15px;
                        text-align: center;
                        border-bottom: 2px solid #dc3545;
                    }
                    .payment-details {
                        margin: 20px 0;
                        padding: 15px;
                        background-color: #f8f9fa;
                        border-radius: 5px;
                    }
                    .booking-details {
                        margin: 20px 0;
                    }
                    .button {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #007bff;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: 500;
                        font-size: 16px;
                        text-align: center;
                    }
                    .button:hover {
                        background-color: #0056b3;
                    }
                    .amount {
                        font-size: 18px;
                        font-weight: bold;
                    }
                    .footer {
                        margin-top: 30px;
                        text-align: center;
                        font-size: 0.9em;
                        color: #666;
                    }
                    .status-failed {
                        color: #dc3545;
                        font-weight: bold;
                    }
                    a.button {
                        color: white !important;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Payment Failed</h2>
                        <p>We were unable to process your payment.</p>
                    </div>
                    
                    <div class="payment-details">
                        <p><strong>Date:</strong> %s</p>
                        <p><strong>Transaction ID:</strong> %s</p>
                        <p><strong>Status:</strong> <span class="status-failed">FAILED</span></p>
                    </div>
                    
                    <div class="booking-details">
                        <h3>Booking Details</h3>
                        <p><strong>Booking ID:</strong> %s</p>
                        <p><strong>Amount:</strong> <span class="amount">Rs. %.2f</span></p>
                    </div>
                    
                    <p>Please try again or contact our support team if you continue to experience issues.</p>
                    
                    <div style="text-align: center;">
                        <a href="http://localhost:4200/user/booking/%s" class="button">Try Again</a>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply to this message.</p>
                        <p>If you have any questions, please contact our support team.</p>
                        <p>&copy; %d BookFlow. All rights reserved.</p>
                    </div>
                </div>
            </body>
        </html>
        """,
                formattedDate,
                transactionId,
                booking.getBooking_id(),
                amount,
                booking.getBooking_id(),
                java.time.Year.now().getValue()
        );
    }

}

