package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.dto.CompanyGrowthDTO;
import com.BookFlow.bookflow.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CompanyRepo extends JpaRepository<Company, UUID> {

    @Query ("SELECT c FROM Company c WHERE c.company_email = :email")
    Optional<Company> findByCompanyEmail(@Param("email") String email);

    @Query("SELECT c.company_id FROM Company c WHERE c.company_email = :email")
    Optional<UUID> findCompanyIdByEmail(@Param("email") String email);

//    @Query("SELECT c FROM Company c WHERE c.is_verified = false AND c.company_createdAt < :expirationTime")
//    List<Company> findUnverifiedCompaniesBefore(@Param("expirationTime") Date expirationTime);

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


}
