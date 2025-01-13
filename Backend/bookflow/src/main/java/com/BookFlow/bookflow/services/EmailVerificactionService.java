package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CompanyDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.VerificationToken;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import com.BookFlow.bookflow.repository.company.VerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailVerificactionService {

    private final VerificationTokenRepo tokenRepo;
    private final CompanyRepo companyRepo;
    private final EmailService emailService;

    @Value("${app.backend.url}")
    private String backendurl;


    @Autowired
    public EmailVerificactionService(VerificationTokenRepo tokenRepo, CompanyRepo companyRepo, EmailService emailService) {
        this.tokenRepo = tokenRepo;
        this.companyRepo = companyRepo;
        this.emailService = emailService;
    }

    public void createVerificationToken(CompanyDTO companyDTO){


        String token = UUID.randomUUID().toString();
        UUID id = companyRepo.findCompanyIdByEmail(companyDTO.getCompanyEmail())
                .orElseThrow(() -> new RuntimeException("Company ID not found for email: " + companyDTO.getCompanyEmail()));


        Company company = new Company();
        company.setCompany_name(companyDTO.getCompanyName());
        company.setCompany_email(companyDTO.getCompanyEmail());
        company.setCompany_id(id);



        VerificationToken verificationToken = new VerificationToken(token, company);
        tokenRepo.save(verificationToken);

        String verificationLink = backendurl + "/api/verification/verify?token=" + token;

        try {
            String emailBody = createEmailBody(company.getCompany_name(),verificationLink);
            emailService.sendEmail(
                    company.getCompany_email(),
                    "Verify Your Email Address",
                    emailBody
            );

        }catch (MatchException e){
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public boolean verifyToken(String token){

        VerificationToken verificationToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }

        if (verificationToken.getExpireAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        Company company = verificationToken.getCompany();
        company.set_verified(true);
        companyRepo.save(company);

        verificationToken.setUsed(true);
        tokenRepo.save(verificationToken);

        return true;


    }

    private String createEmailBody(String companyName, String verificationLink) {
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
            """, companyName, verificationLink);
    }


}
