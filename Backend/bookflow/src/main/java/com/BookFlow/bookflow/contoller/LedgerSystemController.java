package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.CashBookDTO;
import com.BookFlow.bookflow.dto.UserDTO;
import com.BookFlow.bookflow.dto.UserDetailsDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.services.LedgerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/ledger-system")
public class LedgerSystemController {

    private final LedgerService ledgerService;


    @Autowired
    private UserRepo userRepo;

    public LedgerSystemController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Autowired


    @GetMapping("/users")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<UserDetailsResponse> users = ledgerService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error while fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public List<User> getAllCmpUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if(currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        // Get the company ID from the user's company object
        Company userCompany = currentUser.get().getCompany_id();
        UUID companyId = userCompany.getCompany_id();


        return userRepo.findByCompanyId(companyId);
    }


}
