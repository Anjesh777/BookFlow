package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.LedgerDTO;
import com.BookFlow.bookflow.dto.LedgerSummaryDTO;
import com.BookFlow.bookflow.dto.UserDetailsResponse;
import com.BookFlow.bookflow.model.CashBook;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.Ledger;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.CashBookRepo;
import com.BookFlow.bookflow.repository.LedgerRepository;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.utils.classes.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LedgerService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CashBookRepo cashBookRepo;
    @Autowired
    private CashBookService cashBookService;
    @Autowired
    private LedgerRepository ledgerRepository;
    @Autowired
    private UserContextUtil userContextUtil;





    @Transactional
    public LedgerDTO updateLedgerEntry(String entryId, LedgerDTO updatedLedgerDTO) {
        Optional<Ledger> existingEntry = ledgerRepository.findById(entryId);

        if (existingEntry.isPresent()) {
            Ledger ledger = existingEntry.get();
            String previousType = ledger.getType(); // Store the previous type
            ledger.setAmount(updatedLedgerDTO.getAmount());
            ledger.setType(updatedLedgerDTO.getType());
            ledger.setParticulars(updatedLedgerDTO.getParticulars());
            ledger.setNote(updatedLedgerDTO.getNote());
            ledger.setRefrenceNumber(updatedLedgerDTO.getReferenceNumber());

            ledgerRepository.save(ledger);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            Optional<User> currentUser = userRepo.findByUsername(currentUsername);

            if (currentUser.isEmpty()) {
                throw new RuntimeException("Current user not found");
            }

            Company userCompany = currentUser.get().getCompany_id();

            CashBook cashBook = cashBookRepo.findByVoucherNumber(ledger.getRefrenceNumber(), userCompany);

            if (updatedLedgerDTO.getType().equalsIgnoreCase("credit")) {
                if (cashBook == null) {
                    // Create new CashBook entry
                    cashBook = new CashBook();
                    cashBook.setCompany_id(ledger.getCompanyID());
                    cashBook.setDate(ledger.getDate());
                    cashBook.setVoucherNumber(ledger.getRefrenceNumber());
                    cashBook.setDescription(ledger.getParticulars());
                    cashBook.setCategory("Ledger Credit");
                    cashBook.setReceiptAmount(ledger.getAmount());
                    cashBook.setPaymentAmount(BigDecimal.ZERO);

                    BigDecimal previousBalance = cashBookService.getCurrentBalance(ledger.getCompanyID());
                    BigDecimal newBalance = previousBalance.add(ledger.getAmount());
                    cashBook.setBalance(newBalance);
                } else {
                    BigDecimal difference = ledger.getAmount().subtract(cashBook.getReceiptAmount());
                    cashBook.setReceiptAmount(ledger.getAmount());
                    cashBook.setDescription(ledger.getParticulars());
                    cashBook.setBalance(cashBook.getBalance().add(difference));

                    cashBookService.recalculateBalances(cashBook, cashBook.getReceiptAmount().subtract(difference),
                            cashBook.getPaymentAmount());
                }
                cashBookRepo.save(cashBook);
            } else if (updatedLedgerDTO.getType().equalsIgnoreCase("debit") &&
                    cashBook != null) {
                cashBookRepo.delete(cashBook);
                cashBookService.recalculateBalancesAfterDelete(cashBook.getId(),
                        cashBook.getReceiptAmount(), cashBook.getPaymentAmount(), userCompany);
            }

            return mapToDTO(ledger);
        } else {
            throw new RuntimeException("Ledger entry not found");
        }
    }




    public List<UserDetailsResponse> getAllUsers() {
        UUID companyId = userContextUtil.getCurrentUserCompany().getCompany_id();
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
        Company company = userContextUtil.getCurrentUserCompany();
        User user = null;

        if (ledgerDTO.getUser_id() != null && !ledgerDTO.getUser_id().isEmpty()) {
            user = userRepo.findById(UUID.fromString(ledgerDTO.getUser_id()))
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + ledgerDTO.getUser_id()));
        } else {
            user = userContextUtil.getCurrentUser();
        }

        BigDecimal previousBalance = ledgerRepository.findLatestUserBalance(user)
                .orElse(BigDecimal.ZERO);

