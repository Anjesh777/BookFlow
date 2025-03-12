package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.CompanyNotification;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.services.AdminNotificationService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class CustomerService {


    private final UserService userService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    private UserRepo userRepo;

    public CustomerService(UserService userService, AdminNotificationService adminNotificationService) {
        this.userService = userService;
        this.adminNotificationService = adminNotificationService;
    }


    @GetMapping("/notification")
public ResponseEntity<List<Notification>> getUserNotificationPushbyAdmin(){
    try {
        List<Notification> notification = userService.getRecentUserNotifications(2);
        return ResponseEntity.ok(notification);
    }
    catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    }

    @GetMapping("/get-notification")
    public ResponseEntity<List<CompanyNotification>> getUserNotifications() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getRecentUserNotifications(1);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<CompanyNotification>> getAllNotifications() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getRecentCompanyAllUserNotifications(1);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }



}
