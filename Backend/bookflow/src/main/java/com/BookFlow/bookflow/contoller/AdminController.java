package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.*;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.CompanyNotification;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.services.AdminNotificationService;
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

import java.time.LocalDate;
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
    private final AdminNotificationService adminNotificationService;
    private final EmailVerificactionService emailVerificactionService;



    @Autowired
    public AdminController(AdminService adminService, AdminNotificationService adminNotificationService, EmailVerificactionService emailVerificactionService) {
        this.adminService = adminService;
        this.adminNotificationService = adminNotificationService;
        this.emailVerificactionService = emailVerificactionService;
    }

    @PostMapping("/add-user")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDetailsDTO userdetailsDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            System.out.println(userdetailsDTO.toString());
            adminService.registerAdmin(userdetailsDTO);

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

//
//    @GetMapping("/get-all-notification")
//    public ResponseEntity<List<NotificationDTO>> getAllNotifixation(){
//        try {
//            List<CompanyNotification> notifications = adminNotificationService.getRecentCompanyAllUserNotifications(5);
//            List<NotificationDTO> notificationDTOs = notifications.stream()
//                    .map(this::convertToNotificationDTO)
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(notificationDTOs);
//        }
//        catch (RuntimeException e){
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//
//        }
//
//    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Void> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserDetailsResponse request) {

        var currentTime = LocalDate.now();
        Role userRole = Role.fromString(request.getRole());
        System.out.println(userId);


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

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        try {
            System.out.println("U "+userId);

            adminService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            log.error("Error occurred while deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserFilterDTO>> searchUsers(@RequestBody UserFilterDTO filter) {
        try {
            List<UserFilterDTO> result=  adminService.findFilterUser(filter);
            return ResponseEntity.ok(result);

        }
        catch (Exception e){
            log.error("Error is ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @GetMapping("/summary")
    public ResponseEntity<CompanyDashbooksummaryDto> getSummaryDashbookSummary(){

        try {

            CompanyDashbooksummaryDto companyDashbooksummar = adminService.adminSummary();
            return ResponseEntity.ok(companyDashbooksummar);
        }
        catch (Exception e){
            log.error("Error is ", e);
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

    private NotificationDTO convertToNotificationDTO(CompanyNotification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getTargetAudience(),
                notification.getNotificationType().toString(),
                notification.getCreatedAt()
        );
    }







}
