package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.dto.UserGrowthDTO;
import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User,UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByRole(String username);


    //void deleteByCompanyId(UUID id);

    Optional<User> findByEmail(String email);
    Long countByEmail(String email);

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
}
