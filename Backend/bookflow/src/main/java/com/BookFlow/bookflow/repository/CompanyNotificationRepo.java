package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.CompanyNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CompanyNotificationRepo extends JpaRepository<CompanyNotification,Long> {


    @Query("SELECT cn FROM CompanyNotification cn WHERE cn.company_id = :company ORDER BY cn.createdAt DESC")
    List<CompanyNotification> findRecentNotificationsByCompany(@Param("company") Company company, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE company_notification SET " +
            "title = CASE WHEN :title IS NULL THEN title ELSE :title END, " +
            "message = CASE WHEN :message IS NULL THEN message ELSE :message END, " +
            "target_audience = CASE WHEN :targetAudience IS NULL THEN target_audience ELSE :targetAudience END, " +
            "notification_type = CASE WHEN :notificationType IS NULL THEN notification_type ELSE CAST(:notificationType as VARCHAR) END " +
            "WHERE id = :id",
            nativeQuery = true)
    int updateCompanyNotification(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("message") String message,
            @Param("targetAudience") String targetAudience,
            @Param("notificationType") String notificationType
    );


    @Modifying
    @Transactional
    @Query("DELETE FROM CompanyNotification c WHERE c.id = :commentId")
    void deleteComment(@Param("commentId") Long commentId);




}
