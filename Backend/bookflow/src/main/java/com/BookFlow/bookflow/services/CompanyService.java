package com.BookFlow.bookflow.services;


import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CompanyService {

    @Autowired
    private CompanyRepo companyRepo;

    @Transactional
    public long countServie(){
        return companyRepo.count();
    }

}
