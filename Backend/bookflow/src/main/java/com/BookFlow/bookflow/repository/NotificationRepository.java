package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {


//    @Query("INSERT INTO Notification (title, message, targetAudience, notificationType, createdAt) " +
//            "VALUES (:title, :message, :targetAudience, :notificationType, :createdAt)")
//    @Modifying
//    @Transactional
//    void insertNotification(@Param("title") String title,
//                            @Param("message") String message,
//                            @Param("targetAudience") String targetAudience,
//                            @Param("notificationType") NotificationType notificationType,
//                            @Param("createdAt") LocalDateTime createdAt);




    @Modifying
    @Transactional
    @Query(value = "UPDATE notifications SET " +
            "title = CASE WHEN :title IS NULL THEN title ELSE :title END, " +
            "message = CASE WHEN :message IS NULL THEN message ELSE :message END, " +
            "target_audience = CASE WHEN :targetAudience IS NULL THEN target_audience ELSE :targetAudience END, " +
            "notification_type = CASE WHEN :notificationType IS NULL THEN notification_type ELSE CAST(:notificationType as VARCHAR) END " +
            "WHERE id = :id",
            nativeQuery = true)
    int updateNotification(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("message") String message,
            @Param("targetAudience") String targetAudience,
            @Param("notificationType") String notificationType
    );


    @Modifying
    @Transactional
    @Query("DELETE FROM Notification c WHERE c.id = :commentId")
    void deleteComment(@Param("commentId") Long commentId);


    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(Pageable pageable);


}
