package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.ServiceRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service

public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ServiceRepo serviceRepo;

    @Transactional
    public long countServie(){
        return  userRepo.count();
    }

    private Company getCurrentUserCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        return currentUser.get().getCompany_id();
    }

    public List<Notification> getRecentUserNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userRepo.findRecentUserNotifications(getCurrentUserCompany(),pageable);
    }











}
