package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service

public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public long countServie(){
        return  userRepo.count();
    }




}