//        BigDecimal newBalance;
//        if ("credit".equalsIgnoreCase(ledgerDTO.getType())) {
//            newBalance = previousBalance.add(ledgerDTO.getAmount());
//        } else {
//            newBalance = previousBalance.subtract(ledgerDTO.getAmount());
//        }

        BigDecimal newBalance = previousBalance.add(ledgerDTO.getAmount());


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

        Company currentCompany = userContextUtil.getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        List<Ledger> ledgerEntries = ledgerRepository.findByUserIdOrderByDateDesc(userId);

        return ledgerEntries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<LedgerDTO> getLedgerEntriesByUserInverted(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Company currentCompany = userContextUtil.getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        List<Ledger> ledgerEntries = ledgerRepository.findByUserIdOrderByDateDesc(userId);

        return ledgerEntries.stream()
                .map(this::mapToDTOInverted)
                .collect(Collectors.toList());
    }


    public List<LedgerDTO> getLedgerEntriesByUserAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Company currentCompany = userContextUtil.getCurrentUserCompany();
        if (!user.getCompany_id().equals(currentCompany)) {
            throw new RuntimeException("User does not belong to the current company");
        }

        List<Ledger> ledgerEntries = ledgerRepository.findByUserIDAndDateBetween(user, startDate, endDate);

        return ledgerEntries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


//    public LedgerSummaryDTO getUserLedgerSummary(UUID userId) {
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//        Company currentCompany = getCurrentUserCompany();
//        if (!user.getCompany_id().equals(currentCompany)) {
//            throw new RuntimeException("User does not belong to the current company");
//        }
//
//        LedgerSummaryDTO summary = ledgerRepository.getUserSummary(user);
//        return summary != null ? summary : new LedgerSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
//    }

    public LedgerSummaryDTO getUserLedgerSummary(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Get total credits (positive)
        BigDecimal totalCredits = ledgerRepository.sumUserCredits(userId)
                .orElse(BigDecimal.ZERO);

        // Get total debits (positive)
        BigDecimal totalDebits = ledgerRepository.sumUserDebits(userId)
                .orElse(BigDecimal.ZERO);

        // Current balance is sum of all amounts
        BigDecimal currentBalance = ledgerRepository.sumAllUserAmounts(userId)
                .orElse(BigDecimal.ZERO);

        return new LedgerSummaryDTO(totalCredits, totalDebits, currentBalance);
    }

    public LedgerSummaryDTO getUserLedgerSummaryInverted(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        BigDecimal totalDebits = ledgerRepository.sumUserCredits(userId)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalCredits = ledgerRepository.sumUserDebits(userId)
                .orElse(BigDecimal.ZERO);

        BigDecimal currentBalance = ledgerRepository.sumAllUserAmounts(userId)
                .orElse(BigDecimal.ZERO);

        return new LedgerSummaryDTO(totalCredits, totalDebits, currentBalance);
    }

    public LedgerSummaryDTO getCompanyLedgerSummary() {
        Company company = userContextUtil.getCurrentUserCompany();
        Object[] summaryData = ledgerRepository.getCompanySummary(company);

        if (summaryData != null && summaryData.length > 0) {
            Object[] innerData = (Object[]) summaryData[0];

            if (innerData != null && innerData.length >= 3) {
                BigDecimal totalCredits = (BigDecimal) innerData[0];
                BigDecimal totalDebits = (BigDecimal) innerData[1];
                BigDecimal currentBalance = (BigDecimal) innerData[2];

                return new LedgerSummaryDTO(
                        totalCredits != null ? totalCredits : BigDecimal.ZERO,
                        totalDebits != null ? totalDebits : BigDecimal.ZERO,
                        currentBalance != null ? currentBalance : BigDecimal.ZERO
                );
            }
        }

        return new LedgerSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }



    public Optional<LedgerDTO> getLedgerEntryById(String entryId) {
        Company company = userContextUtil.getCurrentUserCompany();
        Optional<Ledger> ledgerEntry = ledgerRepository.findByEntryIDAndCompanyID(entryId, company);

        return ledgerEntry.map(this::mapToDTO);
    }


    @Transactional
    public boolean deleteLedgerEntry(String entryId) {
        Company company = userContextUtil.getCurrentUserCompany();
        Optional<Ledger> ledgerEntry = ledgerRepository.findByEntryIDAndCompanyID(entryId, company);

        if (ledgerEntry.isPresent()) {
            ledgerRepository.delete(ledgerEntry.get());

            User user = ledgerEntry.get().getUserID();
            List<Ledger> userEntries = ledgerRepository.findByUserIdOrderByDateDesc(user.getUser_id());

            if (!userEntries.isEmpty()) {
                updateBalancesAfterDeletion(userEntries);
            }

            return true;
        }
        return false;
    }


    private void updateBalancesAfterDeletion(List<Ledger> entries) {
        BigDecimal runningBalance = BigDecimal.ZERO;

        entries.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        for (Ledger entry : entries) {
//            if ("credit".equalsIgnoreCase(entry.getType())) {
//                runningBalance = runningBalance.add(entry.getAmount());
//            } else {
//                runningBalance = runningBalance.subtract(entry.getAmount());
//            }
//            entry.setBalance(runningBalance);
//            ledgerRepository.save(entry);

            runningBalance = runningBalance.add(entry.getAmount());
            entry.setBalance(runningBalance);
            ledgerRepository.save(entry);

        }
    }

    public Map<String, BigDecimal> getMonthlyTransactionSummary() {
        List<Object[]> results = ledgerRepository.getMonthlyTransactionSummary(userContextUtil.getCurrentUserCompany().getCompany_id());
        Map<String, BigDecimal> summary = new HashMap<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(month, totalAmount);
        }
        return summary;
    }

    public Map<String, BigDecimal> getDailyTransactionSummary(LocalDate month) {
        Company company = userContextUtil.getCurrentUserCompany();

        int extractedMonth = month.getMonthValue();
        int extractedYear = month.getYear();

        List<Object[]> results = ledgerRepository.getDailyTransactionSummary(
                extractedMonth, extractedYear, company.getCompany_id());

        Map<String, BigDecimal> summary = new HashMap<>();
        for (Object[] row : results) {
            String day = String.format("%02d", ((Number)row[0]).intValue());
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(day, totalAmount);
        }
        return summary;
    }




    public List<LedgerDTO> searchLedgerEntries(String searchTerm) {
        Company company = userContextUtil.getCurrentUserCompany();
        List<Ledger> results = ledgerRepository.searchLedgerEntries(company, searchTerm);
        return results.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


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

        dto.setBalance(ledger.getBalance());

        return dto;
    }

