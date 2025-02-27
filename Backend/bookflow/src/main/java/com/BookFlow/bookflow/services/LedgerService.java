package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.LedgerDTO;
import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Ledger;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.LedgerRepository;
import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LedgerService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LedgerRepository ledgerRepository;


    private Company getCurrentUserCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }

        return currentUser.get().getCompany_id();
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
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
                        user.getUsername(),
                        user.is_enabled()
                )).collect(Collectors.toList());
    }


    @Transactional
    public LedgerDTO addRecord(LedgerDTO ledgerDTO) {
        log.info("Adding new ledger record: {}", ledgerDTO);
        Company company = getCurrentUserCompany();
        User user = null;

        // Get user either from DTO or current user
        if (ledgerDTO.getUser_id() != null && !ledgerDTO.getUser_id().isEmpty()) {
            user = userRepo.findById(UUID.fromString(ledgerDTO.getUser_id()))
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + ledgerDTO.getUser_id()));
        } else {
            user = getCurrentUser();
        }

        BigDecimal previousBalance = ledgerRepository.findLatestUserBalance(user)
                .orElse(BigDecimal.ZERO);

        BigDecimal newBalance;
        if ("credit".equalsIgnoreCase(ledgerDTO.getType())) {
            newBalance = previousBalance.add(ledgerDTO.getAmount());
        } else {
            newBalance = previousBalance.subtract(ledgerDTO.getAmount());
        }

        Ledger ledger = mapToEntity(ledgerDTO, user);
        ledger.setCompanyID(company);
        ledger.setBalance(newBalance);

        Ledger savedLedger = ledgerRepository.save(ledger);
        log.info("Ledger record saved successfully with ID: {}", savedLedger.getEntryID());

        return mapToDTO(savedLedger);
    }

    
    public List<LedgerDTO> getLedgerEntriesByUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Company currentCompany = getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        List<Ledger> ledgerEntries = ledgerRepository.findByUserIdOrderByDateDesc(userId);

        return ledgerEntries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<LedgerDTO> getLedgerEntriesByUserAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Company currentCompany = getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        List<Ledger> ledgerEntries = ledgerRepository.findByUserIDAndDateBetween(user, startDate, endDate);

        return ledgerEntries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public LedgerSummaryDTO getUserLedgerSummary(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Verify the user belongs to the current company
        Company currentCompany = getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        Object[] summary = ledgerRepository.getUserSummary(user);

        if (summary == null || summary.length < 3) {
            return new LedgerSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal totalCredits = (BigDecimal) (summary[0] != null ? summary[0] : BigDecimal.ZERO);
        BigDecimal totalDebits = (BigDecimal) (summary[1] != null ? summary[1] : BigDecimal.ZERO);
        BigDecimal balance = (BigDecimal) (summary[2] != null ? summary[2] : BigDecimal.ZERO);

        return new LedgerSummaryDTO(totalCredits, totalDebits, balance);
    }

    /**
     * Get a specific ledger entry by ID
     */
    public Optional<LedgerDTO> getLedgerEntryById(String entryId) {
        Company company = getCurrentUserCompany();
        Optional<Ledger> ledgerEntry = ledgerRepository.findByEntryIDAndCompanyID(entryId, company);

        return ledgerEntry.map(this::mapToDTO);
    }

    /**
     * Delete a ledger entry
     * Note: In a real system, consider soft deletion or audit trail
     */
    @Transactional
    public boolean deleteLedgerEntry(String entryId) {
        Company company = getCurrentUserCompany();
        Optional<Ledger> ledgerEntry = ledgerRepository.findByEntryIDAndCompanyID(entryId, company);

        if (ledgerEntry.isPresent()) {
            ledgerRepository.delete(ledgerEntry.get());

            // Recalculate balances for all subsequent entries
            User user = ledgerEntry.get().getUserID();
            List<Ledger> userEntries = ledgerRepository.findByUserIdOrderByDateDesc(user.getUser_id());

            if (!userEntries.isEmpty()) {
                updateBalancesAfterDeletion(userEntries);
            }

            return true;
        }
        return false;
    }

    /**
     * Update balances for all entries after a deletion
     */
    private void updateBalancesAfterDeletion(List<Ledger> entries) {
        BigDecimal runningBalance = BigDecimal.ZERO;

        // Sort by date ascending
        entries.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        for (Ledger entry : entries) {
            if ("credit".equalsIgnoreCase(entry.getType())) {
                runningBalance = runningBalance.add(entry.getAmount());
            } else {
                runningBalance = runningBalance.subtract(entry.getAmount());
            }
            entry.setBalance(runningBalance);
            ledgerRepository.save(entry);
        }
    }

    /**
     * Search ledger entries
     */
    public List<LedgerDTO> searchLedgerEntries(String searchTerm) {
        Company company = getCurrentUserCompany();
        List<Ledger> results = ledgerRepository.searchLedgerEntries(company, searchTerm);

        return results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map entity to DTO
     */
    private LedgerDTO mapToDTO(Ledger ledger) {
        LedgerDTO dto = new LedgerDTO(
                ledger.getEntryID(),
                ledger.getDate(),
                ledger.getParticulars(),
                ledger.getAmount(),
                ledger.getType(),
                ledger.getRefrenceNumber(),
                ledger.getNote(),
                ledger.getUserID() != null ? ledger.getUserID().getUser_id().toString() : null
        );

        dto.setAmount(ledger.getBalance());

        return dto;
    }


    private Ledger mapToEntity(LedgerDTO dto, User user) {
        Ledger ledger = new Ledger();
        ledger.setEntryID(dto.getEntryId() != null && !dto.getEntryId().isEmpty() ?
                dto.getEntryId() :
                "LEDGER_" + UUID.randomUUID().toString().substring(0, 8));
        ledger.setDate(dto.getDate());
        ledger.setAmount(dto.getAmount());
        ledger.setNote(dto.getNote());
        ledger.setParticulars(dto.getParticulars());
        ledger.setType(dto.getType());
        ledger.setRefrenceNumber(dto.getReferenceNumber());
        ledger.setUserID(user);

        return ledger;
    }
}