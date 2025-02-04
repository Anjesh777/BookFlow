package com.BookFlow.bookflow.services;


import com.BookFlow.bookflow.dto.CompanyFilterDTO;
import com.BookFlow.bookflow.dto.CompanyGrowthDTO;
import com.BookFlow.bookflow.dto.UserGrowthDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public void  updateCompanyDetails(UUID companyId,String companyname, String registrationNumber, String email, String phone, String address, boolean isEnable, boolean isVerified){

            companyRepo.updateCompany(companyId,companyname,registrationNumber,email,phone,address,LocalDate.now(),isEnable, isVerified);
    }

    public List<Company> searchCompanies(CompanyFilterDTO filter) {
        if (filter == null) {
            return companyRepo.findAll();
        }

        Date fromDate = null;
        Date toDate = null;
        if (filter.getDateRange() != null &&
                filter.getDateRange().getFromDate() != null &&
                filter.getDateRange().getToDate() != null) {
            fromDate = new java.sql.Date(filter.getDateRange().getFromDate().getTime());
            toDate = new java.sql.Date(filter.getDateRange().getToDate().getTime());
        }

        String search = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) ?
                filter.getSearch().trim() : null;
        Boolean verified = filter.getVerified();
        Boolean status = filter.getStatus();

        boolean hasSearch = (search != null);
        boolean hasDateRange = (fromDate != null && toDate != null);
        boolean hasVerified = (verified != null);
        boolean hasStatus = (status != null);

        return switch (getFilterCombination(hasSearch, hasDateRange, hasVerified, hasStatus)) {
            case "1111" -> companyRepo.findByAllFilters(search, verified, status, fromDate, toDate);
            case "1110" -> companyRepo.findBySearchDateRangeAndVerified(search, verified, fromDate, toDate);
            case "1101" -> companyRepo.findBySearchDateRangeAndStatus(search, status, fromDate, toDate);
            case "0111" -> companyRepo.findByDateRangeVerifiedAndStatus(verified, status, fromDate, toDate);
            case "1011" -> companyRepo.findBySearchVerifiedAndStatus(search, verified, status);
            case "1100" -> companyRepo.findBySearchAndDateRange(search, fromDate, toDate);
            case "0110" -> companyRepo.findByDateRangeAndVerified(verified, fromDate, toDate);
            case "0101" -> companyRepo.findByDateRangeAndStatus(status, fromDate, toDate);
            case "1010" -> companyRepo.findBySearchAndVerified(search, verified);
            case "1001" -> companyRepo.findBySearchAndStatus(search, status);
            case "0011" -> companyRepo.findByVerifiedAndStatus(verified, status);
            case "1000" -> companyRepo.findBySearch(search);
            case "0100" -> companyRepo.findByDateRange(fromDate, toDate);
            case "0010" -> companyRepo.findByVerifiedStatus(verified);
            case "0001" -> companyRepo.findByStatus(status);
            default -> companyRepo.findAll();
        };
    }

    private String getFilterCombination(boolean hasSearch, boolean hasDateRange, boolean hasVerified, boolean hasStatus) {
        return String.format("%d%d%d%d",
                hasSearch ? 1 : 0,
                hasDateRange ? 1 : 0,
                hasVerified ? 1 : 0,
                hasStatus ? 1 : 0
        );
    }





}
