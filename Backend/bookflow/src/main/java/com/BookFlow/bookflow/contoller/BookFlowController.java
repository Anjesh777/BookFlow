package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.services.CompanyService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("count-users")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Map<String, Object> response = new HashMap<>();

        try {
            long userCount = userService.countServie();

            log.info("Successfully retrieved user count: {}", userCount);
            response.put("status", "success");
            response.put("count", userCount);
            response.put("message", "User count retrieved successfully");

            return ResponseEntity.ok().body(response);
        }
        catch (DataAccessException var1) {
            log.error("Database error while counting users: {}", var1.getMessage());
            response.put("status", "error");
            response.put("message", "Error accessing user data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        catch (Exception var2) {
            log.error("Unexpected error occurred while counting users: {}", var2.getMessage());
            response.put("status", "error");
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("count-company")
    public ResponseEntity<Map<String,Object>> getCompanyCount(){
        Map<String, Object> response = new HashMap<>();

        try {
            long companyCount = companyService.countServie();

            log.info("Successfully retrieved company count: {}", companyCount);
            response.put("status", "success");
            response.put("count", companyCount);
            response.put("message", "company count retrieved successfully");

            return ResponseEntity.ok().body(response);
        }
        catch (DataAccessException var1) {
            log.error("Database error while counting company: {}", var1.getMessage());
            response.put("status", "error");
            response.put("message", "Error accessing user data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        catch (Exception var2) {
            log.error("Unexpected error occurred while counting company: {}", var2.getMessage());
            response.put("status", "error");
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }





}
