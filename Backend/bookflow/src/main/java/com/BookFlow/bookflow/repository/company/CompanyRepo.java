package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.dto.CompanyGrowthDTO;
import com.BookFlow.bookflow.model.Company;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CompanyRepo extends JpaRepository<Company, UUID> {

    @Query ("SELECT c FROM Company c WHERE c.company_email = :email")
    Optional<Company> findByCompanyEmail(@Param("email") String email);

    @Query("SELECT c.company_id FROM Company c WHERE c.company_email = :email")
    Optional<UUID> findCompanyIdByEmail(@Param("email") String email);

    @Query("SELECT c FROM Company c WHERE c.company_name = :name")
    Optional<Company> findByCompanyName(@Param("name") String name);



    @Modifying
    @Transactional
    @Query("UPDATE Company c SET " +
            "c.company_name = :companyName, " +
            "c.company_email = :email, " +
            "c.registration_number = :registrationNumber, "+
            "c.company_phone = :phone, " +
            "c.company_address = :address, " +
            "c.company_updatedAt =:ldate, " +
            "c.enabled = :isEnabled, " +
            "c.verified = :isVerified "+
            "WHERE c.company_id = :companyId")
    int updateCompany(
            @Param("companyId") UUID companyId,
            @Param("companyName") String companyName,
            @Param("registrationNumber") String registrationNumber,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("address") String address,
            @Param("ldate") LocalDate date,
            @Param("isEnabled") boolean isEnabled,
            @Param("isVerified") boolean isVerified
    );



    @Query(value = """
    WITH MonthlyCompanies AS (
        SELECT 
            DATE_TRUNC('month', company_created_at) as month,
            COUNT(*) as company_count
        FROM company 
        GROUP BY DATE_TRUNC('month', company_created_at)
    ),
    CurrentAndPrevious AS (
        SELECT 
            (SELECT company_count 
             FROM MonthlyCompanies 
             WHERE month = DATE_TRUNC('month', CURRENT_DATE)) as current_month_companies,
            (SELECT company_count 
             FROM MonthlyCompanies 
             WHERE month = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')) as prev_month_companies
    )
    SELECT 
        COALESCE(current_month_companies, 0) as currentMonthCompanies,
        CASE 
            WHEN COALESCE(prev_month_companies, 0) = 0 THEN 100.0
            ELSE ABS(ROUND(((current_month_companies - prev_month_companies)::float / prev_month_companies * 100)::numeric, 1))
        END as growthPercentage
    FROM CurrentAndPrevious
    """,
            nativeQuery = true)
    CompanyGrowthDTO getCompanyGrowth();

    @Query("SELECT c FROM Company c ORDER BY c.company_createdAt DESC")
    List<Company> findByDateDesc();

    @Query("SELECT c FROM Company c ORDER BY c.company_createdAt DESC LIMIT 3")
    List<Company> findTop3Recent();


    //--------------------------





}
