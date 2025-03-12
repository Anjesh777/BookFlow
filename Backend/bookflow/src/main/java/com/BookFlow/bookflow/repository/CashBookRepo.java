package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.CashBook;
import com.BookFlow.bookflow.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashBookRepo extends JpaRepository<CashBook, Long> {

    @Query("SELECT TO_CHAR(c.date, 'Month'), SUM(c.receiptAmount) FROM CashBook c " +
            "GROUP BY TO_CHAR(c.date, 'Month')")
    List<Object[]> getMonthlyTransactionSummary();


    @Query(value = "SELECT EXTRACT(DAY FROM date) as day, SUM(receipt_amount - payment_amount) as total_amount " +
            "FROM cash_book " +
            "WHERE EXTRACT(MONTH FROM date) = :month AND EXTRACT(YEAR FROM date) = :year " +
            "AND company_id = :companyId " +
            "GROUP BY EXTRACT(DAY FROM date) " +
            "ORDER BY day", nativeQuery = true)
    List<Object[]> getDailyTransactionSummary(
            @Param("month") int month,
            @Param("year") int year,
            @Param("companyId") UUID companyId);

    @Query("SELECT COUNT(c) FROM CashBook c WHERE c.company_id.id = :companyId")
    int countUsersByCashbook(@Param("companyId") UUID companyId);





    @Query("SELECT t FROM CashBook t WHERE t.company_id = :company")
    Page<CashBook> findByCompany(@Param("company") Company company, Pageable pageable);

    // Case 2: Only text query provided (search by description or voucher number)
    @Query("SELECT t FROM CashBook t WHERE t.company_id = :company " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.voucherNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<CashBook> findByCompanyAndQuery(@Param("company") Company company,
                                         @Param("query") String query,
                                         Pageable pageable);


    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND t.date >= :fromDate AND t.date <= :toDate " +
            "ORDER BY t.date DESC")
    Page<CashBook> findByCompanyAndDateRange(
            @Param("companyId") Company companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);




    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.voucherNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND t.date BETWEEN :fromDate AND :toDate " +
            "ORDER BY t.date DESC")
    Page<CashBook> findByCompanyAndSearchQueryAndDateRange(
            @Param("companyId") Company companyId,
            @Param("query") String query,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);


    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.voucherNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY t.date DESC")
    Page<CashBook> findByCompanyAndSearchQuery(
            @Param("companyId") Company companyId,
            @Param("query") String query,
            Pageable pageable);

    @Query("SELECT SUM(t.receiptAmount) FROM CashBook t WHERE t.company_id = :companyId AND t.date = :today")
    BigDecimal findTotalReceiptsForToday(
            @Param("today") LocalDate today,
            @Param("companyId") Company companyId
    );

    @Query("SELECT SUM(t.paymentAmount) FROM CashBook t WHERE t.company_id = :companyId AND t.date = :today")
    BigDecimal findTotalPaymentsForToday(
            @Param("today") LocalDate today,
            @Param("companyId") Company companyId
    );

    @Query("SELECT t.balance FROM CashBook t WHERE t.company_id = :companyId ORDER BY t.id DESC LIMIT 1")
    Optional<BigDecimal> findLatestBalance(@Param("companyId") Company companyId);

    @Query("SELECT SUM(t.paymentAmount) FROM CashBook t WHERE t.company_id = :companyId AND t.isReimbursementPending = true")
    BigDecimal findTotalPendingReimbursements(
            @Param("companyId") Company companyId
    );


    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.voucherNumber = :voucherNumber")
    CashBook findByVoucherNumber(
            @Param("voucherNumber") String voucherNumber,
            @Param("companyId") Company companyId
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.id > :id ORDER BY t.id ASC")
    List<CashBook> findByCompanyAndIdGreaterThan(
            @Param("companyId") Company companyId,
            @Param("id") Long id
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.id < :id ORDER BY t.id DESC LIMIT 1")
    Optional<CashBook> findTopByCompanyAndIdLessThan(
            @Param("companyId") Company companyId,
            @Param("id") Long id
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId ORDER BY t.id DESC LIMIT 1")
    Optional<CashBook> findTopByCompanyOrderByIdDesc(
            @Param("companyId") Company companyId
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.id >= :startId ORDER BY t.id ASC")
    List<CashBook> findTransactionsForRecalculation(
            @Param("companyId") Company companyId,
            @Param("startId") Long startId
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.voucherNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND t.date BETWEEN :fromDate AND :toDate " +
            "ORDER BY t.date DESC")
    List<CashBook> findByCompanyAndSearchQueryAndDateRangeNoPage(
            @Param("companyId") Company companyId,
            @Param("query") String query,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.voucherNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY t.date DESC")
    List<CashBook> findByCompanyAndSearchQueryNoPage(
            @Param("companyId") Company companyId,
            @Param("query") String query);

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId " +
            "AND t.date BETWEEN :fromDate AND :toDate " +
            "ORDER BY t.date DESC")
    List<CashBook> findByCompanyAndDateRangeNoPage(
            @Param("companyId") Company companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId ORDER BY t.date DESC")
    List<CashBook> findByCompanyNoPage(@Param("companyId") Company companyId);


}