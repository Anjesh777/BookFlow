package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.services.EmailVerificactionService;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import com.BookFlow.bookflow.utils.customException.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            emailVerificactionService.verifyUserToken(token);

            String redirectUrl = frontendUrl + "/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (TokenExpiredException e) {
            log.error("Error verifying  email: {}", e.getMessage());

            String errorRedirectUrl = frontendUrl + "/verification-error?message=" + e.getMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(errorRedirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("Unexpected error during company email verification: {}", e.getMessage());

            String errorRedirectUrl = frontendUrl + "/error";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(errorRedirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @PostMapping("/resend-token")
    public ResponseEntity<Map<String, String>> resendVerificationToken(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");

            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Username is required"
                ));
            }

            emailVerificactionService.resendUserVerificationToken(username);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Verification link resent successfully"
            ));

        } catch (UsernameNotFoundException e) {
            log.error("User not found while resending verification token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));

        } catch (DuplicateFieldException e) {
            log.error("User already verified: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "User is already verified"
            ));

        } catch (Exception e) {
            log.error("Unexpected error while resending verification token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to resend verification token"
            ));
        }
    }

}




