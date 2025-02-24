package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LedgerService {

    @Autowired
    private UserRepo userRepo;

    private Company getCurrentUserCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        return currentUser.get().getCompany_id();
    }

    public List<UserDetailsResponse> getAllUsers() {

        UUID companyId = getCurrentUserCompany().getCompany_id();
        List<User> users = userRepo.findByCompanyId(companyId);

        return users.stream()
                .map(user -> new UserDetailsResponse(
                        user.getUser_id(),
                        user.getFullname(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole().toString(),
                        user.getUsername()

                )).collect(Collectors.toList());
    }

}
