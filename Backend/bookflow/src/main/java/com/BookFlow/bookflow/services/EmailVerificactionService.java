package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CompanyDTO;
import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.model.VerificationToken;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import com.BookFlow.bookflow.repository.company.VerificationTokenRepo;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import com.BookFlow.bookflow.utils.customException.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class EmailVerificactionService {

    @Autowired
    private VerificationTokenRepo tokenRepo;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private EmailService emailService;

    @Value("${app.backend.url}")
    private String backendurl;


    @Transactional
    public void createCompanyVerificationToken(CompanyDTO companyDTO) {
        Company company = companyRepo.findByCompanyEmail(companyDTO.getCompanyEmail())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        User user = userRepository.findByEmail(companyDTO.getCompanyEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, company);
        verificationToken.setUser(user);
        tokenRepo.save(verificationToken);


        String verificationLink = backendurl + "/api/verification/verify?token=" + token;
        sendVerificationEmail(company.getCompany_email(), company.getCompany_name(), verificationLink);
    }

    @Transactional
    public void createUserCredentialsAndVerification(UserDetailsDTO userDetailsDTO, String defaultPassword) {
        User user = userRepository.findByEmail(userDetailsDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepo.save(verificationToken);

        String verificationLink = backendurl + "/api/verification/verify?token=" + token;

        sendCredentialsAndVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                defaultPassword,
                verificationLink
        );
    }

    private void sendCredentialsAndVerificationEmail(String email, String username,
                                                     String password, String verificationLink) {
        try {
            String emailBody = createCredentialsEmailBody(username, password, verificationLink);
            emailService.sendEmail(
                    email,
                    "Welcome to BookFlow - Your Account Credentials",
                    emailBody
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send credentials email", e);
        }
    }


    private void sendVerificationEmail(String email, String name, String verificationLink) {
        try {
            String emailBody = createEmailBody(name, verificationLink);
            emailService.sendEmail(
                    email,
                    "Verify Your Email Address",
                    emailBody
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void verifyUserToken(String token) {
        VerificationToken verificationToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException("Token has expired");
        }

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }

        User user = verificationToken.getUser();

        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepo.save(verificationToken);

    }

    @Transactional
    public void resendUserVerificationToken(String userName) {
        try {
            User user = userRepository.findByUsername(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Optional<VerificationToken> tokenObj = tokenRepo.findByUserId(user.getUser_id());
            if (tokenObj.isPresent() && tokenObj.get().isUsed()) {
                log.error("User is already verified: {}", userName);
                throw new DuplicateFieldException("User is already verified");
            }

            if (tokenObj.isPresent()) {
                tokenRepo.deleteByUserId(user.getUser_id());
            }

            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, user);
            tokenRepo.save(verificationToken);

            String verificationLink = backendurl + "/api/verification/verify?token=" + token;

            try {
                sendVerificationEmail(user.getEmail(), user.getUsername(), verificationLink);
                log.info("Verification email resent successfully to user: {}", userName);
            } catch (Exception e) {
                log.error("Failed to send verification email to user: {}", userName, e);
                throw new RuntimeException("Failed to send verification email");
            }

        } catch (UsernameNotFoundException e) {
            log.error("Failed to resend verification token - User not found: {}", userName);
            throw e;
        } catch (DuplicateFieldException e) {
            log.error("Failed to resend verification token - User already verified: {}", userName);
            throw e;
        } catch (Exception e) {
            log.error("Failed to resend verification token - Unexpected error for user: {}", userName, e);
            throw new RuntimeException("Failed to resend verification token: " + e.getMessage());
        }
    }


    private String createEmailBody(String UserName, String verificationLink) {
        return String.format("""
                  <html>
                          <head>
                              <style>
                                  body {\s
                                      font-family: Arial, sans-serif;\s
                                      line-height: 1.6;\s
                                      color: #333;\s
                                  }
                                  .container {\s
                                      max-width: 600px;\s
                                      margin: 0 auto;\s
                                      padding: 20px;\s
                                  }
                                  .button {
                                      display: inline-block;
                                      padding: 10px 20px;
                                      background-color: #007bff;
                                      color: white !important;  /* Force white color */
                                      text-decoration: none;
                                      border-radius: 5px;
                                      margin: 20px 0;
                                      font-weight: 500;
                                      font-size: 16px;
                                  }
                                  .button:hover {
                                      background-color: #0056b3;
                                  }
                                  .footer {\s
                                      color: #666;\s
                                      font-size: 0.9em;\s
                                      margin-top: 30px;\s
                                  }
                                  /* Ensure link color is white and stays white */
                                  a.button {
                                      color: white !important;
                                      text-decoration: none;
                                  }
                              </style>
                          </head>
                          <body>
                              <div class="container">
                                  <h2>Welcome to BookFlow, %s!</h2>
                                  <p>Thank you for registering. Please click the button below to verify your email address:</p>
                                  <p><a href="%s" class="button">Verify Email Address</a></p>
                                  <p>This link will expire in 1 hour.</p>
                                  <p class="footer">If you didn't create this account, please ignore this email.</p>
                              </div>
                          </body>
                      </html>
            """, UserName, verificationLink);
    }


    private String createCredentialsEmailBody(String username, String password, String verificationLink) {
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
                    .credentials {
                        background-color: #f8f9fa;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .button:hover {
                        background-color: #0056b3;
                    }
                    .footer {
                        color: #666;
                        font-size: 0.9em;
                        margin-top: 30px;
                    }
                    a.button {
                        color: white !important;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Welcome to BookFlow, %s!</h2>
                    <p>Your account has been created successfully. Here are your login credentials:</p>
                    
                    <div class="credentials">
                        <p><strong>Username:</strong> %s</p>
                        <p><strong>Password:</strong> %s</p>
                    </div>
                    
                    <p>Please click the button below to verify your email address:</p>
                    <p><a href="%s" class="button">Verify Email Address</a></p>
                    
                    <p>For security reasons, please change your password after your first login.</p>
                    <p>This verification link will expire in 1 hour.</p>
                    
                    <p class="footer">If you didn't expect this account creation, please contact your administrator.</p>
                </div>
            </body>
        </html>
    """, username, username, password, verificationLink);
    }



}
