package com.BookFlow.bookflow.contoller.Company;

import com.BookFlow.bookflow.dto.CompanyDTO;
import com.BookFlow.bookflow.services.EmailService;
import com.BookFlow.bookflow.services.EmailVerificactionService;
import com.BookFlow.bookflow.services.RegisterCompanyService;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("all")
public class RegisterCompanyController {

    private final RegisterCompanyService registerCompanyService;
    private final EmailVerificactionService emailVerificactionService;


    @Autowired
    public RegisterCompanyController(RegisterCompanyService registerCompanyService, EmailService emailService, EmailVerificactionService emailVerificactionService) {
        this.registerCompanyService = registerCompanyService;
        this.emailVerificactionService = emailVerificactionService;
    }

    @PostMapping("registercmp")
    public ResponseEntity<Map<String, String>> registerCompany(@RequestBody CompanyDTO CompanyDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            registerCompanyService.registerCompany(CompanyDTO);
            emailVerificactionService.createCompanyVerificationToken(CompanyDTO);


            log.info("Company Register Successful");
            response.put("status", "success");
            response.put("message", "Company Register Successful. We have sent you a link to activate account");


            return ResponseEntity.ok().body(response);
        }
        catch (DuplicateFieldException var1) {
            log.error("Error occurred while registering same company Details: {}", var1.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        catch (DataAccessException var2) {
            System.out.println(CompanyDTO.getCompanyPassword());
            log.error("Error occurred while registering the company: {}", var2.getMessage());
            response.put("status", "error");
            response.put("message", "An error occurred while registering the company.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        catch (Exception var3) {
            log.error("Unexpected error occurred: {}", var3.getMessage());
            response.put("status", "error");
            response.put("message", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }


    }
}