//    private LedgerDTO mapToDTOInverted(Ledger ledger) {
//        BigDecimal invertedAmount = ledger.getAmount().negate();
//        String invertedType = "credit".equals(ledger.getType()) ? "debit" : "credit";
//
//        LedgerDTO dto = new LedgerDTO(
//                ledger.getEntryID(),
//                ledger.getDate(),
//                ledger.getParticulars(),
//                invertedAmount, // Use inverted amount
//                invertedType,   // Use inverted type
//                ledger.getRefrenceNumber(),
//                ledger.getNote(),
//                ledger.getUserID() != null ? ledger.getUserID().getUser_id().toString() : null
//        );
//
//        dto.setBalance(ledger.getBalance());
//
//        return dto;
//    }



    public Map<String, BigDecimal> getMonthlyLedgerSummary() {
        User currentUser = userContextUtil.getCurrentUser();
        List<Object[]> results = ledgerRepository.getMonthlyUserLedgerSummary(currentUser);
        Map<String, BigDecimal> summary = new HashMap<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(month, totalAmount);
        }
        return summary;
    }

    private LedgerDTO mapToDTOInverted(Ledger ledger) {
        BigDecimal invertedAmount = ledger.getAmount(); // Keep the amount positive
        String invertedType = "credit".equals(ledger.getType()) ? "debit" : "credit";

        LedgerDTO dto = new LedgerDTO(
                ledger.getEntryID(),
                ledger.getDate(),
                ledger.getParticulars(),
                invertedAmount,
                invertedType,
                ledger.getRefrenceNumber(),
                ledger.getNote(),
                ledger.getUserID() != null ? ledger.getUserID().getUser_id().toString() : null
        );

        // The balance might need to be recalculated for the other user's perspective
        dto.setBalance(ledger.getBalance());

        return dto;
    }

    public Map<String, BigDecimal> getDailyLedgerSummary(LocalDate month) {
        User currentUser = userContextUtil.getCurrentUser();
        int extractedMonth = month.getMonthValue();
        int extractedYear = month.getYear();

        List<Object[]> results = ledgerRepository.getDailyUserLedgerSummary(
                extractedMonth,
                extractedYear,
                currentUser.getUser_id()
        );

        Map<String, BigDecimal> summary = new HashMap<>();
        for (Object[] row : results) {
            String day = String.format("%02d", ((Number)row[0]).intValue());
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(day, totalAmount);
        }
        return summary;
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

    public boolean existsByReferenceNumber(String referenceNumber) {
        Company company = userContextUtil.getCurrentUserCompany();
        return ledgerRepository.existsByRefrenceNumberAndCompanyID(referenceNumber, company);
    }

    @Transactional
    public void deleteByReferenceNumber(String referenceNumber) {
        Company company = userContextUtil.getCurrentUserCompany();
        ledgerRepository.deleteByRefrenceNumberAndCompanyID(referenceNumber, company);
    }


}