package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.CompanyUpdateRequest;
import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.services.AdminService;
import com.BookFlow.bookflow.services.EmailVerificactionService;
import com.BookFlow.bookflow.services.RegisterCompanyService;
import com.BookFlow.bookflow.utils.customException.DuplicateFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailVerificactionService emailVerificactionService;


    @Autowired
    public AdminController(AdminService adminService, EmailVerificactionService emailVerificactionService) {
        this.adminService = adminService;
        this.emailVerificactionService = emailVerificactionService;
    }

    @PostMapping("/add-user")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDetailsDTO userdetailsDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            System.out.println(userdetailsDTO.toString());
            adminService.registerUser(userdetailsDTO);

            log.info("User creation successful");
            response.put("status", "success");
            response.put("message", "User created successfully. Login credentials have been sent to user's email.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (DuplicateFieldException e) {
            log.error("Duplicate field error while creating user: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "User with this email or phone already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        catch (DataAccessException e) {
            log.error("Database error while creating user: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "An error occurred while creating the user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        catch (Exception e) {
            log.error("Unexpected error while creating user: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<UserDetailsDTO>> getAllCompanyUsers() {
        try {
            List<User> users = adminService.getAllCmpUsers();
            List<UserDetailsDTO> userDetails = users.stream()
                    .map(this::convertToUserDetailsDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDetails);
        } catch (RuntimeException e) {
            log.error("Error is ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserDetailsResponse request) {

        var currentTime = LocalDateTime.now();
        Role userRole = Role.fromString(request.getRole());

        try {
            adminService.updateUserDetails(
                    userId,
                    request.getAccount(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getFullname(),
                    userRole,
                    request.isStatus(),
                    currentTime
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    private UserDetailsDTO convertToUserDetailsDTO(User user) {
        UserDetailsResponse dto = new UserDetailsResponse();
        dto.setUser_id(String.valueOf(user.getUser_id()));
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAccount(user.getUsername());
        dto.setRole(user.getRole().toString());
        dto.setStatus(user.is_enabled());
        dto.setCreated_at(String.valueOf(user.getDate()));
        dto.set_main_user(user.getMainuser());


        return dto;
    }





}
