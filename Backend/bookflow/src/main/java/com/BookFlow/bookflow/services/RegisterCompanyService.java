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


@Service
public class RegisterCompanyService {

    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public void registerCompany(CompanyDTO companyDTO){

        Company company = companyRepo.findByCompanyEmail(companyDTO.getCompanyEmail())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setCompany_name(companyDTO.getCompanyName());
                    newCompany.setRegistration_number(companyDTO.getRegistrationNumber());
                    newCompany.setCompany_email(companyDTO.getCompanyEmail());
                    newCompany.setCompany_phone(companyDTO.getCompanyPhone());
                    newCompany.setCompany_address(companyDTO.getCompanyAddress());
                    return companyRepo.save(newCompany);
                });
        User user = new User();
        user.setCompany_id(company);
        user.setEmail(companyDTO.getCompanyEmail());
        user.setUsername(companyDTO.getCompanyName());
        user.setPassword(passwordEncoder.encode(companyDTO.getCompanyPassword()));
        user.setRole(Role.COMPANY_ADMIN);
        user.setMainuser(true);
        user.setPhone(companyDTO.getCompanyPhone());
        user.set_enabled(true);

        userRepo.save(user);
    }
}
