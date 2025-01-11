package com.BookFlow.bookflow.contoller.Company;

import com.BookFlow.bookflow.dto.Company.CompanyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("Company")
public class LoginCompanyController {

    @PostMapping("logincmp")
    public ResponseEntity<Map<String,String>> loginCompany(@RequestBody CompanyDTO companyDTO){
        Map<String, String> response = new HashMap<>();

        try {
            System.out.println(companyDTO.getCompanyName());
            System.out.println(companyDTO.getCompanyPassword());
            response.put("status", "success");
            response.put("message", "Company Login Successful. We have sent you a link to activate account");


            return ResponseEntity.ok().body(response);

        }
        catch (Exception var1){

            response.put("status", "error");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        }
    }

}
