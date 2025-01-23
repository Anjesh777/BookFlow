package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepo extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.company.company_id = :companyId")
    void deleteByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT vt FROM VerificationToken vt WHERE vt.company.company_id = :companyId")
    Optional<VerificationToken> findByCompanyId(@Param("companyId") UUID companyId);


}
