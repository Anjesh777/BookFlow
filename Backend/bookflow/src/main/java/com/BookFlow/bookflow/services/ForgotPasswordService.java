package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.PasswordResetToken;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.PasswordResetTokenRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.customException.TokenExpiredException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class ForgotPasswordService {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.backend.url}")
    private String backendUrl;
    @Value("${app.frontend.url}")
    private String frontendUrl;



    @Transactional
    public void createPasswordResetToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        passwordResetTokenRepo.deleteByUserId(user.getUser_id());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        passwordResetTokenRepo.save(resetToken);

        String resetLink = frontendUrl +  "/set-password?token=" + token;
        sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Password reset token has expired");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Password reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepo.save(resetToken);
    }

    private void sendPasswordResetEmail(String email, String userName, String resetLink) {
        try {
            String emailBody = createEmailBody(userName, resetLink);
            emailService.sendEmail(
                    email,
                    "Reset Your Password",
                    emailBody
            );
        } catch (Exception e) {
            log.error("Failed to send password reset email to user: {}", userName, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String createEmailBody(String userName, String resetLink) {
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
                            }
                            .button:hover {
                                background-color: #0056b3;
                            }
                            .footer { 
                                color: #666; 
                                font-size: 0.9em; 
                                margin-top: 30px; 
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h2>Password Reset Request</h2>
                            <p>Hello %s,</p>
                            <p>We received a request to reset your password. Click the button below to create a new password:</p>
                            <p><a href="%s" class="button">Reset Password</a></p>
                            <p>This link will expire in 1 hour.</p>
                            <p class="footer">If you didn't request this password reset, please ignore this email.</p>
                        </div>
                    </body>
                </html>
                """, userName, resetLink);
    }





}
