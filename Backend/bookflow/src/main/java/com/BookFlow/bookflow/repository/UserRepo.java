package com.BookFlow.bookflow.repository;

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

////    @Query("SELECT u.userId FROM User u WHERE u.email = :email")
//    Optional<UUID> findUsersIdByEmail(@Param("email") String email);




}
