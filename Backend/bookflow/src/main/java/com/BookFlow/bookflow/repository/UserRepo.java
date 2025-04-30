package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.dto.UserGrowthDTO;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Notification;
import com.BookFlow.bookflow.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User,UUID> {

    @Query("SELECT NEW com.BookFlow.bookflow.dto.LedgerSummaryDTO(" +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN l.type = 'debit' THEN l.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN l.type = 'credit' THEN l.amount ELSE -l.amount END), 0)) " +
            "FROM Ledger l WHERE l.companyID = :company")
    LedgerSummaryDTO getCompanySummary(@Param("company") Company company);

    @Query("SELECT u.fullname FROM User u WHERE u.user_id = :userId")
    Optional<String> findFullnameById(@Param("userId") UUID userId);



    Optional<User> findByUsername(String username);


    Optional<User> findByEmail(String email);
    Long countByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.company_id.company_id = :companyId")
    List<User> findByCompanyId(@Param("companyId") UUID companyId);


    @Query("SELECT COUNT(u) FROM User u WHERE u.company_id.company_id = :companyId")
    int countUsersByCompanyId(@Param("companyId") UUID companyId);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.user_id = :userId AND u.mainuser = false")
    void deleteByUserId(@Param("userId") UUID userId);


    @Query(value = """
    WITH MonthlyUsers AS (
        SELECT 
            DATE_TRUNC('month', date) as month,
            COUNT(*) as user_count
        FROM users 
        GROUP BY DATE_TRUNC('month', date)
    ),
    CurrentAndPrevious AS (
        SELECT 
            (SELECT user_count 
             FROM MonthlyUsers 
             WHERE month = DATE_TRUNC('month', CURRENT_DATE)) as current_month_users,
            (SELECT user_count 
             FROM MonthlyUsers 
             WHERE month = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')) as prev_month_users
    )
    SELECT 
        COALESCE(current_month_users, 0) as currentMonthUsers,
        CASE 
            WHEN COALESCE(prev_month_users, 0) = 0 THEN 100.0
            ELSE ABS(ROUND(((current_month_users - prev_month_users)::float / prev_month_users * 100)::numeric, 1))
        END as growthPercentage
    FROM CurrentAndPrevious
    """,
            nativeQuery = true)
    UserGrowthDTO getUserGrowth();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET " +
            "u.fullname = :fullname, " +
            "u.email = :email, " +
            "u.phone = :phone, " +
            "u.username = :username, " +
            "u.role = :role, " +
            "u.is_enabled = :isEnabled, " +
            "u.update_at = :updateDate " +
            "WHERE u.user_id = :userId")
    int updateUser(
            @Param("userId") UUID userId,
            @Param("username") String username,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("fullname") String fullname,
            @Param("role") Role role,
            @Param("isEnabled") boolean isEnabled,
            @Param("updateDate") LocalDate updateDate
    );




    @Query("SELECT n FROM Notification n WHERE n.company_id = :company AND n.targetAudience = 'Users' or n.targetAudience = 'All Users' ORDER BY n.createdAt DESC")
    List<Notification> findRecentUserNotifications(@Param("company") Company company, Pageable pageable);



}
