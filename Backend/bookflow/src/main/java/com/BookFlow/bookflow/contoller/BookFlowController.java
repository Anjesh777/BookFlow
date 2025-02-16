package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.*;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.services.CompanyService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/bookflow")
public class BookFlowController {

    private final UserService userService;
    private final CompanyService companyService;

    @Autowired
    public BookFlowController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("company-details")
    public ResponseEntity<Map<String, Object>> getCompanyDetails() {
        Map<String, Object> response = new HashMap<>();

        try {
            long companyCount = companyService.countServie();
            log.info("Successfully retrieved company count: {}", companyCount);

            String growthPercentage = companyService.getGrowthPercentage();
            log.info("Successfully retrieved user growth percentage: {}", growthPercentage);

            long userCount = userService.countServie();
            log.info("Successfully retrieved user count: {}", userCount);

            String companyGrowthPercentage = companyService.getCompanyGrowthPercentage();
            log.info("Successfully retrieved user count: {}", userCount);

            response.put("status", "success");
            response.put("company_count", companyCount);
            response.put("user_growth_percentage", growthPercentage);
            response.put("user_count", userCount);
            response.put("company_growth_percentage",companyGrowthPercentage);
            response.put("message", "Company and user details retrieved successfully");

            return ResponseEntity.ok().body(response);
        } catch (DataAccessException var1) {
            log.error("Database error while retrieving company and user details: {}", var1.getMessage());
            response.put("status", "error");
            response.put("message", "Error accessing data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception var2) {
            log.error("Unexpected error occurred while retrieving company and user details: {}", var2.getMessage());
            response.put("status", "error");
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/recent-3")
    public ResponseEntity<List<Company>> getRecent3Companies() {
        return ResponseEntity.ok(companyService.get3Companies());
    }

    @GetMapping("/recent-all")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }


    @PutMapping("/update/{companyId}")
    public ResponseEntity<Void> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody CompanyUpdateRequest request
    ) {


        companyService.updateCompanyDetails(
                companyId,
                request.getCompany_name(),
                request.getRegistration_number(),
                request.getCompany_email(),
                request.getCompany_phone(),
                request.getCompany_address(),
                request.isEnabled(),
                request.isVerified()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<Company>> searchCompanies(@RequestBody CompanyFilterDTO filter) {
        List<Company> results = companyService.searchCompanies(filter);
        return ResponseEntity.ok(results);
    }

















}
