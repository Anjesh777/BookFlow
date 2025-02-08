package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.enums.NotificationType;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Notification notification) {
        System.out.println("Before saving: " + notification); // Debug log
        Notification saved = notificationRepository.save(notification);
        System.out.println("After saving: " + saved); // Debug log
        return saved;
    }

    public List<Notification> getRecentNotifications() {
        return notificationRepository.findRecentNotifications(PageRequest.of(0, 5));
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void updateNotification(Long id,Notification notification){

        notificationRepository.updateNotification(
                id,
                notification.getTitle(),
                notification.getMessage(),
                notification.getTargetAudience(),
                notification.getNotificationType().toString()

        );

    }

    public void deleteComment(Long commentId) {
        notificationRepository.deleteComment(commentId);
    }





}
