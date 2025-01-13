package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CompanyDTO;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegisterCompanyService {

    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegisterCompanyService(CompanyRepo companyRepo, UserRepo userRepo, PasswordEncoder passwordEncoder) {

        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerCompany(CompanyDTO companyDTO){



        Company company = new Company();
        company.setCompany_name(companyDTO.getCompanyName());
        company.setRegistration_number(companyDTO.getRegistrationNumber());
        company.setCompany_email(companyDTO.getCompanyEmail());
        company.setCompany_phone(companyDTO.getCompanyPhone());
        company.setCompany_address(companyDTO.getCompanyAddress());
//      company.setCompany_password(companyDTO.getCompanyPassword());
//      company.setSuper_admin(companyDTO.getSuper_admin());

        Company savedCompany=companyRepo.save(company);


        User user = new User();
        user.setCompany_id(savedCompany);
        user.setEmail(companyDTO.getCompanyEmail());
        user.setUsername(companyDTO.getCompanyName());
        user.setPassword(passwordEncoder.encode(companyDTO.getCompanyPassword()));
        user.setRole(Role.COMPANY_SUPERADMIN);
        userRepo.save(user);





    }


}
