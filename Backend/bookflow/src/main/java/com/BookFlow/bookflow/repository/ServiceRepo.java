package com.BookFlow.bookflow.repository;

import com.BookFlow.bookflow.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceRepo extends JpaRepository<Services,String> {



}
