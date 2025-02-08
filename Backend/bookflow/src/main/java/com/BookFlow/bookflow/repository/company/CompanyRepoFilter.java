package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public interface CompanyRepoFilter extends JpaRepository<Company, UUID> {


    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByAllFilters(String search, Boolean verified, Boolean status,
                                   Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchDateRangeAndVerified(String search, Boolean verified,
                                                   Date fromDate, Date toDate);


    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchDateRangeAndStatus(String search, Boolean status,
                                                 Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByDateRangeVerifiedAndStatus(Boolean verified, Boolean status,
                                                   Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchVerifiedAndStatus(String search, Boolean verified, Boolean status);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchAndDateRange(String search, Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_verified = :verified AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByDateRangeAndVerified(Boolean verified, Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByDateRangeAndStatus(Boolean status, Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchAndVerified(String search, Boolean verified);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearchAndStatus(String search, Boolean status);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByVerifiedAndStatus(Boolean verified, Boolean status);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findBySearch(String search);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByDateRange(Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_verified = :verified " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByVerifiedStatus(Boolean verified);

    @Query(value = "SELECT * FROM company c WHERE " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByStatus(Boolean status);


    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationAllFilters(String search, Boolean verified, Boolean status,
                                               Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationDateRangeAndVerified(String search, Boolean verified,
                                                         Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_enable = :status AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationDateRangeAndStatus(String search, Boolean status,
                                                       Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified AND " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationVerifiedAndStatus(String search, Boolean verified, Boolean status);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.company_created_at >= :fromDate AND " +
            "c.company_created_at <= :toDate " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationAndDateRange(String search, Date fromDate, Date toDate);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_verified = :verified " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationAndVerified(String search, Boolean verified);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) AND " +
            "c.is_enable = :status " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistrationAndStatus(String search, Boolean status);

    @Query(value = "SELECT * FROM company c WHERE " +
            "LOWER(c.company_registration) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY c.company_created_at DESC", nativeQuery = true)
    List<Company> findByRegistration(String search);

}
