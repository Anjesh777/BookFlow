// Now, let's clean up the AdminNotificationService to focus only on notifications

package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.CompanyNotificationRepo;
import com.BookFlow.bookflow.repository.NotificationRepository;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdminNotificationService {

    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private CompanyNotificationRepo companyNotificationRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserContextUtil userContextUtil;

    public Notification addAdminNotification(Notification notification) {
        Company userCompany = userContextUtil.getCurrentUserCompany();

        Notification notification1 = new Notification();
        notification1.setId(notification.getId());
        notification1.setNotificationType(notification.getNotificationType());
        notification1.setMessage(notification.getMessage());
        notification1.setTitle(notification.getTitle());
        notification1.setCompany_id(userCompany);
        notification1.setCreatedAt(LocalDateTime.now());
        notification1.setTargetAudience(notification.getTargetAudience());

        return notificationRepo.save(notification1);
    }

    public List<CompanyNotification> getRecentCompanyNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return companyNotificationRepo.findForAdmins(userContextUtil.getCurrentUserCompany(), pageable);
    }

    public List<CompanyNotification> getRecentCompanyAllUserNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return companyNotificationRepo.findForAllUsers(userContextUtil.getCurrentUserCompany(), pageable);
    }

    public List<CompanyNotification> getRecentUserNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return companyNotificationRepo.findForUsers(userContextUtil.getCurrentUserCompany(), pageable);
    }

    public List<Notification> getAllAdminCompanyNotification(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return notificationRepo.findAllRecentUserNotifications(userContextUtil.getCurrentUserCompany(), pageable);
    }

    public List<CompanyNotification> getAllCompanyNotification(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return companyNotificationRepo.findAllByCompany(pageable);
    }


    public List<Notification> getAdminRecentNotification(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return notificationRepo.findRecentAdminNotifications(userContextUtil.getCurrentUserCompany(), pageable);
    }



    public void deleteComment(Long commentId) {
        notificationRepo.deleteComment(commentId);
    }



}