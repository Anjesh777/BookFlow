package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.NotificationDTO;
import com.BookFlow.bookflow.enums.NotificationType;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.CompanyNotification;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.CompanyNotificationRepo;
import com.BookFlow.bookflow.repository.NotificationRepository;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final CompanyNotificationRepo notificationRepository;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NotificationRepository notificationUserRepository;
    @Autowired
    private UserContextUtil userContextUtil;

    public NotificationService(CompanyNotificationRepo notificationRepository) {
        this.notificationRepository = notificationRepository;
    }



    public CompanyNotification createNotification(CompanyNotification notification) {
        notification.setCompany_id(userContextUtil.getCurrentUserCompany());

        CompanyNotification saved = notificationRepository.save(notification);
        return saved;
    }



    public List<CompanyNotification> getRecentNotifications() {
        return notificationRepository.findRecentNotificationsByCompany(userContextUtil.getCurrentUserCompany(),PageRequest.of(0, 10));
    }

    public List<CompanyNotification> get3Notifications() {
        return notificationRepository.findRecentNotificationsByCompany(userContextUtil.getCurrentUserCompany(),PageRequest.of(0, 3));
    }

    public List<CompanyNotification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void updateNotification(Long id,CompanyNotification notification){

        notificationRepository.updateCompanyNotification(
                id,
                notification.getTitle(),
                notification.getMessage(),
                notification.getTargetAudience(),
                notification.getNotificationType().toString()

        );

    }

    public void updateUserNotification(Long id,Notification notificationDTO) {
        notificationUserRepository.updateNotification(
                id,
                notificationDTO.getTitle(),
                notificationDTO.getMessage(),
                notificationDTO.getTargetAudience(),
                String.valueOf(notificationDTO.getNotificationType())
        );
    }


    public void deleteComment(Long commentId) {
        notificationRepository.deleteComment(commentId);
    }





}
