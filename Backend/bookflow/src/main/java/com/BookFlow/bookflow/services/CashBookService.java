package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.CashBookDTO;
import com.BookFlow.bookflow.dto.CashBookSummaryDTO;
import com.BookFlow.bookflow.model.CashBook;
import com.BookFlow.bookflow.model.Company;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.CashBookRepo;
import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CashBookService {

    @Autowired
    private CashBookRepo cashBookRepo;
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

    public Page<CashBookDTO> getAllTransactions(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );
        Page<CashBook> cashBooks = cashBookRepo.findAll(pageable);
        // Assuming you have a method to convert a CashBook entity to CashBookDTO:
        return cashBooks.map(this::mapToDTO);
    }


    @Transactional
    public CashBookDTO addTransaction(CashBookDTO transactionDTO) {
        Company company = getCurrentUserCompany();
        CashBook cashBook = mapToEntity(transactionDTO);

        BigDecimal previousBalance = getCurrentBalance(company);
        BigDecimal newBalance = previousBalance
                .add(cashBook.getReceiptAmount() != null ? cashBook.getReceiptAmount() : BigDecimal.ZERO)
                .subtract(cashBook.getPaymentAmount() != null ? cashBook.getPaymentAmount() : BigDecimal.ZERO);

        cashBook.setBalance(newBalance);
        cashBook.setCompany_id(company);

        CashBook savedTransaction = cashBookRepo.save(cashBook);
        return mapToDTO(savedTransaction);
    }

    private BigDecimal getCurrentBalance(Company company) {
        return cashBookRepo.findTopByCompanyOrderByIdDesc(company)
                .map(CashBook::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Optional<CashBookDTO> getTransactionById(Long id) {
        Company company = getCurrentUserCompany();
        return cashBookRepo.findById(id)
                .filter(t -> t.getCompany_id().equals(company))
                .map(this::mapToDTO);
    }

    @Transactional
    public CashBookDTO updateTransaction(Long id, CashBookDTO transactionDTO) {
        Company company = getCurrentUserCompany();
        CashBook existingTransaction = cashBookRepo.findById(id)
                .filter(t -> t.getCompany_id().equals(company))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        BigDecimal oldReceiptAmount = existingTransaction.getReceiptAmount();
        BigDecimal oldPaymentAmount = existingTransaction.getPaymentAmount();

        existingTransaction.setDate(transactionDTO.getDate());
        existingTransaction.setVoucherNumber(transactionDTO.getVoucherNumber());
        existingTransaction.setDescription(transactionDTO.getDescription());
        existingTransaction.setCategory(transactionDTO.getCategory());
        existingTransaction.setReceiptAmount(transactionDTO.getReceiptAmount());
        existingTransaction.setPaymentAmount(transactionDTO.getPaymentAmount());
        existingTransaction.setReimbursementPending(transactionDTO.isReimbursementPending());

        recalculateBalances(existingTransaction, oldReceiptAmount, oldPaymentAmount);

        CashBook updatedTransaction = cashBookRepo.save(existingTransaction);
        return mapToDTO(updatedTransaction);
    }

    public Page<CashBookDTO> searchTransactions(String query, int page, int size) {
        Company company = getCurrentUserCompany();
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return cashBookRepo.searchByDescription(query, company, pageable).map(this::mapToDTO);
    }

    public Page<CashBookDTO> getTransactionsByDate(LocalDate date, int page, int size) {
        Company company = getCurrentUserCompany();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return cashBookRepo.findByDate(date, company, pageable).map(this::mapToDTO);
    }

    public CashBookSummaryDTO getTransactionSummary() {
        Company company = getCurrentUserCompany();
        CashBookSummaryDTO summary = new CashBookSummaryDTO();

        // Get current balance safely
        BigDecimal currentBalance = cashBookRepo.findLatestBalance(company)
                .orElse(BigDecimal.ZERO);
        summary.setCurrentBalance(currentBalance);

        // Get today's totals
        LocalDate today = LocalDate.now();
        BigDecimal receiptsToday = cashBookRepo.findTotalReceiptsForToday(today, company);
        BigDecimal paymentsToday = cashBookRepo.findTotalPaymentsForToday(today, company);

        summary.setTotalReceiptsToday(receiptsToday);
        summary.setTotalPaymentsToday(paymentsToday);

        // Get pending reimbursements
        BigDecimal pendingReimbursements = cashBookRepo.findTotalPendingReimbursements(company);
        summary.setPendingReimbursements(pendingReimbursements);

        return summary;
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Company company = getCurrentUserCompany();
        CashBook transaction = cashBookRepo.findById(id)
                .filter(t -> t.getCompany_id().equals(company))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        BigDecimal deletedTransactionBalance = transaction.getBalance();
        cashBookRepo.deleteById(id);

        List<CashBook> subsequentTransactions = cashBookRepo.findByCompanyAndIdGreaterThan(company, id);

        if (subsequentTransactions.isEmpty()) {
            return;
        }

        BigDecimal previousBalance = cashBookRepo.findTopByCompanyAndIdLessThan(company, id)
                .map(CashBook::getBalance)
                .orElse(BigDecimal.ZERO);

        BigDecimal runningBalance = previousBalance;
        for (CashBook t : subsequentTransactions) {
            runningBalance = runningBalance
                    .add(t.getReceiptAmount() != null ? t.getReceiptAmount() : BigDecimal.ZERO)
                    .subtract(t.getPaymentAmount() != null ? t.getPaymentAmount() : BigDecimal.ZERO);
            t.setBalance(runningBalance);
        }

        cashBookRepo.saveAll(subsequentTransactions);
    }

    private void recalculateBalances(CashBook updatedTransaction,
                                     BigDecimal oldReceiptAmount,
                                     BigDecimal oldPaymentAmount) {
        Company company = getCurrentUserCompany();
        BigDecimal netChange = updatedTransaction.getReceiptAmount()
                .subtract(oldReceiptAmount)
                .subtract(updatedTransaction.getPaymentAmount())
                .add(oldPaymentAmount);

        if (netChange.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        List<CashBook> subsequentTransactions = cashBookRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() >= updatedTransaction.getId())
                .toList();

        BigDecimal runningBalance = updatedTransaction.getBalance();

        for (CashBook t : subsequentTransactions) {
            if (t.getId().equals(updatedTransaction.getId())) {
                continue;
            }

            runningBalance = runningBalance
                    .add(t.getReceiptAmount())
                    .subtract(t.getPaymentAmount());

            t.setBalance(runningBalance);
            cashBookRepo.save(t);
        }
    }

    private void recalculateBalancesAfterDelete(Long deletedId,
                                                BigDecimal deletedReceiptAmount,
                                                BigDecimal deletedPaymentAmount,
                                                Company company) {
        List<CashBook> subsequentTransactions = cashBookRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() > deletedId)
                .toList();

        if (subsequentTransactions.isEmpty()) {
            return;
        }

        Optional<CashBook> previousTransaction = cashBookRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .filter(t -> t.getCompany_id().equals(company))
                .filter(t -> t.getId() < deletedId)
                .findFirst();

        BigDecimal previousBalance = previousTransaction.map(CashBook::getBalance).orElse(BigDecimal.ZERO);

        // Corrected adjustment calculation
        BigDecimal adjustment = deletedReceiptAmount.subtract(deletedPaymentAmount);
        BigDecimal runningBalance = previousBalance.subtract(adjustment);

        for (CashBook t : subsequentTransactions) {
            runningBalance = runningBalance
                    .add(t.getReceiptAmount())
                    .subtract(t.getPaymentAmount());

            t.setBalance(runningBalance);
            cashBookRepo.save(t);
        }
    }




    private CashBookDTO mapToDTO(CashBook cashbook) {
        return new CashBookDTO(
                cashbook.getId(),
                cashbook.getDate(),
                cashbook.getVoucherNumber(),
                cashbook.getDescription(),
                cashbook.getCategory(),
                cashbook.getReceiptAmount(),
                cashbook.getPaymentAmount(),
                cashbook.getBalance(),
                cashbook.isReimbursementPending()
        );
    }

    private CashBook mapToEntity(CashBookDTO dto) {
        CashBook cashBook = new CashBook();
        cashBook.setId(dto.getId());
        cashBook.setDate(dto.getDate());
        cashBook.setVoucherNumber(dto.getVoucherNumber());
        cashBook.setDescription(dto.getDescription());
        cashBook.setCategory(dto.getCategory());
        cashBook.setReceiptAmount(dto.getReceiptAmount());
        cashBook.setPaymentAmount(dto.getPaymentAmount());
        cashBook.setBalance(dto.getBalance());
        cashBook.setReimbursementPending(dto.isReimbursementPending());
        return cashBook;
    }


}
