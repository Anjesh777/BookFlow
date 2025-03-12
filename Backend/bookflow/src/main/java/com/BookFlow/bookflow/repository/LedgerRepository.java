package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Ledger;
import com.BookFlow.bookflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger,String> {

    List<Ledger> findByCompanyID(Company company);
    List<Ledger> findByUserID(User user);

    @Query("SELECT l FROM Ledger l WHERE l.companyID = :company AND l.date BETWEEN :startDate AND :endDate ORDER BY l.date")
    List<Ledger> findByCompanyIDAndDateBetween(
            @Param("company") Company company,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT l FROM Ledger l WHERE l.userID = :user AND l.date BETWEEN :startDate AND :endDate ORDER BY l.date")
    List<Ledger> findByUserIDAndDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<Ledger> findByEntryIDAndCompanyID(String entryID, Company company);

    @Query("SELECT " +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE 0 END), 0) as totalCredits, " +
            "COALESCE(SUM(CASE WHEN l.type = 'debit' THEN l.amount ELSE 0 END), 0) as totalDebits, " +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE -l.amount END), 0) as balance " +
            "FROM Ledger l WHERE l.companyID = :company")
    Object[] getCompanySummary(@Param("company") Company company);

    @Query("SELECT NEW com.BookFlow.bookflow.dto.LedgerSummaryDTO(" +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN l.type = 'debit' THEN l.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE -l.amount END), 0)) " +
            "FROM Ledger l WHERE l.userID = :user")
    LedgerSummaryDTO getUserSummary(@Param("user") User user);

    @Query("SELECT l.balance FROM Ledger l WHERE l.userID = :user ORDER BY l.date DESC, l.entryID DESC LIMIT 1")
    Optional<BigDecimal> findLatestUserBalance(@Param("user") User user);

    @Query("SELECT l FROM Ledger l WHERE l.userID.user_id = :userId ORDER BY l.date DESC, l.entryID DESC")
    List<Ledger> findByUserIdOrderByDateDesc(@Param("userId") UUID userId);


    @Query("SELECT l FROM Ledger l WHERE l.companyID = :company AND " +
            "(LOWER(l.particulars) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.refrenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Ledger> searchLedgerEntries(
            @Param("company") Company company,
            @Param("searchTerm") String searchTerm);


    @Query("SELECT TO_CHAR(l.date, 'Month'), SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE -l.amount END) FROM Ledger l " +
            "GROUP BY TO_CHAR(l.date, 'Month')")
    List<Object[]> getMonthlyTransactionSummary();

    @Query(value = "SELECT EXTRACT(DAY FROM date) as day, SUM(CASE WHEN type = 'credit' THEN amount ELSE -amount END) as total_amount " +
            "FROM ledger " +
            "WHERE EXTRACT(MONTH FROM date) = :month AND EXTRACT(YEAR FROM date) = :year " +
            "AND company_id = :companyId " +
            "GROUP BY EXTRACT(DAY FROM date) " +
            "ORDER BY day",
            nativeQuery = true)
    List<Object[]> getDailyTransactionSummary(
            @Param("month") int month,
            @Param("year") int year,
            @Param("companyId") UUID companyId);

    @Query("SELECT COUNT(l) FROM Ledger l WHERE l.companyID.company_id = :companyId")
    int countUsersByLedger(@Param("companyId") UUID companyId);



}
