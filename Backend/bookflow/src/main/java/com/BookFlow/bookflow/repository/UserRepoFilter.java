package com.BookFlow.bookflow.repository;


import com.BookFlow.bookflow.enums.Role;
import com.BookFlow.bookflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepoFilter extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.company_id.company_id = :companyId")
    List<User> findAll(@Param("companyId") UUID companyId);

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND " +
            "(:search IS NULL OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.is_enabled = :status) AND " +
            "(:fromDate IS NULL OR u.date >= :fromDate) AND " +
            "(:toDate IS NULL OR u.date <= :toDate)")
    List<User> findByAllFilters(
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            @Param("role") Role role,
            @Param("status") Boolean status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> findBySearch(
            @Param("companyId") UUID companyId,
            @Param("search") String search
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND u.role = :role")
    List<User> findByRole(
            @Param("companyId") UUID companyId,
            @Param("role") Role role
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND u.is_enabled = :status")
    List<User> findByStatus(
            @Param("companyId") UUID companyId,
            @Param("status") Boolean status
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND " +
            "u.date BETWEEN :fromDate AND :toDate")
    List<User> findByDateRange(
            @Param("companyId") UUID companyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "u.role = :role")
    List<User> findBySearchAndRole(
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            @Param("role") Role role
    );

    @Query("SELECT u FROM User u WHERE " +
            "u.company_id.company_id = :companyId AND " +
            "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "u.is_enabled = :status")
    List<User> findBySearchAndStatus(
            @Param("companyId") UUID companyId,
            @Param("search") String search,
            @Param("status") Boolean status
    );

}
