package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.CompanyNotificationRepo;
import com.BookFlow.bookflow.repository.ServiceRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AdminNotificationService {

    @Autowired
    private CompanyNotificationRepo companyNotificationRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ServiceRepo serviceRepo;


    private Company getCurrentUserCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        return currentUser.get().getCompany_id(); // or getCompany() if you've renamed the field
    }

    public CompanyNotification addAdminNotification(Notification notification) {
        Company userCompany = getCurrentUserCompany();

        CompanyNotification notification1 = new CompanyNotification();
        notification1.setId(notification.getId());
        notification1.setNotificationType(notification.getNotificationType());
        notification1.setMessage(notification.getMessage());
        notification1.setTitle(notification.getTitle());
        notification1.setCompany_id(userCompany);  // Ensure the setter expects a Company object
        notification1.setCreatedAt(LocalDateTime.now());
        notification1.setTargetAudience(notification.getTargetAudience());

        return companyNotificationRepo.save(notification1);
    }

    @Transactional
    public Services addServicce(Services services) {
        try {
            Company userCompany = getCurrentUserCompany();
            log.info("Current user company: {}", userCompany);

            services.setCompany_id(userCompany);
            Services savedService = serviceRepo.save(services);
            log.info("Service saved: {}", savedService);
            return savedService;
        } catch (Exception e) {
            log.error("Error saving service: ", e);
            throw e;
        }
    }




    public List<CompanyNotification> getRecentCompanyNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return companyNotificationRepo.findRecentNotificationsByCompany(getCurrentUserCompany(),pageable);
    }

    public void updateNotification(Long id,Notification notification){



        companyNotificationRepo.updateCompanyNotification(
                id,
                notification.getTitle(),
                notification.getMessage(),
                notification.getTargetAudience(),
                notification.getNotificationType().toString()

        );

    }

    public void deleteComment(Long commentId) {
        companyNotificationRepo.deleteComment(commentId);
    }



}
