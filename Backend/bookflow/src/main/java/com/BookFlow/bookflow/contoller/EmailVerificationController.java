package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.services.EmailVerificactionService;
import com.BookFlow.bookflow.utils.customException.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/verification")
public class EmailVerificationController {

    @Autowired
    private EmailVerificactionService emailVerificactionService;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            emailVerificactionService.verifyToken(token);

            String redirectUrl = frontendUrl + "/login-cmp";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (TokenExpiredException e) {
            log.error("Error verifying email: {}", e.getMessage());

            String errorRedirectUrl = frontendUrl + "/verification-error?message=" + e.getMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(errorRedirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("Unexpected error during email verification: {}", e.getMessage());

            String errorRedirectUrl = frontendUrl + "/error";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(errorRedirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @PostMapping("/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestBody Map<String, String> payload) {
        try {
            String companyEmail = payload.get("email");

            if (companyEmail == null || companyEmail.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Email is required"
                ));
            }

            emailVerificactionService.resendVerificationToken(companyEmail);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Verification link resent successfully"
            ));

        } catch (RuntimeException e) {
            log.error("Error resending verification token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }


    }
}




