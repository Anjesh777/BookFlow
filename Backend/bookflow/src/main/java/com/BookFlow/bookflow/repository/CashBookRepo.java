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

public interface CashBookRepo extends JpaRepository<CashBook, Long> {
    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<CashBook> searchByDescription(
            @Param("query") String query,
            @Param("companyId") Company companyId,
            Pageable pageable
    );

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

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.date = :date")
    Page<CashBook> findByDate(
            @Param("date") LocalDate date,
            @Param("companyId") Company companyId,
            Pageable pageable
    );

    @Query("SELECT t.balance FROM CashBook t WHERE t.company_id = :companyId ORDER BY t.id DESC LIMIT 1")
    Optional<BigDecimal> findLatestBalance(@Param("companyId") Company companyId);

    @Query("SELECT SUM(t.paymentAmount) FROM CashBook t WHERE t.company_id = :companyId AND t.isReimbursementPending = true")
    BigDecimal findTotalPendingReimbursements(
            @Param("companyId") Company companyId
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.date BETWEEN :startDate AND :endDate")
    Page<CashBook> findByDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("companyId") Company companyId,
            Pageable pageable
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

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.id < :id ORDER BY t.id DESC")
    Optional<CashBook> findTopByCompanyAndIdLessThan(
            @Param("companyId") Company companyId,
            @Param("id") Long id
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId ORDER BY t.id DESC")
    Optional<CashBook> findTopByCompanyOrderByIdDesc(
            @Param("companyId") Company companyId
    );

    @Query("SELECT t FROM CashBook t WHERE t.company_id = :companyId AND t.id >= :startId ORDER BY t.id ASC")
    List<CashBook> findTransactionsForRecalculation(
            @Param("companyId") Company companyId,
            @Param("startId") Long startId
    );
}