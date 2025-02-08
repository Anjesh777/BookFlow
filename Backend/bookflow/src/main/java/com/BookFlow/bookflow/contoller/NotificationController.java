package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.enums.NotificationType;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.services.CompanyService;
import com.BookFlow.bookflow.services.NotificationService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("api/v1/bookflow")
public class NotificationController {

    private final UserService userService;
    private final CompanyService companyService;
    private final NotificationService notificationService;

    public NotificationController(UserService userService, CompanyService companyService, NotificationService notificationService) {
        this.userService = userService;
        this.companyService = companyService;
        this.notificationService = notificationService;
    }

    @PostMapping("/notification")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO notificationDTO) {
        System.out.println("Received DTO: " + notificationDTO);

        try {
            Notification notification = new Notification();
            notification.setTitle(notificationDTO.getTitle());
            notification.setMessage(notificationDTO.getMessage());
            notification.setTargetAudience(notificationDTO.getTargetAudience());
            notification.setNotificationType(NotificationType.valueOf(notificationDTO.getNotificationType()));

            Notification savedNotification = notificationService.createNotification(notification);
            return ResponseEntity.ok(savedNotification);

        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-notification")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getRecentNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/notification/{id}")
    public ResponseEntity<?> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationDTO request
    ) {
        try {

            Notification notification = new Notification();
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setTargetAudience(request.getTargetAudience());
            notification.setNotificationType(NotificationType.valueOf(request.getNotificationType()));

            notificationService.updateNotification(id, notification);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error updating notification: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id){
        try {
            notificationService.deleteComment(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }



}
