package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken,Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Query("DELETE FROM PasswordResetToken p WHERE p.user.user_id = :userId")
    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);

//    @Query("SELECT p FROM PasswordResetToken p WHERE p.user.user_id = :userId")
//    Optional<PasswordResetToken> findByUserId(@Param("userId") UUID userId);
}
