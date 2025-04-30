package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.*;
import com.BookFlow.bookflow.repository.ServiceRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
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
    @Autowired
    private UserContextUtil userContextUtil;

    @Transactional
    public long countServie(){
        return  userRepo.count();
    }

    public List<Notification> getRecentUserNotifications(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userRepo.findRecentUserNotifications(userContextUtil.getCurrentUserCompany(),pageable);
    }











}
