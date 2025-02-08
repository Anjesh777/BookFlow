package com.BookFlow.bookflow.services;


import com.BookFlow.bookflow.dto.CompanyFilterDTO;
import com.BookFlow.bookflow.dto.CompanyGrowthDTO;
import com.BookFlow.bookflow.dto.UserGrowthDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepo;
import com.BookFlow.bookflow.repository.company.CompanyRepoFilter;
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
    private CompanyRepoFilter companyRepoFilter;
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

        boolean isRegistrationNumber = false;
        if (search != null) {
            try {
                Long.parseLong(search);
                isRegistrationNumber = true;
            } catch (NumberFormatException e) {
                isRegistrationNumber = false;
            }
        }

        boolean hasSearch = (search != null);
        boolean hasDateRange = (fromDate != null && toDate != null);
        boolean hasVerified = (verified != null);
        boolean hasStatus = (status != null);

        if (isRegistrationNumber) {
            return switch (getFilterCombination(hasSearch, hasDateRange, hasVerified, hasStatus)) {
                case "1111" -> companyRepoFilter.findByRegistrationAllFilters(search, verified, status, fromDate, toDate);
                case "1110" -> companyRepoFilter.findByRegistrationDateRangeAndVerified(search, verified, fromDate, toDate);
                case "1101" -> companyRepoFilter.findByRegistrationDateRangeAndStatus(search, status, fromDate, toDate);
                case "1011" -> companyRepoFilter.findByRegistrationVerifiedAndStatus(search, verified, status);
                case "1100" -> companyRepoFilter.findByRegistrationAndDateRange(search, fromDate, toDate);
                case "1010" -> companyRepoFilter.findByRegistrationAndVerified(search, verified);
                case "1001" -> companyRepoFilter.findByRegistrationAndStatus(search, status);
                case "1000" -> companyRepoFilter.findByRegistration(search);
                case "0111", "0110", "0101", "0011", "0100", "0010", "0001" -> handleNonSearchFilters(hasDateRange, hasVerified, hasStatus, verified, status, fromDate, toDate);
                default -> companyRepo.findAll();
            };
        } else {
            return switch (getFilterCombination(hasSearch, hasDateRange, hasVerified, hasStatus)) {
                case "1111" -> companyRepoFilter.findByAllFilters(search, verified, status, fromDate, toDate);
                case "1110" -> companyRepoFilter.findBySearchDateRangeAndVerified(search, verified, fromDate, toDate);
                case "1101" -> companyRepoFilter.findBySearchDateRangeAndStatus(search, status, fromDate, toDate);
                case "0111" -> companyRepoFilter.findByDateRangeVerifiedAndStatus(verified, status, fromDate, toDate);
                case "1011" -> companyRepoFilter.findBySearchVerifiedAndStatus(search, verified, status);
                case "1100" -> companyRepoFilter.findBySearchAndDateRange(search, fromDate, toDate);
                case "0110" -> companyRepoFilter.findByDateRangeAndVerified(verified, fromDate, toDate);
                case "0101" -> companyRepoFilter.findByDateRangeAndStatus(status, fromDate, toDate);
                case "1010" -> companyRepoFilter.findBySearchAndVerified(search, verified);
                case "1001" -> companyRepoFilter.findBySearchAndStatus(search, status);
                case "0011" -> companyRepoFilter.findByVerifiedAndStatus(verified, status);
                case "1000" -> companyRepoFilter.findBySearch(search);
                case "0100" -> companyRepoFilter.findByDateRange(fromDate, toDate);
                case "0010" -> companyRepoFilter.findByVerifiedStatus(verified);
                case "0001" -> companyRepoFilter.findByStatus(status);
                default -> companyRepo.findAll();
            };
        }
    }

    private List<Company> handleNonSearchFilters(boolean hasDateRange, boolean hasVerified, boolean hasStatus,
                                                 Boolean verified, Boolean status, Date fromDate, Date toDate) {
        String combination = getFilterCombination(false, hasDateRange, hasVerified, hasStatus);
        return switch (combination) {
            case "0111" -> companyRepoFilter.findByDateRangeVerifiedAndStatus(verified, status, fromDate, toDate);
            case "0110" -> companyRepoFilter.findByDateRangeAndVerified(verified, fromDate, toDate);
            case "0101" -> companyRepoFilter.findByDateRangeAndStatus(status, fromDate, toDate);
            case "0011" -> companyRepoFilter.findByVerifiedAndStatus(verified, status);
            case "0100" -> companyRepoFilter.findByDateRange(fromDate, toDate);
            case "0010" -> companyRepoFilter.findByVerifiedStatus(verified);
            case "0001" -> companyRepoFilter.findByStatus(status);
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
