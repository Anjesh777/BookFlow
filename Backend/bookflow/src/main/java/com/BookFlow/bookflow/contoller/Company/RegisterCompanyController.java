package com.BookFlow.bookflow.contoller.Company;

import com.BookFlow.bookflow.dto.Company.RegisterCompanyDTO;
import com.BookFlow.bookflow.services.RegisterCompanyService;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Company")
public class RegisterCompanyController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterCompanyController.class);
    private final RegisterCompanyService registerCompanyService;


    @Autowired
    public RegisterCompanyController(RegisterCompanyService registerCompanyService) {
        this.registerCompanyService = registerCompanyService;
    }

    @PostMapping("register")
    public ResponseEntity<?> registerCompany(@RequestBody RegisterCompanyDTO registerCompanyDTO){

        try {
            registerCompanyService.registerCompany(registerCompanyDTO);
            logger.error("Company Register Successful");
            return ResponseEntity.ok().body("Company Register Successful We have send you link to activate account");
        }
        catch (DuplicateFieldException var1) {
            logger.error("Error occurred while registering same company Details: {}", var1.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(var1.getMessage());
        }
        catch (DataAccessException var2) {
            System.out.println(registerCompanyDTO.getCompanyEmail());
            logger.error("Error occurred while registering the company: {}", var2.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the company.");
        }
        catch (Exception var3) {
            logger.error("Unexpected error occurred: {}", var3.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }



    }



}
