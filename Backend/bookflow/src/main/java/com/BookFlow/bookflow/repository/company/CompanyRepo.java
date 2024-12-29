package com.BookFlow.bookflow.repository.company;

import com.BookFlow.bookflow.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepo extends JpaRepository<Company, Long> {



}
