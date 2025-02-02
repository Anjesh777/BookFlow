package com.BookFlow.bookflow.services;


import com.BookFlow.bookflow.dto.CompanyGrowthDTO;
import com.BookFlow.bookflow.dto.UserGrowthDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CompanyService {

    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRepo userRepo;


    @Transactional
    public long countServie(){
        return companyRepo.count();
    }

    public String getGrowthPercentage() {
        UserGrowthDTO growth = userRepo.getUserGrowth();
        return growth.getGrowthPercentage() + "%";
    }

    public String getCompanyGrowthPercentage() {
        CompanyGrowthDTO growth = companyRepo.getCompanyGrowth();
        return growth.getGrowthPercentage() + "%";
    }

    public List<Company> getAllCompanies() {
        return companyRepo.findByDateDesc();
    }
    public List<Company> get3Companies() {
        return companyRepo.findTop3Recent();
    }






}
