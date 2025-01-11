package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.services.EmailVerificactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
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

        } catch (RuntimeException e) {
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

}



