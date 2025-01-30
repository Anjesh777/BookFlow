package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.services.ForgotPasswordService;
import com.BookFlow.bookflow.utils.customException.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("all")
@Slf4j
public class PasswordResetController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");

            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Email is required"
                ));
            }
            forgotPasswordService.createPasswordResetToken(username);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Password reset instructions sent to your email"
            ));

        } catch (UsernameNotFoundException e) {
            log.error("User not found while processing forgot password request: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "If an account exists with this email, you will receive password reset instructions"
            ));

        } catch (Exception e) {
            log.error("Unexpected error during forgot password process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to process password reset request"
            ));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam("token") String token,
            @RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");

            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "New password is required"
                ));
            }

            forgotPasswordService.resetPassword(token, newPassword);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Password has been reset successfully"
            ));

        } catch (TokenExpiredException e) {
            log.error("Token expired while resetting password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Password reset link has expired"
            ));

        } catch (Exception e) {
            log.error("Unexpected error during password reset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to reset password"
            ));
        }
    }



}
