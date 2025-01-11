package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface CompanyRepo extends JpaRepository<Company, Long> {



    @Query("SELECT c.company_id FROM Company c WHERE c.company_email = :email")
    Optional<UUID> findCompanyIdByEmail(@Param("email") String email);


}
