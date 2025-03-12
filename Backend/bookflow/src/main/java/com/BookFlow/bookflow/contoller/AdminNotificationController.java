package com.BookFlow.bookflow.contoller;


import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.enums.NotificationType;
import com.BookFlow.bookflow.model.CompanyNotification;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.services.AdminNotificationService;
import com.BookFlow.bookflow.services.CompanyService;
import com.BookFlow.bookflow.services.NotificationService;
import com.BookFlow.bookflow.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/v1/admin")
public class AdminNotificationController {


    private final UserService userService;
    private final CompanyService companyService;
    private final AdminNotificationService adminNotificationService;
private final NotificationService notificationService;

    public AdminNotificationController(UserService userService, CompanyService companyService, NotificationService notificationService, AdminNotificationService adminNotificationService, NotificationService notificationService1) {
        this.userService = userService;
        this.companyService = companyService;
        this.adminNotificationService = adminNotificationService;
        this.notificationService = notificationService1;
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


            log.info(String.valueOf(notification));

            Notification savedNotification = adminNotificationService.addAdminNotification(notification);
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
    public ResponseEntity<List<CompanyNotification>> getAllNotifications() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getRecentCompanyNotifications(10);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all-notification")
    public ResponseEntity<List<CompanyNotification>> getAll() {
        try {
            List<CompanyNotification> notifications = adminNotificationService.getAllCompanyNotification(10);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }








    @GetMapping("/get-adminNotication")
    public ResponseEntity<List<Notification>> getAllAdminNotifications() {
        try {
            List<Notification> notifications = adminNotificationService.getAllAdminCompanyNotification(10);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/notification/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody NotificationDTO request) {
        try {
            Notification notification = new Notification();
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setTargetAudience(request.getTargetAudience());
            notification.setNotificationType(NotificationType.valueOf(request.getNotificationType()));
            notificationService.updateUserNotification(id, notification);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error updating notification: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }





    @DeleteMapping("/notification/{id}")
    public ResponseEntity<?> deleteUserCMPNotification(@PathVariable Long id) {

        try {
            adminNotificationService.deleteComment(id);
            return  ResponseEntity.ok().build();
        }
        catch (Exception e){

            return ResponseEntity.internalServerError().build();

        }

    }








}
