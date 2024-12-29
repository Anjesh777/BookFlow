package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.contoller.Company.RegisterCompanyController;
import com.BookFlow.bookflow.dto.Company.RegisterCompanyDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterCompanyService {

    private final CompanyRepo companyRepo;


    @Autowired
    public RegisterCompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Transactional
    public void registerCompany(RegisterCompanyDTO companyDTO){

        Company company = new Company();
        company.setCompany_name(companyDTO.getCompanyName());
        company.setRegistration_number(companyDTO.getRegistrationNumber());
        company.setCompany_email(companyDTO.getCompanyEmail());
        company.setCompany_phone(companyDTO.getCompanyPhone());
        company.setCompany_address(companyDTO.getCompanyAddress());
        company.setCompany_status(companyDTO.getCompanyStatus());
        company.setCompany_password(companyDTO.getCompanyPassword());
        company.setCompany_user(company.getCompany_user());

        companyRepo.save(company);

    }


}
